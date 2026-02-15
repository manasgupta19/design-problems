package Tier1.Pastebin.exception;

public class PasteNotFoundException extends RuntimeException {
    public PasteNotFoundException(String message) { super(message); }
}
