package backend.academy.analyser;

import backend.academy.analyser.argument.ArgsParser;
import backend.academy.analyser.argument.CommandLineArgs;
import backend.academy.analyser.mapper.AdocMapper;
import backend.academy.analyser.mapper.MarkdownMapper;
import backend.academy.analyser.reader.LogFileReader;
import backend.academy.analyser.reader.factory.LocalFileReaderFactory;
import backend.academy.analyser.reader.factory.LogFileReaderFactory;
import backend.academy.analyser.reader.factory.UrlLogFileReaderFactory;
import backend.academy.analyser.stats.StatsHandler;
import com.beust.jcommander.ParameterException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Основной класс для работы программы, вызывает основные методы, необходимые для запуска
 */
@Slf4j
@RequiredArgsConstructor
public class LogAnalyserHandler {

    private final String[] args;

    private final static String PATH_TO_SAVE = "output.%s";

    private final static String ADOC_STRING = "adoc";
    private final static String MARKDOWN_STRING = "markdown";

    private LogFileReaderFactory logFileReaderFactory;
    private LocalDateTime fromDate;
    private LocalDateTime toDate;
    private String filterField;
    private String filterValue;

    /**
     * Поочередно вызывает методы для работы программы
     */
    public void handle() {

        CommandLineArgs cmgArgs = getArgs();

        setFilter(cmgArgs.filterField(), cmgArgs.filterValue());
        filterValidate(filterField, filterValue);
        createLogFileReaderFactory(cmgArgs.filePath());
        setTime(cmgArgs.fromTime(), cmgArgs.toTime());
        String format = setFormat(cmgArgs.outFormat());

        StatsHandler statsHandler = new StatsHandler(cmgArgs.filePath(), fromDate, toDate);

        LogFileReader logFileReader =
            logFileReaderFactory.createLogFileReader(statsHandler, fromDate, toDate, filterField, filterValue);
        logFileReader.read(cmgArgs.filePath());

        statsHandler.countStats();

        String outputStats = mapOutputStats(format, statsHandler);
        saveToFile(String.format(PATH_TO_SAVE, format), outputStats);
    }

    /**
     * Метод для вызова метода форматирования статистики в строку в зависимости от выбранного формата
     *
     * @param format Выбранный формат
     * @param statsHandler Объект с собранной статистикой
     * @return Строка с отформатированной статистикой
     */
    private String mapOutputStats(String format, StatsHandler statsHandler) {
        String outputStats;
        if (MARKDOWN_STRING.equals(format)) {
            outputStats = MarkdownMapper.mapStatsToMarkdownString(statsHandler);
        } else {
            outputStats = AdocMapper.mapStatsToAdocString(statsHandler);
        }
        return outputStats;
    }

    /**
     * Метод для установления определенного формата вывода
     *
     * @param format Выбранный пользователем формат
     * @return Формат с обработкой случая без выбора пользователя
     */
    private String setFormat(String format) {
        return Objects.isNull(format) ? MARKDOWN_STRING : format;
    }

    /**
     * Метода для установления фильтра
     *
     * @param argFilterField Выбранное поле фильтра
     * @param argFilterValue Выбранное значение фильтра
     */
    private void setFilter(String argFilterField, String argFilterValue) {
        filterField = Objects.requireNonNullElse(argFilterField, "");
        filterValue = Objects.requireNonNullElse(argFilterValue, "");
    }

    /**
     * Метод для установления начального и конечного времени
     *
     * @param from Выбранное начальное время
     * @param to Выбранное конечное время
     */
    private void setTime(LocalDateTime from, LocalDateTime to) {
        fromDate = Objects.requireNonNullElse(from, LocalDateTime.MIN);
        toDate = Objects.requireNonNullElse(to, LocalDateTime.MAX);
    }

    /**
     * Метод для выбора создания конкретной фабрики ридера в зависимости от пути к логам
     *
     * @param path Путь к логам
     */
    private void createLogFileReaderFactory(String path) {
        if (isUrl(path)) {
            logFileReaderFactory = new UrlLogFileReaderFactory();
        } else {
            logFileReaderFactory = new LocalFileReaderFactory();
        }
    }

    /**
     * Метода для получения аргументов из командной строки
     *
     * @return Набор элементов командной строки
     */
    private CommandLineArgs getArgs() {
        ArgsParser argsParser = new ArgsParser();
        try {
            argsParser.handleArgsParser(args);
        } catch (ParameterException e) {
            log.error("Argument parsing exception: {}", e.getMessage());
        }

        return argsParser.cmdArgs();
    }

    /**
     * Валидация, является ли строка URL-ом
     *
     * @param path Валидируемая строка
     * @return Результат валидации
     */
    private boolean isUrl(String path) {
        return path.startsWith("http://") || path.startsWith("https://") || path.startsWith("ftp://");
    }

    /**
     * Метод для сохранения собранной статистики в файл
     *
     * @param filepath    Путь, куда будет сохранен файл
     * @param stringStats Собранная статистика в виде строки
     */
    private void saveToFile(String filepath, String stringStats) {
        Path path = Path.of(filepath);

        try {
            Files.writeString(path, stringStats, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            log.error("Output error: {}", e.getMessage());
        }
    }

    /**
     * Валидация наличия второго компонента фильтра при наличии первого
     *
     * @param filterField Поле для фильтрации
     * @param filterValue Значение для фильтрации
     */
    private void filterValidate(String filterField, String filterValue) {
        boolean isFieldEmpty = filterField.isEmpty();
        boolean isValueEmpty = filterValue.isEmpty();
        if (isFieldEmpty && !isValueEmpty) {
            throw new ParameterException("Filter field must be not empty");
        }
        if (isValueEmpty && !isFieldEmpty) {
            throw new ParameterException("Filter value must be not empty");
        }
    }
}

