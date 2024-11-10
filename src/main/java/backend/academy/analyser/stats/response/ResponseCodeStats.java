package backend.academy.analyser.stats.response;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

/**
 * Класс для сбора статистики по ответам сервера
 */
public class ResponseCodeStats {

    @Getter
    @Setter
    private HashMap<Integer, Integer> responseCodesMap;

    public ResponseCodeStats() {
        responseCodesMap = new HashMap<>();
        initResponseCodeStats();
    }

    /**
     * Инициализация мапы ответов сервера
     */
    private void initResponseCodeStats() {
        for (Code code : Code.values()) {
            responseCodesMap.put(code.code(), 0);
        }
    }

    /**
     * Прибавляет единицу в мапе к встреченному коду ответа
     *
     * @param code Текущий код ответа
     */
    public void changeResponseCodeStats(int code) {
        responseCodesMap.put(code, responseCodesMap.getOrDefault(code, 0) + 1);
    }

    /**
     * Сортирует мапу с кодами ответа по убыванию
     *
     * @param map Мапа встреченных кодов ответа
     * @return Отсортированная мапа встреченных кодов ответа
     */
    public LinkedHashMap<Integer, Integer> sortCodeMap(HashMap<Integer, Integer> map) {
        List<Map.Entry<Integer, Integer>> list = new LinkedList<>(map.entrySet());

        list.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

        LinkedHashMap<Integer, Integer> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<Integer, Integer> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }
}
