package Tier0.JSONParser.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Tier0.JSONParser.model.Token;
import Tier0.JSONParser.model.TokenType;

public class SimpleJsonParser {
    private final List<Token> tokens;
    private int pos = 0;
    private int currentDepth = 0;
    private static final int MAX_DEPTH = 50; // Prevention against StackOverflow [Source 251]

    public SimpleJsonParser(String json) {
        this.tokens = tokenize(json);
    }

    public Object parse() {
        Object result = parseValue();
        if (pos < tokens.size()) throw new RuntimeException("Extra characters after JSON");
        return result;
    }

    // ---------------------------------------------------------
    // 1. THE TOKENIZER (Lexical Analysis)
    // ---------------------------------------------------------
    private List<Token> tokenize(String json) {
        List<Token> list = new ArrayList<>();
        int i = 0;
        while (i < json.length()) {
            char c = json.charAt(i);

            // Skip whitespace
            if (Character.isWhitespace(c)) { i++; continue; }

            switch (c) {
                case '{': list.add(new Token(TokenType.BEGIN_OBJECT, null)); i++; break;
                case '}': list.add(new Token(TokenType.END_OBJECT, null)); i++; break;
                case '[': list.add(new Token(TokenType.BEGIN_ARRAY, null)); i++; break;
                case ']': list.add(new Token(TokenType.END_ARRAY, null)); i++; break;
                case ':': list.add(new Token(TokenType.COLON, null)); i++; break;
                case ',': list.add(new Token(TokenType.COMMA, null)); i++; break;
                case '"': // String Handling
                    int end = json.indexOf('"', i + 1);
                    // In production, handle escaped quotes here
                    if (end == -1) throw new RuntimeException("Unterminated string");
                    list.add(new Token(TokenType.STRING, json.substring(i + 1, end)));
                    i = end + 1;
                    break;
                default:
                    // Number, Boolean, Null handling
                    if (Character.isDigit(c) || c == '-') {
                        int start = i;
                        while (i < json.length() && (Character.isDigit(json.charAt(i)) || json.charAt(i) == '.')) i++;
                        list.add(new Token(TokenType.NUMBER, json.substring(start, i)));
                    } else if (json.startsWith("true", i)) {
                        list.add(new Token(TokenType.BOOLEAN, "true")); i += 4;
                    } else if (json.startsWith("false", i)) {
                        list.add(new Token(TokenType.BOOLEAN, "false")); i += 5;
                    } else if (json.startsWith("null", i)) {
                        list.add(new Token(TokenType.NULL, "null")); i += 4;
                    } else {
                        throw new RuntimeException("Illegal character: " + c);
                    }
            }
        }
        return list;
    }

    // ---------------------------------------------------------
    // 2. THE PARSER (Syntactic Analysis - Recursive Descent)
    // ---------------------------------------------------------

    // Core Dispatcher
    private Object parseValue() {
        if (pos >= tokens.size()) throw new RuntimeException("Unexpected EOF");
        Token token = tokens.get(pos++);

        switch (token.getType()) {
            case BEGIN_OBJECT: return parseObject();
            case BEGIN_ARRAY:  return parseArray();
            case STRING:       return token.getValue();
            case NUMBER:       return Double.parseDouble(token.getValue());
            case BOOLEAN:      return Boolean.parseBoolean(token.getValue());
            case NULL:         return null;
            default:           throw new RuntimeException("Unexpected token: " + token.getType());
        }
    }

    private Map<String, Object> parseObject() {
        // Operational Excellence: Depth Check [Source 214]
        if (++currentDepth > MAX_DEPTH) throw new RuntimeException("Max depth exceeded");

        Map<String, Object> map = new HashMap<>();

        // Handle empty object {}
        if (pos < tokens.size() && tokens.get(pos).getType() == TokenType.END_OBJECT) {
            pos++; currentDepth--; return map;
        }

        while (true) {
            // 1. Expect Key (String)
            Token keyToken = tokens.get(pos++);
            if (keyToken.getType() != TokenType.STRING) throw new RuntimeException("Expected String key");

            // 2. Expect Colon
            if (tokens.get(pos++).getType() != TokenType.COLON) throw new RuntimeException("Expected Colon");

            // 3. Expect Value (Recursive)
            map.put(keyToken.getValue(), parseValue());

            // 4. Expect Comma or End Object
            TokenType nextType = tokens.get(pos++).getType();
            if (nextType == TokenType.END_OBJECT) break;
            if (nextType != TokenType.COMMA) throw new RuntimeException("Expected comma or }");
        }

        currentDepth--;
        return map;
    }

    private List<Object> parseArray() {
        if (++currentDepth > MAX_DEPTH) throw new RuntimeException("Max depth exceeded");
        List<Object> list = new ArrayList<>();

        // Handle empty array []
        if (pos < tokens.size() && tokens.get(pos).getType() == TokenType.END_ARRAY) {
            pos++; currentDepth--; return list;
        }

        while (true) {
            // 1. Parse Value (Recursive)
            list.add(parseValue());

            // 2. Expect Comma or End Array
            TokenType nextType = tokens.get(pos++).getType();
            if (nextType == TokenType.END_ARRAY) break;
            if (nextType != TokenType.COMMA) throw new RuntimeException("Expected comma or ]");
        }

        currentDepth--;
        return list;
    }
}
