package backend.academy.analyser.stats.response;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

public class ResponseCodeStats {

    @Getter
    @Setter
    private HashMap<Integer, Integer> responseCodesMap;

    public ResponseCodeStats() {
        responseCodesMap = new HashMap<>();
        initResponseCodeStats();
    }

    private void initResponseCodeStats() {
        for (Code code : Code.values()) {
            responseCodesMap.put(code.code(), 0);
        }
    }

    public void changeResponseCodeStats(int code) {
        responseCodesMap.put(code, responseCodesMap.getOrDefault(code, 0) + 1);
    }

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
