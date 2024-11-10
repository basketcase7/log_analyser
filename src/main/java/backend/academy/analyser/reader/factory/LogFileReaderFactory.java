package backend.academy.analyser.reader.factory;

import backend.academy.analyser.reader.LogFileReader;
import backend.academy.analyser.stats.StatsHandler;
import java.time.LocalDateTime;

/**
 * Фабрика для создания экземпляра LogFileReader
 */
public interface LogFileReaderFactory {
    LogFileReader createLogFileReader(StatsHandler statsHandler, LocalDateTime fromTime, LocalDateTime toTime,
        String filterField, String filterValue);
}
