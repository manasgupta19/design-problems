package Tier0.LogParser.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.function.Consumer;

import Tier0.LogParser.model.LogEntry;
import Tier0.LogParser.strategy.ParserStrategy;

public class PrincipalLogParser {

    // 4. The Processor (Streaming Engine)
    public void processLogs(String filePath, ParserStrategy parser, Consumer<LogEntry> sink) {
        // Optimization: BufferedReader for O(1) Memory on large files
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                parser.parse(line).ifPresent(sink);
            }
        } catch (IOException e) {
            System.err.println("IO Error processing logs: " + e.getMessage());
        }
    }

    
}


