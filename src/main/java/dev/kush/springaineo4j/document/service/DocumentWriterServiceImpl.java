package dev.kush.springaineo4j.document.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentWriterServiceImpl implements DocumentWriterService {

    private final VectorStore vectorStore;

    @Override
    public void writeDocuments(List<Document> documents) {
        log.info("DocumentWriterServiceImpl :: writeDocuments :: start");
        vectorStore.accept(documents);
        log.info("DocumentWriterServiceImpl :: writeDocuments :: end");
    }
}
