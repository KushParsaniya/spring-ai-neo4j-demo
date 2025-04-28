package dev.kush.springaineo4j.exception;

public class SummaryMetadataEnricherException extends RuntimeException {

    public SummaryMetadataEnricherException(String message) {
        super(message);
    }

    public SummaryMetadataEnricherException() {
        super("Summary Metadata Enricher Exception");
    }
}
