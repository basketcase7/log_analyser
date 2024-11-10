package backend.academy.analyser.mapper;

import backend.academy.analyser.stats.StatsHandler;
import backend.academy.analyser.stats.date.DateStats;
import backend.academy.analyser.stats.request.RequestStats;
import backend.academy.analyser.stats.response.ResponseCodeStats;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static backend.academy.analyser.mapper.MarkdownMapper.mapStatsToMarkdownString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.when;

@DisplayName("Тест класса, преобразующего статистику в строку для markdown файла")
@ExtendWith(MockitoExtension.class)
public class MarkdownMapperTest {

    @Mock
    private StatsHandler statsHandler;

    @Mock
    private RequestStats requestStats;

    @Mock
    private DateStats dateStats;

    private String expectedMarkdownString = "#### Общая информация\n" +
        "\n" +
        "|        Метрика        |     Значение |\n" +
        "|:---------------:|-----------:|\n" +
        "|       Источник логов        | `C:\\Users\\Maxim\\Desktop\\backend_academy\\backend_academy_2024_project_3-java-basketcase7\\src\\main\\resources\\logs\\*short.txt` |\n" +
        "|    Начальная дата     |   - |\n" +
        "|     Конечная дата     |   - |\n" +
        "|  Количество запросов  |       11 |\n" +
        "| Средний размер ответа |         451b |\n" +
        "|   95p размера ответа  |         1903b |\n" +
        "\n" +
        "#### Самые запрашиваемые ресурсы\n" +
        "\n" +
        "|     Ресурс      | Количество |\n" +
        "|:---------------:|-----------:|\n" +
        "|  `/downloads/product_1`  |      8 |\n" +
        "|  `/downloads/product_2`  |      3 |\n" +
        "\n" +
        "#### Самые запрашиваемые методы\n" +
        "\n" +
        "|     Метод      | Количество |\n" +
        "|:---------------:|-----------:|\n" +
        "|  `GET`  |      11 |\n" +
        "\n" +
        "#### Коды ответа\n" +
        "\n" +
        "| Код |          Имя          | Количество |\n" +
        "|:---:|:---------------------:|-----------:|\n" +
        "| 109 | Unknown              | 5 |\n" +
        "| 304 | Not Modified         | 3 |\n" +
        "| 404 | Not Found            | 2 |\n" +
        "#### Самое активное время\n" +
        "\n" +
        "|     Час      | Количество |\n" +
        "|:---------------:|-----------:|\n" +
        "|  `8`  |      11 |\n" +
        "\n";

    @BeforeEach
    public void setUp() {

        when(statsHandler.files()).thenReturn(
            "C:\\Users\\Maxim\\Desktop\\backend_academy\\backend_academy_2024_project_3-java-basketcase7\\src\\main\\resources\\logs\\*short.txt");
        when(statsHandler.fromTime()).thenReturn(null);
        when(statsHandler.toTime()).thenReturn(null);
        when(statsHandler.requestCount()).thenReturn(11);
        when(statsHandler.percentile()).thenReturn(1903);
        when(statsHandler.avgResponseSize()).thenReturn(451);

        when(statsHandler.requestStats()).thenReturn(requestStats);
        LinkedHashMap<String, Integer> resourceCounts = new LinkedHashMap<>();
        resourceCounts.put("/downloads/product_1", 8);
        resourceCounts.put("/downloads/product_2", 3);
        when(requestStats.requestResourceCounts()).thenReturn(resourceCounts);

        LinkedHashMap<String, Integer> methodCounts = new LinkedHashMap<>();
        methodCounts.put("GET", 11);
        when(requestStats.requestMethodsCounts()).thenReturn(methodCounts);

        ResponseCodeStats responseCodeStats = new ResponseCodeStats();
        HashMap<Integer, Integer> codeCount = new HashMap<>();
        codeCount.put(109, 5);
        codeCount.put(304, 3);
        codeCount.put(404, 2);
        responseCodeStats.responseCodesMap(codeCount);
        when(statsHandler.responseCodeStats()).thenReturn(responseCodeStats);

        when(statsHandler.dateStats()).thenReturn(dateStats);
        LinkedHashMap<String, Integer> hourRequestCount = new LinkedHashMap<>();
        hourRequestCount.put("8", 11);
        when(dateStats.hoursRequestCounts()).thenReturn(hourRequestCount);

        when(statsHandler.sortRequestMap(anyMap())).thenAnswer(invocation -> {
            Map<String, Integer> argument = invocation.getArgument(0);
            if (argument.containsKey("/downloads/product_1")) {
                return resourceCounts;
            } else if (argument.containsKey("GET")) {
                return methodCounts;
            } else if (argument.containsKey("8")) {
                return hourRequestCount;
            } else {
                return new LinkedHashMap<>();
            }
        });
    }

    @DisplayName("Проверка преобразования статистики в строку для markdown файла")
    @Test
    void testMapStatsToMarkdownString() {

        String actualString = mapStatsToMarkdownString(statsHandler);

        assertEquals(expectedMarkdownString, actualString);
    }
}
