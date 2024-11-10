package backend.academy.analyser.mapper;

import backend.academy.analyser.stats.StatsHandler;
import backend.academy.analyser.stats.response.Code;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import lombok.experimental.UtilityClass;

/**
 * Преобразует собранную статистику в строку, которая будет записана в markdown файл
 */
@SuppressFBWarnings("VA_FORMAT_STRING_USES_NEWLINE")
@UtilityClass
public class MarkdownMapper {

    private final static int COUNT_OF_FIRST_VALUES = 3;
    private final static String DIVIDE_STRING = "|:---------------:|-----------:|\n";
    private final static String VAR_FORMAT_STRING = "|  `%s`  |      %,d |\n";
    private final static String QUANTITY_STRING = "Количество";

    /**
     * Основной метод, вызывающие все методы по сборе компонентов строки
     *
     * @param statsHandler Собранная статистика
     * @return Строку для записи в markdown
     */
    public static String mapStatsToMarkdownString(StatsHandler statsHandler) {
        StringBuilder output = new StringBuilder();

        output.append("#### Общая информация\n\n");
        output.append(generateGeneralInfoTable(statsHandler));

        output.append("#### Самые запрашиваемые ресурсы\n\n");
        output.append(generateTopRequestedResourcesTable(statsHandler));

        output.append("#### Самые запрашиваемые методы\n\n");
        output.append(generateTopRequestedMethodsTable(statsHandler));

        output.append("#### Коды ответа\n\n");
        output.append(generateResponseCodesTable(statsHandler));

        output.append("#### Самое активное время\n\n");
        output.append(generateActiveHoursTable(statsHandler));

        return output.toString();
    }

    /**
     * Генерация части с основной статистикой
     *
     * @param statsHandler Собранная статистика
     * @return Таблица с основной статистикой
     */
    private static String generateGeneralInfoTable(StatsHandler statsHandler) {
        StringBuilder output = new StringBuilder();
        output.append("|        Метрика        |     Значение |\n");
        output.append(DIVIDE_STRING);

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        String fromDate = statsHandler.fromTime() != null ? statsHandler.fromTime().format(dateTimeFormatter) : "-";
        String toDate = statsHandler.toTime() != null ? statsHandler.toTime().format(dateTimeFormatter) : "-";
        String file = statsHandler.files();

        output.append(String.format("|       Источник логов        | `%s` |\n", file));
        output.append(String.format("|    Начальная дата     |   %s |\n", fromDate));
        output.append(String.format("|     Конечная дата     |   %s |\n", toDate));
        output.append(String.format("|  Количество запросов  |       %,d |\n", statsHandler.requestCount()));
        output.append(String.format("| Средний размер ответа |         %db |\n", statsHandler.avgResponseSize()));
        output.append(String.format("|   95p размера ответа  |         %db |\n\n", statsHandler.percentile()));

        return output.toString();
    }

    /**
     * Генерирует таблицу для самых запрашиваемых ресурсах
     *
     * @param statsHandler Собранная статистика
     * @return Таблица с самыми запрашиваемыми ресурсами
     */
    public static String generateTopRequestedResourcesTable(StatsHandler statsHandler) {
        return generateTableWithSingleMetric("Ресурс", QUANTITY_STRING, statsHandler.sortRequestMap(
            statsHandler.requestStats().requestResourceCounts()));
    }

    /**
     * Генерирует таблицу для самых запрашиваемых методов
     *
     * @param statsHandler Собранная статистика
     * @return Таблица с самыми запрашиваемыми методами
     */
    public static String generateTopRequestedMethodsTable(StatsHandler statsHandler) {
        return generateTableWithSingleMetric("Метод", QUANTITY_STRING, statsHandler.sortRequestMap(
            statsHandler.requestStats().requestMethodsCounts()));
    }

    /**
     * Генерирует таблицу для самых частых кодов ответа сервера
     *
     * @param statsHandler Собранная статистика
     * @return Таблица с самыми частыми кодами ответа сервера
     */
    private static String generateResponseCodesTable(StatsHandler statsHandler) {
        StringBuilder output = new StringBuilder();
        output.append("| Код |          Имя          | Количество |\n");
        output.append("|:---:|:---------------------:|-----------:|\n");

        int count = 0;
        for (Map.Entry<Integer, Integer> entry : statsHandler.responseCodeStats()
            .sortCodeMap(statsHandler.responseCodeStats().responseCodesMap()).entrySet()) {
            if (count < COUNT_OF_FIRST_VALUES) {
                String codeName = getStatusName(entry.getKey());
                output.append(String.format("| %d | %-20s | %,d |\n", entry.getKey(), codeName, entry.getValue()));
                count++;
            }
        }
        return output.toString();
    }

    /**
     * Генерирует таблицу с самыми активными часами записи логов
     *
     * @param statsHandler Собранная статистика
     * @return Таблица с самыми активными часами записи логов
     */
    public static String generateActiveHoursTable(StatsHandler statsHandler) {
        return generateTableWithSingleMetric("Час", QUANTITY_STRING, statsHandler.sortRequestMap(
            statsHandler.dateStats().hoursRequestCounts()));
    }

    /**
     * Генерирует таблицу по определенным параметрам
     *
     * @param metricName Параметр
     * @param valueName Количество
     * @param data Собранная статистика по параметру
     * @return Таблица по определенным параметрам
     */
    private static String generateTableWithSingleMetric(
        String metricName,
        String valueName,
        Map<String, Integer> data
    ) {
        StringBuilder output = new StringBuilder();
        output.append(String.format("|     %s      | %s |\n", metricName, valueName));
        output.append(DIVIDE_STRING);

        int count = 0;
        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            if (count < COUNT_OF_FIRST_VALUES) {
                output.append(String.format(VAR_FORMAT_STRING, entry.getKey(), entry.getValue()));
                count++;
            }
        }
        output.append('\n');
        return output.toString();
    }

    /**
     * Метод для получения описания кода ответа сервера
     *
     * @param code Код ответа
     * @return Описание
     */
    private String getStatusName(int code) {
        for (Code status : Code.values()) {
            if (status.code() == code) {
                return status.description();
            }
        }
        return "Unknown";
    }
}
