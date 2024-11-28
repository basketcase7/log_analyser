package backend.academy.analyser;

import backend.academy.analyser.parser.HttpRequestParser;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.MockedStatic;
import static org.junit.jupiter.api.Assertions.assertEquals;
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

    @DisplayName("Тест фильтрации по пользователю")
    @ParameterizedTest
    @CsvSource({
        "-, -, true",
        "smb, -, false"
    })
    void testFilterByRemoteUser(String logUser, String filterUser, boolean expectedResult) {
        LogsFilter logsFilter = new LogsFilter("remoteUser", filterUser);
        when(nginxLogEntity.remoteUser()).thenReturn(logUser);

        boolean result = logsFilter.filter(nginxLogEntity);

        assertEquals(expectedResult, result);
    }

    @DisplayName("Тест фильтрации по пути в HTTP-запросе")
    @ParameterizedTest
    @CsvSource({
        "GET /downloads/product_1 HTTP/1.1, downloads/product_1, true",
        "GET /downloads/product_2 HTTP/1.1, downloads/product_1, false"
    })
    void testFilterByRequestPath(String logRequest, String filterPath, boolean expectedResult) {
        LogsFilter logsFilter = new LogsFilter("requestPath", filterPath);
        when(nginxLogEntity.request()).thenReturn(logRequest);

        try (MockedStatic<HttpRequestParser> parserMock = mockStatic(HttpRequestParser.class)) {
            parserMock.when(() -> HttpRequestParser.parseHttpRequest(logRequest))
                .thenAnswer(_ -> {
                    String[] parts = logRequest.split(" ");
                    return new String[] {parts[0], parts[1], parts[2]};
                });

            boolean result = logsFilter.filter(nginxLogEntity);

            assertEquals(expectedResult, result);
        }
    }

    @DisplayName("Тест фильтрации по версии протокола")
    @ParameterizedTest
    @CsvSource({
        "GET /downloads/product_1 HTTP/1.1, HTTP/1.1, true",
        "GET /downloads/product_1 HTTP/1.1, HTTP/1.2, false"
    })
    void testFilterByRequestProtocol(String logRequest, String filterProtocol, boolean expectedResult) {
        LogsFilter logsFilter = new LogsFilter("requestProtocol", filterProtocol);
        when(nginxLogEntity.request()).thenReturn(logRequest);

        try (MockedStatic<HttpRequestParser> parserMock = mockStatic(HttpRequestParser.class)) {
            parserMock.when(() -> HttpRequestParser.parseHttpRequest(logRequest))
                .thenReturn(new String[] {"GET", "/downloads/product_1", "HTTP/1.1"});

            boolean result = logsFilter.filter(nginxLogEntity);

            assertEquals(expectedResult, result);
        }
    }

    @DisplayName("Тест фильтрации по статусу ответа")
    @ParameterizedTest
    @CsvSource({
        "404, 404, true",
        "404, 500, false"
    })
    void testFilterByStatus(int logStatus, String filterStatus, boolean expectedResult) {
        LogsFilter logsFilter = new LogsFilter("status", filterStatus);
        when(nginxLogEntity.status()).thenReturn(logStatus);

        boolean result = logsFilter.filter(nginxLogEntity);

        assertEquals(expectedResult, result);
    }

    @DisplayName("Тест фильтрации по размеру ответа")
    @ParameterizedTest
    @CsvSource({
        "0, 0, true",
        "0, 343, false"
    })
    void testFilterByBytes(int logBytes, String filterBytes, boolean expectedResult) {
        LogsFilter logsFilter = new LogsFilter("bytes", filterBytes);
        when(nginxLogEntity.bodyBytesSent()).thenReturn(logBytes);

        boolean result = logsFilter.filter(nginxLogEntity);

        assertEquals(expectedResult, result);
    }

    @DisplayName("Тест фильтрации по источнику")
    @ParameterizedTest
    @CsvSource({
        "-, -, true",
        "url, -, false"
    })
    void testFilterByReferer(String logReferer, String filterReferer, boolean expectedResult) {
        LogsFilter logsFilter = new LogsFilter("referer", filterReferer);
        when(nginxLogEntity.httpReferer()).thenReturn(logReferer);

        boolean result = logsFilter.filter(nginxLogEntity);

        assertEquals(expectedResult, result);
    }

    @DisplayName("Тест фильтрации по агенту")
    @ParameterizedTest
    @CsvSource({
        "Debian APT-HTTP/1.3 (0.8.16~exp12ubuntu10.21), Debian APT-HTTP/1.3, true",
        "Debian APT-HTTP/1.2, Debian APT-HTTP/1.3, false"
    })
    void testFilterByUserAgent(String logAgent, String filterAgent, boolean expectedResult) {
        LogsFilter logsFilter = new LogsFilter("agent", filterAgent);
        when(nginxLogEntity.httpUserAgent()).thenReturn(logAgent);

        boolean result = logsFilter.filter(nginxLogEntity);

        assertEquals(expectedResult, result);
    }

    @DisplayName("Тест фильтрации по дате")
    @ParameterizedTest
    @CsvSource({
        "2023-10-15T10:15:30, 2023-10-15T10:15:30, true",
        "2022-10-19T10:15:31, 2021-11-15T10:12:30, false"
    })
    void testFilterByDate(String logDate, String filterDate, boolean expectedResult) {
        LogsFilter logsFilter = new LogsFilter("date", filterDate);
        LocalDateTime logDateTime = LocalDateTime.parse(logDate);
        when(nginxLogEntity.timeLocal()).thenReturn(logDateTime);

        boolean result = logsFilter.filter(nginxLogEntity);

        assertEquals(expectedResult, result);
    }

    @DisplayName("Тест фильтрации по адресу")
    @ParameterizedTest
    @CsvSource({
        "93.180.71.3, 93.180.71.3, true",
        "217.168.17.5, 93.180.71.3, false"
    })
    void testFilterByRemoteAddr(String logAddr, String filterAddr, boolean expectedResult) {
        LogsFilter logsFilter = new LogsFilter("remoteAddr", filterAddr);
        when(nginxLogEntity.remoteAddr()).thenReturn(logAddr);

        boolean result = logsFilter.filter(nginxLogEntity);

        assertEquals(expectedResult, result);
    }

    @DisplayName("Тест фильтрации по HTTP-методу")
    @ParameterizedTest
    @CsvSource({
        "GET /downloads/product_1 HTTP/1.1, GET, true",
        "GET /downloads/product_1 HTTP/1.1, HEAD, false"
    })
    void testFilterByRequestMethod(String logRequest, String filterMethod, boolean expectedResult) {
        LogsFilter logsFilter = new LogsFilter("requestMethod", filterMethod);
        when(nginxLogEntity.request()).thenReturn(logRequest);

        try (MockedStatic<HttpRequestParser> parserMock = mockStatic(HttpRequestParser.class)) {
            parserMock.when(() -> HttpRequestParser.parseHttpRequest(logRequest))
                .thenAnswer(_ -> {
                    String[] parts = logRequest.split(" ");
                    return new String[] {parts[0], parts[1], parts[2]};
                });

            boolean result = logsFilter.filter(nginxLogEntity);

            assertEquals(expectedResult, result);
        }
    }

    @DisplayName("Тест фильтрации по диапазону дат")
    @ParameterizedTest
    @CsvSource({
        "2022-10-01T00:00:00, 2024-12-31T23:59:59, 2023-11-15T12:00:00, true",
        "2024-10-01T00:00:00, 2024-12-31T23:59:59, 2025-11-15T12:00:00, false"
    })
    void testCheckFromToDate(String from, String to, String actual, boolean expectedResult) {
        LocalDateTime fromDateTime = LocalDateTime.parse(from);
        LocalDateTime toDateTime = LocalDateTime.parse(to);
        LocalDateTime actualDateTime = LocalDateTime.parse(actual);
        LogsFilter logsFilter = new LogsFilter("field", "value");

        boolean result = logsFilter.checkFromToDate(fromDateTime, toDateTime, actualDateTime);

        assertEquals(expectedResult, result);
    }

}
