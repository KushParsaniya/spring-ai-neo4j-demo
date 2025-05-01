package dev.kush.springaineo4j.document.service;

import dev.kush.springaineo4j.config.UserUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.ParagraphPdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class DocumentReaderServiceImpl implements DocumentReaderService {

    @Override
    public List<Document> readTextDocuments(Resource resource, String fileName) {
        log.info("DocumentReaderServiceImpl :: readTextDocuments :: start");
        TextReader textReader = new TextReader(resource);
        // TODO: USE username from security context
        textReader.getCustomMetadata().put("username", UserUtils.getCurrentUser());
        textReader.getCustomMetadata().put("source", fileName);
        log.info("DocumentReaderServiceImpl :: readTextDocuments :: end");
        return textReader.read();

    }

    @Override
    public List<Document> readPdfDocuments(Resource resource, String fileName) {
        try {
            log.info("DocumentReaderServiceImpl :: readPdfDocuments :: start");
            PdfDocumentReaderConfig config = PdfDocumentReaderConfig
                    .builder()
                    .withPageTopMargin(0)
                    .withPagesPerDocument(1)
                    .withPageExtractedTextFormatter(ExtractedTextFormatter.builder()
                            .withNumberOfTopTextLinesToDelete(0)
                            .build())
                    .build();
            ParagraphPdfDocumentReader paragraphPdfDocumentReader = new ParagraphPdfDocumentReader(resource, config);
            List<Document> documents = paragraphPdfDocumentReader.read();
            for (Document document : documents) {
                document.getMetadata().put("source", fileName);
                document.getMetadata().put("username", UserUtils.getCurrentUser());
            }
            log.info("DocumentReaderServiceImpl :: readPdfDocuments :: end");
            return documents;
        } catch (IllegalArgumentException e) {
            log.warn("DocumentReaderServiceImpl :: readPdfDocuments :: error", e);
            log.info("Table of contents not found, trying to read as page pdf");
            return readPagePdfDocument(resource, fileName);
        } catch (Exception e) {
            log.error("DocumentReaderServiceImpl :: readPdfDocuments :: error", e);
            throw new RuntimeException("Error reading PDF document", e);
        }
    }

    @Override
    public List<Document> readPagePdfDocument(Resource resource, String fileName) {
        log.info("DocumentReaderServiceImpl :: readPagePdfDocument :: start");
        PdfDocumentReaderConfig config = PdfDocumentReaderConfig
                .builder()
                .withPageTopMargin(0)
                .withPagesPerDocument(1)
                .withPageExtractedTextFormatter(ExtractedTextFormatter.builder()
                        .withNumberOfTopTextLinesToDelete(0)
                        .build())
                .build();
        PagePdfDocumentReader pagePdfDocumentReader = new PagePdfDocumentReader(resource, config);
        List<Document> documents = pagePdfDocumentReader.read();
        for (Document document : documents) {
            document.getMetadata().put("source", fileName);
            // TODO: USE username from security context
            document.getMetadata().put("username", UserUtils.getCurrentUser());
        }
        log.info("DocumentReaderServiceImpl :: readPagePdfDocument :: end");
        return documents;
    }
}
