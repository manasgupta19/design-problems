package Tier1.LogParser.strategy;

import java.util.Optional;

import Tier1.LogParser.model.LogEntry;

// 2. The Strategy Interface
public interface ParserStrategy {
    Optional<LogEntry> parse(String line);
}