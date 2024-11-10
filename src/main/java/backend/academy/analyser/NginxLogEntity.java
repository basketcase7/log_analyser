package backend.academy.analyser;

import java.time.LocalDateTime;

/**
 * Класс сущности nginx лога
 *
 * @param remoteAddr    Адрес клиента
 * @param remoteUser    Имя пользователя
 * @param timeLocal     Время запроса
 * @param request       Полный запрос
 * @param status        Код статуса
 * @param bodyBytesSent Кол-во байтов в теле ответа
 * @param httpReferer   URL страницы, с которой совершен переход
 * @param httpUserAgent Строка user-agent
 */
public record NginxLogEntity(String remoteAddr,
                             String remoteUser,
                             LocalDateTime timeLocal,
                             String request,
                             int status,
                             int bodyBytesSent,
                             String httpReferer,
                             String httpUserAgent) {
}
