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

@Slf4j
@RequiredArgsConstructor
public class LogAnalyserHandler {

    private final String[] args;

    private final static String PATH_TO_SAVE =
        "output.%s";

    private final static String ADOC_STRING = "adoc";
    private final static String MARKDOWN_STRING = "markdown";

    public void handle() {
        LogFileReaderFactory logFileReaderFactory;

        LocalDateTime fromDate = LocalDateTime.MIN;
        LocalDateTime toDate = LocalDateTime.MAX;

        String format;

        ArgsParser argsParser = new ArgsParser();
        try {
            argsParser.handleArgsParser(args);
        } catch (ParameterException e) {
            log.error("Argument parsing exception: {}", e.getMessage());
        }

        CommandLineArgs cmgArgs = argsParser.cmdArgs();

        String filterField = cmgArgs.filterField() != null ? cmgArgs.filterField() : "";
        String filterValue = cmgArgs.filterValue() != null ? cmgArgs.filterValue() : "";

        if (cmgArgs.filterField() != null && cmgArgs.filterValue() != null) {
            filterField = cmgArgs.filterField();
            filterValue = cmgArgs.filterValue();
        }
        filterValidate(filterField, filterValue);

        if (isUrl(cmgArgs.filePath())) {
            logFileReaderFactory = new UrlLogFileReaderFactory();
        } else {
            logFileReaderFactory = new LocalFileReaderFactory();
        }

        if (Objects.isNull(cmgArgs.outFormat())) {
            format = MARKDOWN_STRING;
        } else {
            format = cmgArgs.outFormat();
        }

        StatsHandler statsHandler = new StatsHandler(cmgArgs.filePath(), fromDate, toDate);

        LogFileReader logFileReader =
            logFileReaderFactory.createLogFileReader(statsHandler, fromDate, toDate, filterField, filterValue);
        logFileReader.read(cmgArgs.filePath());

        statsHandler.countStats();

        String outputStats = "";
        if (MARKDOWN_STRING.equals(format)) {
            outputStats = MarkdownMapper.mapStatsToMarkdownString(statsHandler);
        } else if (ADOC_STRING.equals(format)) {
            outputStats = AdocMapper.mapStatsToAdocString(statsHandler);
        }

        saveToFile(String.format(PATH_TO_SAVE, format), outputStats);
    }

    private boolean isUrl(String path) {
        return path.startsWith("http://") || path.startsWith("https://") || path.startsWith("ftp://");
    }

    private void saveToFile(String filepath, String markdownStats) {
        Path path = Path.of(filepath);

        try {
            Files.writeString(path, markdownStats, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            log.error("Output error: {}", e.getMessage());
        }
    }

    private void filterValidate(String filterField, String filterValue) {
        if (filterField.isEmpty() && !filterValue.isEmpty()) {
            throw new ParameterException("Filter field must be not empty");
        }
        if (filterValue.isEmpty() && !filterField.isEmpty()) {
            throw new ParameterException("Filter value must be not empty");
        }
    }
}

