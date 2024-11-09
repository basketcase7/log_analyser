package backend.academy.analyser.reader;

import backend.academy.analyser.LogsFilter;
import backend.academy.analyser.parser.LogParser;
import backend.academy.analyser.stats.StatsHandler;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SuppressFBWarnings("URLCONNECTION_SSRF_FD")
public class UrlLogFileReader implements LogFileReader {

    private final LogParser logParser;
    private final StatsHandler statsHandler;
    private final LocalDateTime fromDateTime;
    private final LocalDateTime toDateTime;
    private final LogsFilter logsFilter;

    private final static String INVALID_URI_STRING = "Invalid URI";

    public UrlLogFileReader(
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

    @Override
    public void read(String path) {
        if (isValidUrl(path)) {
            processUrl(path);
        }
    }

    public boolean isValidUrl(String path) {
        try {
            URI uri = new URI(path);
            String scheme = uri.getScheme();
            return "http".equals(scheme) || "https".equals(scheme) || "ftp".equals(scheme);

        } catch (URISyntaxException e) {
            log.error(INVALID_URI_STRING + "{}", path);
            return false;
        }
    }

    public void processUrl(String uriString) {
        try {
            URI uri = new URI(uriString);
            try (BufferedReader bufferedReader = createBufferedReader(uri)) {
                bufferedReader.lines()
                    .map(logParser::parse)
                    .filter(l -> l != null && logsFilter.filter(l))
                    .filter(l -> logsFilter.checkFromToDate(fromDateTime, toDateTime, l.timeLocal()))
                    .forEach(statsHandler::updateStats);
            }
        } catch (URISyntaxException | IOException e) {
            log.error(INVALID_URI_STRING + "{}", uriString);
        }
    }

    protected BufferedReader createBufferedReader(URI uri) throws IOException {
        return new BufferedReader(new InputStreamReader(uri.toURL().openStream(), StandardCharsets.UTF_8));
    }
}
