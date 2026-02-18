package Tier0.LogParser.strategy;

import Tier0.LogParser.model.LogEntry;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// 3. Concrete Implementation: Regex Optimized Parser
public class StandardLogParser implements ParserStrategy {
    // Optimization: Compile Pattern ONCE [Source 1008]
    // Regex: Date Space Level Space [Thread] Space Message
    // Example: "2023-10-27 10:00:00 ERROR [main] Database connection failed"
    private final Pattern LOG_PATTERN = Pattern.compile(
        "^(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2})\\s+(\\w+)\\s+\\[(.*?)\\]\\s+(.*)$"
    );

    private final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public Optional<LogEntry> parse(String line) {
        // Optimization: Fast Fail check [Source 1005]
        if (line == null || line.isEmpty() || !Character.isDigit(line.charAt(0))) {
            return Optional.empty();
        }

        Matcher matcher = LOG_PATTERN.matcher(line);
        if (matcher.find()) {
            try {
                LocalDateTime ts = LocalDateTime.parse(matcher.group(1), DATE_FMT);
                String level = matcher.group(2);
                String msg = matcher.group(4); // Skipping thread group(3) for simplicity
                return Optional.of(new LogEntry(ts, level, msg));
            } catch (Exception e) {
                // Log malformed date errors internally, don't crash pipeline
                return Optional.empty();
            }
        }
        return Optional.empty();
    }
}
