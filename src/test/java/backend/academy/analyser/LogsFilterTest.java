package backend.academy.analyser;

import backend.academy.analyser.parser.HttpRequestParser;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@DisplayName("Тесты для класса фильтрации логов по дате и полям")
public class LogsFilterTest {

    private NginxLogEntity nginxLogEntity;

    @BeforeEach
    void setUp() {
        nginxLogEntity = mock(NginxLogEntity.class);
    }

    @DisplayName("Тест фильтрации по адресу с совпадающим адресом")
    @Test
    void testFilterByValidRemoteAddr() {
        LogsFilter logsFilter = new LogsFilter("remoteAddr", "93.180.71.3");
        when(nginxLogEntity.remoteAddr()).thenReturn("93.180.71.3");

        boolean result = logsFilter.filter(nginxLogEntity);

        assertTrue(result);
    }

    @DisplayName("Тест фильтрации по адресу с разными адресами")
    @Test
    void testFilterByInvalidRemoteAddr() {
        LogsFilter logsFilter = new LogsFilter("remoteAddr", "93.180.71.3");
        when(nginxLogEntity.remoteAddr()).thenReturn("217.168.17.5");

        boolean result = logsFilter.filter(nginxLogEntity);

        assertFalse(result);
    }

    @DisplayName("Тест фильтрации по пользователю с совпадающим пользователем")
    @Test
    void testFilterByValidRemoteUser() {
        LogsFilter logsFilter = new LogsFilter("remoteUser", "-");
        when(nginxLogEntity.remoteUser()).thenReturn("-");

        boolean result = logsFilter.filter(nginxLogEntity);

        assertTrue(result);
    }

    @DisplayName("Тест фильтрации по пользователю с разными пользователями")
    @Test
    void testFilterByInvalidRemoteUser() {
        LogsFilter logsFilter = new LogsFilter("remoteUser", "");
        when(nginxLogEntity.remoteUser()).thenReturn("smb");

        boolean result = logsFilter.filter(nginxLogEntity);

        assertFalse(result);
    }

    @DisplayName("Тест фильтрации по методу в http-запросе с совпадающим методом")
    @Test
    void testFilterByValidRequestMethod() {
        LogsFilter logsFilter = new LogsFilter("requestMethod", "GET");
        when(nginxLogEntity.request()).thenReturn("GET /downloads/product_1 HTTP/1.1");

        try (MockedStatic<HttpRequestParser> parserMock = mockStatic(HttpRequestParser.class)) {
            parserMock.when(() -> HttpRequestParser.parseHttpRequest("GET /downloads/product_1 HTTP/1.1"))
                .thenReturn(new String[] {"GET", "/downloads/product_1", "HTTP/1.1"});

            boolean result = logsFilter.filter(nginxLogEntity);

            assertTrue(result);
        }
    }

    @DisplayName("Тест фильтрации по методу в http-запросе с разными методами")
    @Test
    void testFilterByInvalidRequestMethod() {
        LogsFilter logsFilter = new LogsFilter("requestMethod", "HEAD");
        when(nginxLogEntity.request()).thenReturn("GET /downloads/product_1 HTTP/1.1");

        try (MockedStatic<HttpRequestParser> parserMock = mockStatic(HttpRequestParser.class)) {
            parserMock.when(() -> HttpRequestParser.parseHttpRequest("GET /downloads/product_1 HTTP/1.1"))
                .thenReturn(new String[] {"GET", "/downloads/product_1", "HTTP/1.1"});

            boolean result = logsFilter.filter(nginxLogEntity);

            assertFalse(result);
        }
    }

    @DisplayName("Тест фильтрации по пути в http-запросе с путем, часть которого совпадает с логом")
    @Test
    void testFilterByValidRequestPath() {
        LogsFilter logsFilter = new LogsFilter("requestPath", "downloads/product_1");
        when(nginxLogEntity.request()).thenReturn("GET main/downloads/product_1 HTTP/1.1");

        try (MockedStatic<HttpRequestParser> parserMock = mockStatic(HttpRequestParser.class)) {
            parserMock.when(() -> HttpRequestParser.parseHttpRequest("GET main/downloads/product_1 HTTP/1.1"))
                .thenReturn(new String[] {"GET", "main/downloads/product_1", "HTTP/1.1"});

            boolean result = logsFilter.filter(nginxLogEntity);

            assertTrue(result);
        }
    }

    @DisplayName("Тест фильтрации по пути в http-запросе с путем, который не совпадает с тем, что в логе")
    @Test
    void testFilterByInvalidRequestPath() {
        LogsFilter logsFilter = new LogsFilter("requestPath", "downloads/product_2");
        when(nginxLogEntity.request()).thenReturn("GET /downloads/product_1 HTTP/1.1");

        try (MockedStatic<HttpRequestParser> parserMock = mockStatic(HttpRequestParser.class)) {
            parserMock.when(() -> HttpRequestParser.parseHttpRequest("GET /downloads/product_1 HTTP/1.1"))
                .thenReturn(new String[] {"GET", "/downloads/product_1", "HTTP/1.1"});

            boolean result = logsFilter.filter(nginxLogEntity);

            assertFalse(result);
        }
    }

    @DisplayName("Тест фильтрации по версии протокола в http-запросе с совпадающей версией")
    @Test
    void testFilterByValidRequestProtocol() {
        LogsFilter logsFilter = new LogsFilter("requestProtocol", "HTTP/1.1");
        when(nginxLogEntity.request()).thenReturn("GET /downloads/product_1 HTTP/1.1");

        try (MockedStatic<HttpRequestParser> parserMock = mockStatic(HttpRequestParser.class)) {
            parserMock.when(() -> HttpRequestParser.parseHttpRequest("GET /downloads/product_1 HTTP/1.1"))
                .thenReturn(new String[] {"GET", "/downloads/product_1", "HTTP/1.1"});

            boolean result = logsFilter.filter(nginxLogEntity);

            assertTrue(result);
        }
    }

    @DisplayName("Тест фильтрации по версии протокола в http-запросе с разными версиями")
    @Test
    void testFilterByInvalidRequestProtocol() {
        LogsFilter logsFilter = new LogsFilter("requestProtocol", "HTTP/1.2");
        when(nginxLogEntity.request()).thenReturn("GET /downloads/product_1 HTTP/1.1");

        try (MockedStatic<HttpRequestParser> parserMock = mockStatic(HttpRequestParser.class)) {
            parserMock.when(() -> HttpRequestParser.parseHttpRequest("GET /downloads/product_1 HTTP/1.1"))
                .thenReturn(new String[] {"GET", "/downloads/product_1", "HTTP/1.1"});

            boolean result = logsFilter.filter(nginxLogEntity);

            assertFalse(result);
        }
    }

    @DisplayName("Тест фильтрации по коду ответа с совпадающими кодами")
    @Test
    void testFilterByValidStatus() {
        LogsFilter logsFilter = new LogsFilter("status", "404");
        when(nginxLogEntity.status()).thenReturn(404);

        boolean result = logsFilter.filter(nginxLogEntity);

        assertTrue(result);
    }

    @DisplayName("Тест фильтрации по коду ответа с разными кодами")
    @Test
    void testFilterByInvalidStatus() {
        LogsFilter logsFilter = new LogsFilter("status", "500");
        when(nginxLogEntity.status()).thenReturn(404);

        boolean result = logsFilter.filter(nginxLogEntity);

        assertFalse(result);
    }

    @DisplayName("Тест фильтрации по размеру ответа с совпадающими размерами")
    @Test
    void testFilterByValidBytes() {
        LogsFilter logsFilter = new LogsFilter("bytes", "0");
        when(nginxLogEntity.bodyBytesSent()).thenReturn(0);

        boolean result = logsFilter.filter(nginxLogEntity);

        assertTrue(result);
    }

    @DisplayName("Тест фильтрации по размеру ответа с разными размерами")
    @Test
    void testFilterByInvalidBytes() {
        LogsFilter logsFilter = new LogsFilter("bytes", "343");
        when(nginxLogEntity.bodyBytesSent()).thenReturn(0);

        boolean result = logsFilter.filter(nginxLogEntity);

        assertFalse(result);
    }

    @DisplayName("Тест фильтрации по источнику с одним источником")
    @Test
    void testFilterByValidReferer() {
        LogsFilter logsFilter = new LogsFilter("referer", "-");
        when(nginxLogEntity.httpReferer()).thenReturn("-");

        boolean result = logsFilter.filter(nginxLogEntity);

        assertTrue(result);
    }

    @DisplayName("Тест фильтрации по источнику с разными источниками")
    @Test
    void testFilterByInvalidReferer() {
        LogsFilter logsFilter = new LogsFilter("referer", "url");
        when(nginxLogEntity.httpReferer()).thenReturn("-");

        boolean result = logsFilter.filter(nginxLogEntity);

        assertFalse(result);
    }

    @DisplayName("Тест фильтрации по агенту с агентом, часть которого есть в агенте лога")
    @Test
    void testFilterByValidUserAgent() {
        LogsFilter logsFilter = new LogsFilter("agent", "Debian APT-HTTP/1.3");
        when(nginxLogEntity.httpUserAgent()).thenReturn("Debian APT-HTTP/1.3 (0.8.16~exp12ubuntu10.21)");

        boolean result = logsFilter.filter(nginxLogEntity);

        assertTrue(result);
    }

    @DisplayName("Тест фильтрации по агенту с агентом, части которого нет в агенте лога")
    @Test
    void testFilterByInvalidUserAgent() {
        LogsFilter logsFilter = new LogsFilter("agent", "Debian APT-HTTP/1.3");
        when(nginxLogEntity.httpUserAgent()).thenReturn("Debian APT-HTTP/1.2");

        boolean result = logsFilter.filter(nginxLogEntity);

        assertFalse(result);
    }

    @DisplayName("Тест фильтрации по дате, когда дата совпадает")
    @Test
    void testFilterByValidDate() {
        LogsFilter logsFilter = new LogsFilter("date", "2023-10-15T10:15:30");
        LocalDateTime logDate = LocalDateTime.parse("2023-10-15T10:15:30");
        when(nginxLogEntity.timeLocal()).thenReturn(logDate);

        boolean result = logsFilter.filter(nginxLogEntity);

        assertTrue(result, "Should match date");
    }

    @DisplayName("Тест фильтрации по дате, когда дата не совпадает")
    @Test
    void testFilterByInvalidDate() {
        LogsFilter logsFilter = new LogsFilter("date", "2021-11-15T10:12:30");
        LocalDateTime logDate = LocalDateTime.parse("2022-10-19T10:15:31");
        when(nginxLogEntity.timeLocal()).thenReturn(logDate);

        boolean result = logsFilter.filter(nginxLogEntity);

        assertFalse(result);
    }

    @DisplayName("Тест фильтрации попадания даты в выбранный временной диапазон, когда дата попадает")
    @Test
    void testCheckValidFromToDate() {
        LocalDateTime fromDateTime = LocalDateTime.parse("2022-10-01T00:00:00");
        LocalDateTime toDateTime = LocalDateTime.parse("2024-12-31T23:59:59");
        LocalDateTime actualDateTime = LocalDateTime.parse("2023-11-15T12:00:00");
        LogsFilter logsFilter = new LogsFilter("field", "value");

        boolean result = logsFilter.checkFromToDate(fromDateTime, toDateTime, actualDateTime);

        assertTrue(result);
    }

    @DisplayName("Тест фильтрации попадания даты в выбранный временной диапазон, когда дата не попадает")
    @Test
    void testCheckInvalidFromToDate() {
        LocalDateTime fromDateTime = LocalDateTime.parse("2024-10-01T00:00:00");
        LocalDateTime toDateTime = LocalDateTime.parse("2024-12-31T23:59:59");
        LocalDateTime actualDateTime = LocalDateTime.parse("2025-11-15T12:00:00");
        LogsFilter logsFilter = new LogsFilter("field", "value");

        boolean result = logsFilter.checkFromToDate(fromDateTime, toDateTime, actualDateTime);

        assertFalse(result);
    }
}
