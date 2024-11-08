package backend.academy.log_analyser;

import java.time.LocalDateTime;

public record NginxLogEntity(String remoteAddr,
                             String remoteUser,
                             LocalDateTime timeLocal,
                             String request,
                             int status,
                             int bodyBytesSent,
                             String httpReferer,
                             String httpUserAgent) {
}
