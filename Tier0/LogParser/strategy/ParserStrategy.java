package Tier0.LogParser.strategy;

import java.util.Optional;

import Tier0.LogParser.model.LogEntry;

// 2. The Strategy Interface
public interface ParserStrategy {
    Optional<LogEntry> parse(String line);
}