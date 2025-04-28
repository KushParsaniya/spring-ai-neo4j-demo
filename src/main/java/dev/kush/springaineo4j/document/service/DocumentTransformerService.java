package dev.kush.springaineo4j.document.service;

import org.springframework.ai.document.Document;

import java.util.List;

public interface DocumentTransformerService {

    List<Document> transformTextDocuments(List<Document> documents);

    List<Document> keywordMetadataEnrichment(List<Document> documents);

    List<Document> summaryMetadataEnrichment(List<Document> documents);
}
