package Tier0.LogParser;

import Tier0.LogParser.strategy.ParserStrategy;
import Tier0.LogParser.strategy.StandardLogParser;

public class Driver {
// Driver
    public static void main(String[] args) {
        ParserStrategy strategy = new StandardLogParser();

        // Mocking file reading for demonstration
        String sampleLine = "2023-10-27 10:00:00 ERROR [main] Connection timeout";
        strategy.parse(sampleLine).ifPresent(System.out::println);

        String sampleLine2 = "2023-10-27 10:00:00 INFO [worker] Job started";
        strategy.parse(sampleLine2).ifPresent(System.out::println); // Should print successfully

        String stackTrace = "\tat com.mycompany.Service.method(Service.java:10)";
        strategy.parse(stackTrace).ifPresent(System.out::println); // Should be empty due to fast fail check

        String malformedDate = "2023-99-99 10:00:00 INFO [main] Bad date";
        strategy.parse(malformedDate).ifPresent(System.out::println); // Should be empty due to date parse error
    }
}
