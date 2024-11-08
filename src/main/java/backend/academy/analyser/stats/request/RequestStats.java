package backend.academy.analyser.stats.request;

import backend.academy.analyser.NginxLogEntity;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import static backend.academy.analyser.parser.HttpRequestParser.parseHttpRequest;

@Getter
public class RequestStats {

    private final Map<String, Integer> requestResourceCounts;
    private final Map<String, Integer> requestMethodsCounts;

    public RequestStats() {
        requestResourceCounts = new HashMap<>();
        requestMethodsCounts = new HashMap<>();
    }

    public void changeRequestStats(NginxLogEntity nginxLogEntity) {
        String[] parsedHttpRequest = parseHttpRequest(nginxLogEntity.request());
        int methodPositionInHttpRequest = 0;
        int resourcePositionInHttpRequest = 1;
        requestResourceCounts.put(parsedHttpRequest[resourcePositionInHttpRequest],
            requestResourceCounts.getOrDefault(parsedHttpRequest[resourcePositionInHttpRequest], 0) + 1);
        requestMethodsCounts.put(parsedHttpRequest[methodPositionInHttpRequest],
            requestMethodsCounts.getOrDefault(parsedHttpRequest[methodPositionInHttpRequest], 0) + 1);
    }
}
