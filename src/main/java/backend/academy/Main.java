package backend.academy;

import backend.academy.analyser.LogAnalyserHandler;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Main {
    public static void main(String[] args) {

        LogAnalyserHandler logAnalyserHandler = new LogAnalyserHandler(args);
        logAnalyserHandler.handle();
    }
}
