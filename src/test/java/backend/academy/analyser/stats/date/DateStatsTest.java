package backend.academy.analyser.stats.date;

import backend.academy.analyser.NginxLogEntity;
import java.time.LocalDateTime;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("Тесты для класса со сбором статистики по времени")
public class DateStatsTest {

    private DateStats dateStats;
    private NginxLogEntity nginxLogEntity;

    @BeforeEach
    public void setUp() {
        dateStats = new DateStats();
        nginxLogEntity = mock(NginxLogEntity.class);
    }

    @DisplayName("Проверка инициализации мапы со статистикой часов")
    @Test
    void testInitHoursRequestCounts() {
        Map<String, Integer> hoursRequestCounts = dateStats.hoursRequestCounts();

        assertEquals(0, hoursRequestCounts.size());
    }

    @DisplayName("Проверка метода собирающего статистику логов по часам")
    @Test
    void testChangeDateStats() {
        String hour = "8";
        when(nginxLogEntity.timeLocal()).thenReturn(LocalDateTime.of(2022, 5, 8, 8, 1));

        dateStats.changeDateStats(nginxLogEntity);
        dateStats.changeDateStats(nginxLogEntity);

        assertEquals(2, dateStats.hoursRequestCounts().get(hour));
    }

}
