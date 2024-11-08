    package backend.academy.analyser.stats.date;

    import backend.academy.analyser.NginxLogEntity;
    import java.util.HashMap;
    import java.util.Map;
    import lombok.Getter;

    @Getter
    public class DateStats {

        private final Map<String, Integer> hoursRequestCounts;

        public DateStats() {
            hoursRequestCounts = new HashMap<>();
        }

        public void changeDateStats(NginxLogEntity nginxLogEntity) {
            String hour = String.valueOf(nginxLogEntity.timeLocal().getHour());
            hoursRequestCounts.put(hour, hoursRequestCounts.getOrDefault(hour, 0) + 1);
        }
    }
