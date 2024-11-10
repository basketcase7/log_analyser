package backend.academy.analyser;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import static backend.academy.analyser.parser.HttpRequestParser.parseHttpRequest;

/**
 * Класс, осуществляющий фильтрацию логов по каким-то параметрам
 *
 * @param filterField Поле фильтрации
 * @param filterValue Значение фильтрации
 */
public record LogsFilter(String filterField, String filterValue) {

    /**
     * Фильтрует логи в зависимости от выбранного поля и значения лога
     *
     * @param nginxLogEntity Запрос на фильтрацию
     * @return Результат фильтрации лога по выбранному полю и значению
     */
    public boolean filter(NginxLogEntity nginxLogEntity) {
        Map<String, Function<NginxLogEntity, Boolean>> filterStrategies = new HashMap<>();
        int methodPosition = 0;
        int pathPosition = 1;
        int protocolPosition = 2;

        filterStrategies.put("remoteAddr", ng -> ng.remoteAddr().equals(filterValue));
        filterStrategies.put("remoteUser", ng -> ng.remoteUser().equals(filterValue));
        filterStrategies.put("requestMethod",
            ng -> parseHttpRequest(ng.request())[methodPosition].equals(filterValue.toUpperCase()));
        filterStrategies.put("requestPath", ng -> parseHttpRequest(ng.request())[pathPosition].contains(filterValue));
        filterStrategies.put("requestProtocol",
            ng -> parseHttpRequest(ng.request())[protocolPosition].equals(filterValue));
        filterStrategies.put("status", ng -> String.valueOf(ng.status()).equals(filterValue));
        filterStrategies.put("bytes", ng -> String.valueOf(ng.bodyBytesSent()).equals(filterValue));
        filterStrategies.put("referer", ng -> ng.httpReferer() != null && ng.httpReferer().contains(filterValue));
        filterStrategies.put("agent", ng -> ng.httpUserAgent() != null && ng.httpUserAgent().contains(filterValue));
        filterStrategies.put("date", ng -> ng.timeLocal().toString().equals(filterValue));

        Function<NginxLogEntity, Boolean> filterFunction = filterStrategies.get(filterField);

        if (filterFunction == null) {
            return true;
        }

        return filterFunction.apply(nginxLogEntity);
    }

    /**
     * Фильтрует логи в зависимости от того, попадают ли они в выбранный временной диапазон
     *
     * @param fromDateTime Дата, до которой логи фильтруются
     * @param toDateTime Дата, после которой логи фильтруются
     * @param actualDateTime Дата лога
     * @return Результат фильтрации лога по заданному временному диапазону
     */
    public boolean checkFromToDate(LocalDateTime fromDateTime, LocalDateTime toDateTime, LocalDateTime actualDateTime) {
        return actualDateTime.isAfter(fromDateTime) && actualDateTime.isBefore(toDateTime);
    }

}
