package backend.academy.analyser.stats.response;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Тесты для класса, собирающего статистику по кодам ответа")
public class ResponseCodeStatsTest {

    private ResponseCodeStats responseCodeStats;

    @BeforeEach
    public void setUp() {
        responseCodeStats = new ResponseCodeStats();
    }

    @DisplayName("Проверка метода инициализирующего мапу встреченных ответов")
    @Test
    void testInitResponseCodeStats() {
        HashMap<Integer, Integer> responseCodesMap = responseCodeStats.responseCodesMap();

        for (Code code : Code.values()) {
            assertTrue(responseCodesMap.containsKey(code.code()));
            assertEquals(0, responseCodesMap.get(code.code()));
        }
    }

    @DisplayName("Проверка метода, добавляющего встреченный код ответа в мапу")
    @Test
    void changeResponseCodeStatsTest() {
        int status200 = Code.OK.code();
        int status500 = Code.INTERNAL_SERVER_ERROR.code();

        responseCodeStats.changeResponseCodeStats(status200);
        responseCodeStats.changeResponseCodeStats(status500);
        responseCodeStats.changeResponseCodeStats(status500);

        assertEquals(1, responseCodeStats.responseCodesMap().get(status200));
        assertEquals(2, responseCodeStats.responseCodesMap().get(status500));
    }

    @DisplayName("Проверка метода, сортирующего мапу в порядке убывания")
    @Test
    void testSortCodeMap() {
        HashMap<Integer, Integer> map = new HashMap<>();
        map.put(Code.OK.code(), 1);
        map.put(Code.INTERNAL_SERVER_ERROR.code(), 17);
        map.put(Code.NOT_FOUND.code(), 18);

        LinkedHashMap<Integer, Integer> sortedMap = responseCodeStats.sortCodeMap(map);

        Integer[] expectedOrder = {Code.NOT_FOUND.code(), Code.INTERNAL_SERVER_ERROR.code(), Code.OK.code()};
        int i = 0;
        for (Map.Entry<Integer, Integer> entry : sortedMap.entrySet()) {
            assertEquals(expectedOrder[i++], entry.getKey());
        }
    }

}
