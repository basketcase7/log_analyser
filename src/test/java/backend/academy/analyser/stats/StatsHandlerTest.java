package backend.academy.analyser.stats;

import backend.academy.analyser.NginxLogEntity;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("Тесты главного класса по сбору статистики")
public class StatsHandlerTest {

    private StatsHandler statsHandler;
    private NginxLogEntity nginxLogEntity;

    @BeforeEach
    public void setUp() {
        statsHandler = new StatsHandler("testFile", LocalDateTime.MIN, LocalDateTime.MAX);
        nginxLogEntity = mock(NginxLogEntity.class);
    }

    @DisplayName("Проверка работы конструктора")
    @Test
    void testInit() {
        assertEquals("testFile", statsHandler.files());
        assertEquals(LocalDateTime.MIN, statsHandler.fromTime());
        assertEquals(LocalDateTime.MAX, statsHandler.toTime());
        assertEquals(0, statsHandler.requestCount());
        assertNotNull(statsHandler.requestStats());
        assertNotNull(statsHandler.responseCodeStats());
        assertNotNull(statsHandler.dateStats());
    }

    @DisplayName("Проверка метода, обновляющего статистику")
    @Test
    void testUpdateStats() {
        when(nginxLogEntity.bodyBytesSent()).thenReturn(215);
        when(nginxLogEntity.status()).thenReturn(404);
        when(nginxLogEntity.timeLocal()).thenReturn(LocalDateTime.now());
        when(nginxLogEntity.request()).thenReturn("GET /downloads/product_1 HTTP/1.1");

        statsHandler.updateStats(nginxLogEntity);

        assertEquals(1, statsHandler.requestCount());
        assertEquals(215, statsHandler.sumResponseSize());
        assertEquals(1, statsHandler.responseCodeStats().responseCodesMap().get(404));
        assertEquals(1, statsHandler.dateStats().hoursRequestCounts().size());
        assertEquals(1, statsHandler.requestStats().requestMethodsCounts().size());
        assertEquals(1, statsHandler.requestStats().requestResourceCounts().size());
    }

    @DisplayName("Проверка метода, подводящего итоги статистики")
    @Test
    void testCountStats() {
        when(nginxLogEntity.bodyBytesSent()).thenReturn(215);
        when(nginxLogEntity.status()).thenReturn(404);
        when(nginxLogEntity.timeLocal()).thenReturn(LocalDateTime.now());
        when(nginxLogEntity.request()).thenReturn("GET /downloads/product_1 HTTP/1.1");

        statsHandler.updateStats(nginxLogEntity);

        when(nginxLogEntity.bodyBytesSent()).thenReturn(100);
        when(nginxLogEntity.status()).thenReturn(500);
        when(nginxLogEntity.timeLocal()).thenReturn(LocalDateTime.now());
        when(nginxLogEntity.request()).thenReturn("GET /downloads/product_1 HTTP/1.1");

        statsHandler.updateStats(nginxLogEntity);

        statsHandler.countStats();

        assertEquals(157, statsHandler.avgResponseSize());
        assertEquals(209, statsHandler.percentile());
    }

    @DisplayName("Проверка метода сортировки мапы")
    @Test
    void testSortRequestMap() {
        Map<String, Integer> requestMap = new HashMap<>();
        requestMap.put("GET", 10);
        requestMap.put("POST", 5);
        requestMap.put("HEAD", 15);

        LinkedHashMap<String, Integer> sortedMap = statsHandler.sortRequestMap(requestMap);

        assertEquals(15, sortedMap.values().iterator().next());
        assertEquals("HEAD", sortedMap.keySet().iterator().next());
    }

}
