package Tier0.LogParser.model;

import java.time.LocalDateTime;

// 1. Data Object (Immutable DTO)
public class LogEntry {
    final LocalDateTime timestamp;
    final String level;
    final String message;

    public LogEntry(LocalDateTime t, String l, String m) {
        this.timestamp = t; this.level = l; this.message = m;
    }
    @Override public String toString() { return timestamp + " [" + level + "] " + message; }
}
