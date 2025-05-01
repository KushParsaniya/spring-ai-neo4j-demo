package dev.kush.springaineo4j.document.service;

import org.springframework.ai.document.Document;

import java.util.List;

public interface DocumentSerializationService {

    String serializeDocuments(List<Document> documents);

    List<Document> deserializeDocuments(String serializedDocuments);

    String serializeDocument(Document document);

    Document deserializeDocument(String serializedDocument);
}
