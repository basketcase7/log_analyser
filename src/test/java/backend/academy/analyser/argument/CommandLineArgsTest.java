package backend.academy.analyser.argument;

import com.beust.jcommander.ParameterException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Тест валидации аргументов")
public class CommandLineArgsTest {

    @DisplayName("Проверка валидации формата пути к логам с корректными путями")
    @Test
    void testValidatorPathValid() {
        CommandLineArgs.ValidatorPath validatorPath = new CommandLineArgs.ValidatorPath();

        assertDoesNotThrow(() -> validatorPath.validate("--path", "C:\\Users\\User\\logs"));
        assertDoesNotThrow(() -> validatorPath.validate("--path", "http://google.com"));
        assertDoesNotThrow(() -> validatorPath.validate("--path", "https://google.com"));
    }

    @DisplayName("Проверка валидации формата пути к логам с некорректными путями")
    @Test
    void testValidatorPathInvalid() {
        CommandLineArgs.ValidatorPath validatorPath = new CommandLineArgs.ValidatorPath();

        assertThrows(ParameterException.class, () -> validatorPath.validate("--path", "path"));
    }

    @DisplayName("Проверка валидации формата вывода с корректным форматом")
    @Test
    void testValidatorFormatValid() {
        CommandLineArgs.ValidatorFormat validatorFormat = new CommandLineArgs.ValidatorFormat();

        assertDoesNotThrow(() -> validatorFormat.validate("--format", "markdown"));
        assertDoesNotThrow(() -> validatorFormat.validate("--format", "adoc"));
    }

    @DisplayName("Проверка валидации формата вывода с некорректным форматом")
    @Test
    void testValidatorFormatInvalid() {
        CommandLineArgs.ValidatorFormat validatorFormat = new CommandLineArgs.ValidatorFormat();

        assertThrows(ParameterException.class, () -> validatorFormat.validate("--format", "json"));
        assertThrows(ParameterException.class, () -> validatorFormat.validate("--format", "html"));
    }

    @DisplayName("Проверка конвертации аргумента с датой в класс LocalDateTime с корректной введенной датой")
    @Test
    void testDateConverterValid() {
        CommandLineArgs.DateConverter dateConverter = new CommandLineArgs.DateConverter();

        LocalDateTime dateTime = dateConverter.convert("2024-11-10T12:30:00+00:00");

        assertNotNull(dateTime);
        assertEquals(2024, dateTime.getYear());
        assertEquals(11, dateTime.getMonthValue());
        assertEquals(10, dateTime.getDayOfMonth());
    }

    @DisplayName("Проверка конвертации аргумента с датой в класс LocalDateTime с некорректной введенной датой")
    @Test
    void testDateConverterInvalid() {
        CommandLineArgs.DateConverter dateConverter = new CommandLineArgs.DateConverter();

        assertThrows(DateTimeParseException.class, () -> dateConverter.convert("2024-11-10T12:30:00"));
    }

    @DisplayName("Проверка валидации аргументов в целом с корректными аргументами")
    @Test
    void testCommandLineArgsValidArgs() {
        String[] args = {
            "--path", "C:\\Users\\User\\logs\\log.txt",
            "--from", "2024-11-10T12:30:00+00:00",
            "--to", "2024-11-11T12:30:00+00:00",
            "--format", "markdown",
            "--filter-field", "userAgent",
            "--filter-value", "Mozilla"
        };

        CommandLineArgs commandLineArgs = new CommandLineArgs();
        assertDoesNotThrow(() -> com.beust.jcommander.JCommander.newBuilder()
            .addObject(commandLineArgs)
            .build()
            .parse(args));

        assertEquals("C:\\Users\\User\\logs\\log.txt", commandLineArgs.filePath());
        assertNotNull(commandLineArgs.fromTime());
        assertNotNull(commandLineArgs.toTime());
        assertEquals("markdown", commandLineArgs.outFormat());
        assertEquals("userAgent", commandLineArgs.filterField());
        assertEquals("Mozilla", commandLineArgs.filterValue());
    }

    @DisplayName("Проверка валидации аргументов в целом с некорректными аргументами")
    @Test
    void testCommandLineArgsInvalidArgs() {
        String[] args = {
            "--path", "C:\\Users\\User\\logs\\log.txt",
            "--from", "2024-11-10T12:30:00+00:00",
            "--to", "2024-11-10T12:30:00+00:00",
            "--format", "json",
            "--filter-field", "field",
            "--filter-value", "value"
        };

        CommandLineArgs commandLineArgs = new CommandLineArgs();
        assertThrows(com.beust.jcommander.ParameterException.class, () ->
            com.beust.jcommander.JCommander.newBuilder()
                .addObject(commandLineArgs)
                .build()
                .parse(args));
    }

    @DisplayName("Проверка валидации аргументов при введении аргумента, который не предусмотрен")
    @Test
    void testAlienArgument() {
        String[] args = {
            "--path", "C:\\Users\\User\\logs\\log.txt",
            "--from", "2024-11-10T12:30:00+00:00",
            "--to", "2024-11-11T12:30:00+00:00",
            "--format", "markdown",
            "--filter-field", "userAgent",
            "--filter-value", "Mozilla",
            "--alienArg", "arg"
        };

        CommandLineArgs commandLineArgs = new CommandLineArgs();
        assertThrows(com.beust.jcommander.ParameterException.class, () ->
            com.beust.jcommander.JCommander.newBuilder()
                .addObject(commandLineArgs)
                .build()
                .parse(args));
    }
}
