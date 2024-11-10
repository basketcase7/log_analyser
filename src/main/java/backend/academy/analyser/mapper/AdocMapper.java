package backend.academy.analyser.mapper;

import backend.academy.analyser.stats.StatsHandler;
import backend.academy.analyser.stats.response.Code;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import lombok.experimental.UtilityClass;

@SuppressFBWarnings("VA_FORMAT_STRING_USES_NEWLINE")
@UtilityClass
public class AdocMapper {

    private final static int COUNT_OF_FIRST_VALUES = 3;
    private final static String DIVIDE_STRING = "|===\n";
    private final static String VAR_FORMAT_STRING = "| `%s` | %,d\n";
    private final static String QUANTITY_STRING = "Количество";

    public static String mapStatsToAdocString(StatsHandler statsHandler) {
        StringBuilder output = new StringBuilder();

        output.append("==== Общая информация\n\n");
        output.append(generateGeneralInfoTable(statsHandler));

        output.append("\n==== Самые запрашиваемые ресурсы\n\n");
        output.append(generateTopRequestedResourcesTable(statsHandler));

        output.append("\n==== Самые запрашиваемые методы\n\n");
        output.append(generateTopRequestedMethodsTable(statsHandler));

        output.append("\n==== Коды ответа\n\n");
        output.append(generateResponseCodesTable(statsHandler));

        output.append("\n==== Самое активное время\n\n");
        output.append(generateActiveHoursTable(statsHandler));

        return output.toString();
    }

    private static String generateGeneralInfoTable(StatsHandler statsHandler) {
        StringBuilder output = new StringBuilder();
        output.append(DIVIDE_STRING);
        output.append("| Метрика | Значение\n");

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        String fromDate = statsHandler.fromTime() != null ? statsHandler.fromTime().format(dateTimeFormatter) : "-";
        String toDate = statsHandler.toTime() != null ? statsHandler.toTime().format(dateTimeFormatter) : "-";
        String filesList = String.join(", ", statsHandler.files());

        output.append(String.format("| Источник логов | `%s`\n", filesList));
        output.append(String.format("| Начальная дата | %s\n", fromDate));
        output.append(String.format("| Конечная дата | %s\n", toDate));
        output.append(String.format("| Количество запросов | %,d\n", statsHandler.requestCount()));
        output.append(String.format("| Средний размер ответа | %db\n", statsHandler.avgResponseSize()));
        output.append(String.format("| 95p размера ответа | %db\n", statsHandler.percentile()));
        output.append(DIVIDE_STRING);
        return output.toString();
    }

    private static String generateTopRequestedResourcesTable(StatsHandler statsHandler) {
        return generateTableWithSingleMetric("Ресурс", QUANTITY_STRING, statsHandler.sortRequestMap(
            statsHandler.requestStats().requestResourceCounts()));
    }

    private static String generateTopRequestedMethodsTable(StatsHandler statsHandler) {
        return generateTableWithSingleMetric("Метод", QUANTITY_STRING, statsHandler.sortRequestMap(
            statsHandler.requestStats().requestMethodsCounts()));
    }

    private static String generateResponseCodesTable(StatsHandler statsHandler) {
        StringBuilder output = new StringBuilder();
        output.append(DIVIDE_STRING);
        output.append("| Код | Имя | Количество\n");

        int count = 0;
        for (Map.Entry<Integer, Integer> entry : statsHandler.responseCodeStats()
            .sortCodeMap(statsHandler.responseCodeStats().responseCodesMap()).entrySet()) {
            if (count < COUNT_OF_FIRST_VALUES) {
                String codeName = getStatusName(entry.getKey());
                output.append(String.format("| %d | %-20s | %,d\n", entry.getKey(), codeName, entry.getValue()));
                count++;
            }
        }
        output.append(DIVIDE_STRING);
        return output.toString();
    }

    private static String generateActiveHoursTable(StatsHandler statsHandler) {
        return generateTableWithSingleMetric("Час", QUANTITY_STRING, statsHandler.sortRequestMap(
            statsHandler.dateStats().hoursRequestCounts()));
    }

    private static String generateTableWithSingleMetric(
        String metricName,
        String valueName,
        Map<String, Integer> data
    ) {
        StringBuilder output = new StringBuilder();
        output.append(DIVIDE_STRING);
        output.append(String.format("| %s | %s\n", metricName, valueName));

        int count = 0;
        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            if (count < COUNT_OF_FIRST_VALUES) {
                output.append(String.format(VAR_FORMAT_STRING, entry.getKey(), entry.getValue()));
                count++;
            }
        }
        output.append(DIVIDE_STRING);
        return output.toString();
    }

    private String getStatusName(int code) {
        for (Code status : Code.values()) {
            if (status.code() == code) {
                return status.description();
            }
        }
        return "Unknown";
    }
}
