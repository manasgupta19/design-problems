package Tier0.JSONParser.model;

// Enum for Token types to decouple raw chars from logic
public enum TokenType {
    BEGIN_OBJECT, END_OBJECT, BEGIN_ARRAY, END_ARRAY,
        STRING, NUMBER, BOOLEAN, NULL, COLON, COMMA
}
