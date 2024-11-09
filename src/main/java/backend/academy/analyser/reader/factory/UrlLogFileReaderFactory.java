package backend.academy.analyser.reader.factory;

import backend.academy.analyser.reader.LogFileReader;
import backend.academy.analyser.reader.UrlLogFileReader;
import backend.academy.analyser.stats.StatsHandler;
import java.time.LocalDateTime;

public class UrlLogFileReaderFactory implements LogFileReaderFactory {

    @Override
    public LogFileReader createLogFileReader(
        StatsHandler statsHandler, LocalDateTime fromTime, LocalDateTime toTime,
        String filterField, String filterValue
    ) {
        return new UrlLogFileReader(statsHandler, fromTime, toTime, filterField, filterValue);
    }

}
