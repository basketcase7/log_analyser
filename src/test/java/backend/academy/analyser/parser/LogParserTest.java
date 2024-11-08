package backend.academy.analyser.parser;

import backend.academy.analyser.NginxLogEntity;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@DisplayName("Тесты для класса парсера строки в лог")
public class LogParserTest {

    private LogParser logParser;
    private DateTimeFormatter dateTimeFormatter;

    @BeforeEach
    void setUp() {
        logParser = new LogParser();
        dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss Z", Locale.US);
    }

    @DisplayName("Тест перевода корректной строки в лог")
    @Test
    void testParseValidLogLine() {
        String logLine = "93.180.71.3 - - [17/May/2015:08:05:32 +0000] \"GET /downloads/product_1 HTTP/1.1\" 304 0 \"-\" \"Debian APT-HTTP/1.3 (0.8.16~exp12ubuntu10.21)\"";

        NginxLogEntity nginxLogEntity = logParser.parse(logLine);

        assertNotNull(nginxLogEntity);
        assertEquals("93.180.71.3", nginxLogEntity.remoteAddr());
        assertEquals("-", nginxLogEntity.remoteUser());
        assertEquals(LocalDateTime.parse("17/May/2015:08:05:32 +0000", dateTimeFormatter), nginxLogEntity.timeLocal());
        assertEquals("GET /downloads/product_1 HTTP/1.1", nginxLogEntity.request());
        assertEquals(Integer.parseInt("304"), nginxLogEntity.status());
        assertEquals(0, nginxLogEntity.bodyBytesSent());
        assertEquals("-", nginxLogEntity.httpReferer());
        assertEquals("Debian APT-HTTP/1.3 (0.8.16~exp12ubuntu10.21)", nginxLogEntity.httpUserAgent());
    }

    @DisplayName("Тест перевода некорректной строки в лог")
    @Test
    void testParseInvalidLogLine() {
        String logLine = "log line";

        NginxLogEntity nginxLogEntity = logParser.parse(logLine);

        assertNull(nginxLogEntity);
    }

    @DisplayName("Тест перевода строки с некорректной датой в лог")
    @Test
    void testParseLogLineInvalidDate() {
        String logLine = "93.180.71.3 - - [asdasd] \"GET /downloads/product_1 HTTP/1.1\" 304 0 \"-\" \"Debian APT-HTTP/1.3 (0.8.16~exp12ubuntu10.21)\"";

        NginxLogEntity nginxLogEntity = logParser.parse(logLine);

        assertNull(nginxLogEntity.timeLocal());
    }

}
