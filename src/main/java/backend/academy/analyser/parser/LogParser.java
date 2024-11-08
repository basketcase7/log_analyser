package backend.academy.analyser.parser;

import backend.academy.analyser.NginxLogEntity;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogParser {

    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss Z", Locale.US);

    private static final int REMOTE_ADDRESS_GROUP = 1;
    private static final int REMOTE_USER_GROUP = 2;
    private static final int TIME_GROUP = 3;
    private static final int REQUEST_GROUP = 4;
    private static final int STATUS_GROUP = 5;
    private static final int BYTES_GROUP = 6;
    private static final int HTTP_REFERER_GROUP = 7;
    private static final int HTTP_USER_AGENT_GROUP = 8;

    public NginxLogEntity parse(String logLine) {
        String regex = "(\\S+) - (\\S+) \\[(.+?)] \"(.+?)\" (\\d{3}) (\\d+) \"(.+?)\" \"(.+?)\"";

        Pattern parsePattern = Pattern.compile(regex);
        Matcher matcher = parsePattern.matcher(logLine);

        if (matcher.matches()) {
            return createNginxLogEntity(matcher);
        }
        return null;
    }

    private NginxLogEntity createNginxLogEntity(Matcher matcher) {

        String remoteAddr = matcher.group(REMOTE_ADDRESS_GROUP);
        String remoteUser = matcher.group(REMOTE_USER_GROUP);
        LocalDateTime timeLocal = parseLocalDateTime(matcher.group(TIME_GROUP));
        String request = matcher.group(REQUEST_GROUP);
        int status = Integer.parseInt(matcher.group(STATUS_GROUP));
        int bodyBytesSent = Integer.parseInt(matcher.group(BYTES_GROUP));
        String httpReferer = matcher.group(HTTP_REFERER_GROUP);
        String httpUserAgent = matcher.group(HTTP_USER_AGENT_GROUP);

        return new NginxLogEntity(remoteAddr,
            remoteUser,
            timeLocal, request,
            status, bodyBytesSent,
            httpReferer,
            httpUserAgent);
    }

    private LocalDateTime parseLocalDateTime(String logTime) {
        try {
            return LocalDateTime.parse(logTime, dateTimeFormatter);
        } catch (DateTimeParseException e) {
            System.err.println("Error parsing log line: " + logTime);
        }
        return null;
    }
}
