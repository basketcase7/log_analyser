package backend.academy.analyser.reader;

import backend.academy.analyser.LogsFilter;
import backend.academy.analyser.parser.LogParser;
import backend.academy.analyser.stats.StatsHandler;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;

/**
 * Класс, выполняющий логику чтения и обработку файлов с логами при передаче в виде аргумента локального пути
 */
@Slf4j
public class LocalFileReader implements LogFileReader {

    private final LogParser logParser;
    private final StatsHandler statsHandler;
    private final LocalDateTime fromDateTime;
    private final LocalDateTime toDateTime;
    private final LogsFilter logsFilter;

    public LocalFileReader(
        StatsHandler statsHandler,
        LocalDateTime fromDateTime,
        LocalDateTime toDateTime,
        String filterField,
        String filterValue
    ) {
        this.logParser = new LogParser();
        this.statsHandler = statsHandler;
        this.fromDateTime = fromDateTime;
        this.toDateTime = toDateTime;
        this.logsFilter = new LogsFilter(filterField, filterValue);
    }

    /**
     * Метод для поиска необходимых файлов
     *
     * @param path Путь, переданный пользователем
     */
    @SuppressFBWarnings(value = "PATH_TRAVERSAL_IN")
    @Override
    public void read(String path) {
        if (path.contains("*")) {
            processWildcardPath(path);
        } else {
            Path logFilePath = Paths.get(path);
            if (Files.isDirectory(logFilePath)) {
                try (Stream<Path> paths = Files.list(logFilePath)) {
                    paths.filter(Files::isRegularFile)
                        .forEach(this::processFile);
                } catch (IOException e) {
                    log.error("Error reading directory: {}", logFilePath);
                }
            } else if (Files.isRegularFile(logFilePath)) {
                processFile(logFilePath);
            } else {
                log.error("Error reading path: {}", logFilePath);
            }
        }
    }

    /**
     * Метод для потокового чтения файла и сбора статистики
     *
     * @param logFilePath Путь к файлу, который необходимо обработать
     */
    public void processFile(Path logFilePath) {

        try (BufferedReader bufferedReader = Files.newBufferedReader(logFilePath)) {
            bufferedReader.lines()
                .map(logParser::parse)
                .filter(l -> l != null && logsFilter.filter(l))
                .filter(l -> logsFilter.checkFromToDate(fromDateTime, toDateTime, l.timeLocal()))
                .forEach(statsHandler::updateStats);

        } catch (IOException e) {
            log.error("Error reading file: {}", logFilePath);
        }
    }

    /**
     * Метод для поиска файлов по шаблону
     *
     * @param path Путь, переданный пользователем
     */
    @SuppressFBWarnings(value = "PATH_TRAVERSAL_IN")
    private void processWildcardPath(String path) {
        String globPattern = path.replace('\\', '/');
        PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:" + globPattern);

        String baseDirPath = getBaseDir(globPattern);
        Path baseDir = Paths.get(baseDirPath);

        try (Stream<Path> paths = Files.walk(baseDir)) {
            paths.filter(Files::isRegularFile)
                .filter(matcher::matches)
                .forEach(this::processFile);
        } catch (IOException e) {
            log.error("Error processing wildcard path: {}", path);
        }
    }

    private String getBaseDir(String globPattern) {
        int firstWildcard = Math.min(
            globPattern.contains("*") ? globPattern.indexOf('*') : globPattern.length(),
            globPattern.contains("?") ? globPattern.indexOf('?') : globPattern.length()
        );

        return firstWildcard > 0 && globPattern.lastIndexOf('/') > 0
            ? globPattern.substring(0, globPattern.lastIndexOf('/', firstWildcard))
            : ".";
    }

}

