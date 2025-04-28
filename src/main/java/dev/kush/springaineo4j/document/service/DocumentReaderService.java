package dev.kush.springaineo4j.document.service;

import org.springframework.ai.document.Document;
import org.springframework.core.io.Resource;

import java.util.List;

public interface DocumentReaderService {
    List<Document> readTextDocuments(Resource resource, String fileName);

    List<Document> readPdfDocuments(Resource resource, String fileName);

    List<Document> readPagePdfDocument(Resource resource, String fileName);
}
