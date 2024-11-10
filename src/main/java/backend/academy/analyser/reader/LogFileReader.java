package backend.academy.analyser.reader;

/**
 * Интерфейс, который реализуют класса чтения по локальному пути и по URL
 */
public interface LogFileReader {
    void read(String path);
}
