package backend.academy.analyser.argument;

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import lombok.Getter;

/**
 * Класс для валидации и конвертации аргументов командной строки
 */
@Getter
public class CommandLineArgs {
    @Parameter(names = "--path", validateWith = ValidatorPath.class, required = true)
    private String filePath;
    @Parameter(names = "--from", converter = DateConverter.class)
    private LocalDateTime fromTime;
    @Parameter(names = "--to", converter = DateConverter.class)
    private LocalDateTime toTime;
    @Parameter(names = "--format", validateWith = ValidatorFormat.class)
    private String outFormat;
    @Parameter(names = "--filter-field")
    private String filterField;
    @Parameter(names = {"--filter-value"})
    private String filterValue;

    /**
     * Валидация аргумента пути к лог-файлам
     */
    public static class ValidatorPath implements IParameterValidator {

        @Override
        public void validate(String name, String value) throws ParameterException {
            if (!value.contains("\\") && !value.contains(".") && !value.contains("http://")
                && !value.contains("https://") && !value.contains("ftp://")) {
                throw new ParameterException("Invalid path: " + value);
            }
        }
    }

    /**
     * Валидация формата записи статистики
     */
    public static class ValidatorFormat implements IParameterValidator {

        @Override
        public void validate(String name, String value) throws ParameterException {
            if (!formatIsValid(value)) {
                throw new ParameterException("Unknown format: " + value);
            }
        }

        private boolean formatIsValid(String formatString) {
            return "markdown".equals(formatString) || "adoc".equals(formatString);
        }
    }

    /**
     * Конвертация даты, введенной пользователем
     */
    public static class DateConverter implements IStringConverter<LocalDateTime> {
        @Override
        public LocalDateTime convert(String isoDateTime) {
            try {
                ZonedDateTime zonedDateTime = ZonedDateTime.parse(isoDateTime);
                return zonedDateTime.toLocalDateTime();
            } catch (DateTimeParseException e) {
                DateTimeParseException newException =
                    new DateTimeParseException("Error with parse date" + e.getMessage(), isoDateTime, 0);
                newException.initCause(e);
                throw newException;
            }
        }
    }

}
