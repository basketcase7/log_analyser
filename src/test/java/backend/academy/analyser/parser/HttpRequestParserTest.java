package backend.academy.analyser.parser;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@DisplayName("Тесты парсера Http-запроса")
public class HttpRequestParserTest {

    @DisplayName("Проверка парсинга строки с корректным запросом")
    @Test
    void testParseValidHttpRequest() {
        String httpRequest = "GET /downloads/product_1 HTTP/1.1";

        String[] parsedRequest = HttpRequestParser.parseHttpRequest(httpRequest);

        assertEquals(3, parsedRequest.length);
        assertEquals(parsedRequest[0], "GET");
        assertEquals(parsedRequest[1], "/downloads/product_1");
        assertEquals(parsedRequest[2], "HTTP/1.1");
    }

    @DisplayName("Проверка корректности данных после парсинга")
    @Test
    void testParseInvalidHttpRequest() {
        String httpRequest = "HEAD /downloads/product_2 HTTP/2.0";

        String[] parsedRequest = HttpRequestParser.parseHttpRequest(httpRequest);

        assertNotEquals(0, parsedRequest.length);
        assertNotEquals(parsedRequest[0], "GET");
        assertNotEquals(parsedRequest[1], "/downloads/product_1");
        assertNotEquals(parsedRequest[2], "HTTP/1.1");
    }

    @DisplayName("Проверка парсинга строки с пропущенным полем")
    @Test
    void testParseSkippedFieldHttpRequest() {
        String httpRequest = "/downloads/product_1 HTTP/1.1";

        String[] parsedRequest = HttpRequestParser.parseHttpRequest(httpRequest);

        assertEquals(0, parsedRequest.length);
    }

    @DisplayName("Проверка парсинга пустой строки")
    @Test
    void testParseEmptyHttpRequest() {
        String httpRequest = "";

        String[] parsedRequest = HttpRequestParser.parseHttpRequest(httpRequest);

        assertEquals(0, parsedRequest.length);
    }
}
