package backend.academy.analyser.reader;

import backend.academy.analyser.LogsFilter;
import backend.academy.analyser.NginxLogEntity;
import backend.academy.analyser.parser.LogParser;
import backend.academy.analyser.stats.StatsHandler;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("Тесты для чтения и обработки файлов по URL")
public class UrlLogFileReaderTest {

    @InjectMocks
    private UrlLogFileReader urlLogFileReader;

    @Mock
    private LogParser logParser;

    @Mock
    private LogsFilter logsFilter;

    @Mock
    private NginxLogEntity nginxLogEntity;

    @Mock
    private StatsHandler statsHandler;

    private LocalDateTime fromTime;
    private LocalDateTime toTime;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        fromTime = LocalDateTime.of(2023, 1, 1, 0, 0);
        toTime = LocalDateTime.of(2023, 12, 31, 23, 59);
        urlLogFileReader = new UrlLogFileReader(statsHandler, fromTime, toTime, "status", "400");
    }

    @DisplayName("Проверка валидации URL с корректным URL")
    @Test
    void testIsValidUrlWithValidUrl() {
        assertTrue(urlLogFileReader.isValidUrl("http://www.google.com"));
    }

    @DisplayName("Проверка валидации URL с некорректным URL")
    @Test
    void testIsValidUrlWithInvalidUrl() {
        assertFalse(urlLogFileReader.isValidUrl("smth"));
    }

    @DisplayName("Проверка метода чтения URL")
    @Test
    void testRead() {
        UrlLogFileReader readerSpy = spy(urlLogFileReader);
        doNothing().when(readerSpy).processUrl(anyString());

        readerSpy.processUrl("http://www.google.com");

        verify(readerSpy).processUrl("http://www.google.com");
    }

    @DisplayName("Проверка чтения логов в файле")
    @Test
    void testProcessUrl() throws IOException {
        LogsFilter mockFilter = mock(LogsFilter.class);
        LogParser mockLogParser = mock(LogParser.class);

        UrlLogFileReader readerSpy = spy(urlLogFileReader);

        URI uri = URI.create("http://www.google.com");
        BufferedReader bufferedReader =
            new BufferedReader(new InputStreamReader(uri.toURL().openStream(), StandardCharsets.UTF_8));

        when(mockLogParser.parse(any())).thenReturn(nginxLogEntity);
        when(mockFilter.filter(any())).thenReturn(true);
        when(mockFilter.checkFromToDate(any(), any(), any())).thenReturn(true);
        when(readerSpy.createBufferedReader(uri)).thenReturn(bufferedReader);

        doReturn(mockFilter.filter(any())).when(logsFilter).filter(any());
        doReturn(mockFilter.checkFromToDate(any(), any(), any())).when(logsFilter).checkFromToDate(any(), any(), any());
        doReturn(mockLogParser.parse(any())).when(logParser).parse(any());

        readerSpy.processUrl("http://www.google.com");

        verify(mockLogParser).parse(any());
        verify(mockFilter).filter(any());
        verify(mockFilter).checkFromToDate(any(), any(), any());
    }
}
