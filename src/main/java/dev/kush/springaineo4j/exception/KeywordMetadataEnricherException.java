package dev.kush.springaineo4j.exception;

public class KeywordMetadataEnricherException extends RuntimeException {

    public KeywordMetadataEnricherException(String message) {
        super(message);
    }

    public KeywordMetadataEnricherException() {
        super("KeywordMetadataEnricherException :: error");
    }
}
