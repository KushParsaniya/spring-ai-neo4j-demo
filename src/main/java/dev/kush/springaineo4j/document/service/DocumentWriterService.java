package dev.kush.springaineo4j.document.service;

import org.springframework.ai.document.Document;

import java.util.List;

public interface DocumentWriterService {
    void writeDocuments(List<Document> documents);
}
