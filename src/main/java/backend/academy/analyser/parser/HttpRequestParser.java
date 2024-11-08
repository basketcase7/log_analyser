package backend.academy.analyser.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.experimental.UtilityClass;

@UtilityClass
public class HttpRequestParser {

    private static final String REQUEST_REGEX = "(\\S+)\\s(\\S+)\\s(HTTP/\\d\\.\\d)";
    private static final int HTTP_REQUEST_METHOD_GROUP = 1;
    private static final int HTTP_REQUEST_PATH_GROUP = 2;
    private static final int HTTP_REQUEST_PROTOCOL_GROUP = 3;

    public static String[] parseHttpRequest(String httpRequest) {
        Pattern pattern = Pattern.compile(REQUEST_REGEX);
        Matcher matcher = pattern.matcher(httpRequest);

        if (matcher.matches()) {
            String method = matcher.group(HTTP_REQUEST_METHOD_GROUP);
            String path = matcher.group(HTTP_REQUEST_PATH_GROUP);
            String protocol = matcher.group(HTTP_REQUEST_PROTOCOL_GROUP);
            return new String[] {method, path, protocol};
        }
        return new String[0];
    }
}
