package backend.academy.analyser.reader;

import backend.academy.analyser.LogsFilter;
import backend.academy.analyser.parser.LogParser;
import backend.academy.analyser.stats.StatsHandler;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("Тесты для класса чтения локальных путей")
public class LocalFileReaderTest {

    private LocalFileReader localFileReader;
    private StatsHandler statsHandler;
    private LogsFilter logsFilter;
    private LogParser logParser;

    @TempDir
    public Path tempDir;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        statsHandler = mock(StatsHandler.class);
        logsFilter = mock(LogsFilter.class);
        logParser = mock(LogParser.class);

        localFileReader = new LocalFileReader(
            statsHandler,
            LocalDateTime.now(),
            LocalDateTime.now(),
            "filterField",
            "filterValue"
        );
    }

    @DisplayName("Проверка чтения конкретного файла")
    @Test
    void testReadWithFile() throws IOException {
        LocalFileReader spyFileReader = spy(localFileReader);

        Path tempFile = Files.createFile(tempDir.resolve("tempLogFile.txt"));

        spyFileReader.read(tempDir.toString());

        verify(spyFileReader).processFile(tempFile);
    }

    @DisplayName("Проверка чтения файлов в директории")
    @Test
    void testReadWithFiles() throws IOException {
        LocalFileReader spyFileReader = spy(localFileReader);

        Path tempFile1 = Files.createFile(tempDir.resolve("tempLogFile1.txt"));
        Path tempFile2 = Files.createFile(tempDir.resolve("tempLogFile2.txt"));

        spyFileReader.read(tempDir.toString());

        verify(spyFileReader).processFile(tempFile1);
        verify(spyFileReader).processFile(tempFile2);
    }

    @DisplayName("Проверка чтения файлов по шаблону")
    @Test
    void testReadWithWildcard() throws IOException {
        LocalFileReader spyFileReader = spy(localFileReader);

        Path directory1 = Files.createDirectory(tempDir.resolve("directory1"));
        Path directory2 = Files.createDirectory(tempDir.resolve("directory2"));

        Path tempFile1 = Files.createFile(directory1.resolve("tempLogFile1.txt"));
        Path tempFile2 = Files.createFile(directory2.resolve("tempLogFile2.txt"));
        Path alienFile = Files.createFile(directory1.resolve("alienLogFile.txt"));

        spyFileReader.read(tempDir.toString() + "/**/temp*.txt");

        verify(spyFileReader, times(1)).processFile(tempFile1);
        verify(spyFileReader, times(1)).processFile(tempFile2);
        verify(spyFileReader, times(0)).processFile(alienFile);
    }

}
