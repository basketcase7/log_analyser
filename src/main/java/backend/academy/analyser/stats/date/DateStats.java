    package backend.academy.analyser.stats.date;

    import backend.academy.analyser.NginxLogEntity;
    import java.util.HashMap;
    import java.util.Map;
    import lombok.Getter;

    /**
     * Собирает статистику по датам логов
     */
    @Getter
    public class DateStats {

        private final Map<String, Integer> hoursRequestCounts;

        public DateStats() {
            hoursRequestCounts = new HashMap<>();
        }

        /**
         * Добавляет в мапу с часами логов единицу в зависимости от времени в логе
         *
         * @param nginxLogEntity Текущий лог
         */
        public void changeDateStats(NginxLogEntity nginxLogEntity) {
            String hour = String.valueOf(nginxLogEntity.timeLocal().getHour());
            hoursRequestCounts.put(hour, hoursRequestCounts.getOrDefault(hour, 0) + 1);
        }
    }
