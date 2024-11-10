package backend.academy.analyser.reader.factory;

import backend.academy.analyser.reader.LocalFileReader;
import backend.academy.analyser.reader.LogFileReader;
import backend.academy.analyser.stats.StatsHandler;
import java.time.LocalDateTime;

/**
 * Фабрика для создания экземпляра LocalFileReader
 */
public class LocalFileReaderFactory implements LogFileReaderFactory {

    @Override
    public LogFileReader createLogFileReader(
        StatsHandler statsHandler, LocalDateTime fromTime, LocalDateTime toTime,
        String filterField, String filterValue
    ) {
        return new LocalFileReader(statsHandler, fromTime, toTime, filterField, filterValue);
    }

}
