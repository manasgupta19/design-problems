package Tier0.JSONParser.model;

public class Token {
    TokenType type;
    String value; // Only used for STRING/NUMBER
    public Token(TokenType type, String value) { this.type = type; this.value = value; }

    public TokenType getType() { return type; }
    public String getValue() { return value; }
}
