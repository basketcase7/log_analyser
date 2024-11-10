package backend.academy.analyser.stats;

import backend.academy.analyser.NginxLogEntity;
import backend.academy.analyser.stats.date.DateStats;
import backend.academy.analyser.stats.request.RequestStats;
import backend.academy.analyser.stats.response.ResponseCodeStats;
import com.google.common.math.Quantiles;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import lombok.Getter;

/**
 * Основной класс по сбору статистики
 */
public class StatsHandler {

    @Getter
    private final RequestStats requestStats;
    @Getter
    private final ResponseCodeStats responseCodeStats;
    @Getter
    private final DateStats dateStats;

    @Getter
    private int requestCount;
    @Getter
    private long sumResponseSize;
    @Getter
    private String files;
    @Getter
    private LocalDateTime fromTime;
    @Getter
    private LocalDateTime toTime;
    @Getter
    private int avgResponseSize;
    @Getter
    private int percentile;

    private final List<Integer> responseSizes;

    private static final int PERCENTILE = 95;

    /**
     * Считает количество обработанных логов
     */
    private void addRequestCount() {
        requestCount++;
    }

    public StatsHandler(String files, LocalDateTime fromTime, LocalDateTime toTime) {
        this.fromTime = fromTime;
        this.toTime = toTime;
        this.files = files;
        requestStats = new RequestStats();
        responseCodeStats = new ResponseCodeStats();
        responseSizes = new ArrayList<>();
        dateStats = new DateStats();
        requestCount = 0;
        sumResponseSize = 0;
    }

    /**
     * Обновляет статистику во время обратки каждого лога
     *
     * @param nginxLogEntity Текущий лог
     */
    public void updateStats(NginxLogEntity nginxLogEntity) {
        addRequestCount();
        sumResponseSize += nginxLogEntity.bodyBytesSent();
        responseCodeStats.changeResponseCodeStats(nginxLogEntity.status());
        requestStats.changeRequestStats(nginxLogEntity);
        dateStats.changeDateStats(nginxLogEntity);
        responseSizes.add(nginxLogEntity.bodyBytesSent());
    }

    /**
     * Считает средний размер ответа и перцентиль размера ответа сервера
     */
    public void countStats() {
        if (fromTime == LocalDateTime.MIN) {
            fromTime = null;
        }
        if (toTime == LocalDateTime.MAX) {
            toTime = null;
        }
        countResponseAvgSize();
        calculatePercentile();
    }

    /**
     * Логика подсчета среднего размера ответа сервера
     */
    private void countResponseAvgSize() {
        if (requestCount == 0) {
            avgResponseSize = 0;
        } else {
            avgResponseSize = (int) (sumResponseSize / requestCount);
        }
    }

    /**
     * Логика подсчета перцентиля размера ответа сервера
     */
    private void calculatePercentile() {
        if (responseSizes.isEmpty()) {
            percentile = 0;
            return;
        }

        Collections.sort(responseSizes);
        percentile = (int) Quantiles.percentiles().index(PERCENTILE).compute(responseSizes);
    }

    /**
     * Сортирует мапу из строки и числа по убыванию
     *
     * @param map Текущая мапа
     * @return Отсортированная по убыванию мапа
     */
    public LinkedHashMap<String, Integer> sortRequestMap(Map<String, Integer> map) {
        List<Map.Entry<String, Integer>> list = new LinkedList<>(map.entrySet());

        list.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

        LinkedHashMap<String, Integer> sortedMap = new LinkedHashMap<>();

        for (Map.Entry<String, Integer> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }
}
