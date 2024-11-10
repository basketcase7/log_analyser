package backend.academy.analyser.argument;

import com.beust.jcommander.JCommander;
import lombok.Getter;

@Getter
public class ArgsParser {

    private CommandLineArgs cmdArgs;

    public void handleArgsParser(String[] args) {
        cmdArgs = new CommandLineArgs();
        JCommander jCommander = JCommander.newBuilder()
            .addObject(cmdArgs)
            .build();
        jCommander.parse(args);
    }
}
