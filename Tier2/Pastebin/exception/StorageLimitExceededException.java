package Tier2.Pastebin.exception;

public class StorageLimitExceededException extends RuntimeException {
    public StorageLimitExceededException(String message) { super(message); }
}
