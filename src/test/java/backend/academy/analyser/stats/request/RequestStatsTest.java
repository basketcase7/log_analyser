package backend.academy.analyser.stats.request;

import backend.academy.analyser.NginxLogEntity;
import backend.academy.analyser.parser.HttpRequestParser;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@DisplayName("Тест класса, собирающего статистику по запросам")
public class RequestStatsTest {

    private RequestStats requestStats;
    private NginxLogEntity nginxLogEntity;

    @BeforeEach
    public void setUp() {
        requestStats = new RequestStats();
        nginxLogEntity = mock(NginxLogEntity.class);
    }

    @DisplayName("Проверка инициализации мап со статистикой")
    @Test
    void testInitResourcesAndMethodsMap() {
        Map<String, Integer> requestResourceCounts = requestStats.requestResourceCounts();
        Map<String, Integer> requestMethodsCounts = requestStats.requestMethodsCounts();

        assertEquals(0, requestResourceCounts.size());
        assertEquals(0, requestMethodsCounts.size());
    }

    @DisplayName("Проверка обновления статистики методов и ресурсов")
    @Test
    void testChangeRequestStats() {
        Map<String, Integer> requestResourceCounts = requestStats.requestResourceCounts();
        Map<String, Integer> requestMethodsCounts = requestStats.requestMethodsCounts();

        when(nginxLogEntity.request()).thenReturn("GET /downloads/product_1 HTTP/1.1");

        try (MockedStatic<HttpRequestParser> parserMock = mockStatic(HttpRequestParser.class)) {
            parserMock.when(() -> HttpRequestParser.parseHttpRequest("GET /downloads/product_1 HTTP/1.1"))
                .thenReturn(new String[] {"GET", "/downloads/product_1", "HTTP/1.1"});

            requestStats.changeRequestStats(nginxLogEntity);
        }

        assertEquals(1, requestMethodsCounts.get("GET"));
        assertEquals(1, requestResourceCounts.get("/downloads/product_1"));
    }

}
