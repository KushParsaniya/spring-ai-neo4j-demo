package dev.kush.springaineo4j.document.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.ai.document.Document;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ETLServiceImpl implements ETLService {

    private final DocumentReaderService documentReaderService;
    private final DocumentTransformerService documentTransformerService;
    private final DocumentWriterService documentWriterService;
    private final AzureBlobServiceImpl azureBlobService;

    @Override
    public void extractAndTransformAndLoad(MultipartFile file) {
        try {
            Resource resource = new ByteArrayResource(file.getBytes());
            String fileName = file.getOriginalFilename();
            List<Document> readDocuments = readFile(fileName, resource);
            List<Document> transformedDocuments = documentTransformerService.transformTextDocuments(readDocuments);
            List<Document> keywordMetadataEnrichmentDocuments = documentTransformerService.keywordMetadataEnrichment(transformedDocuments);
            List<Document> summaryMetadataEnrichmentDocuments = documentTransformerService.summaryMetadataEnrichment(keywordMetadataEnrichmentDocuments);
            documentWriterService.writeDocuments(summaryMetadataEnrichmentDocuments);
        } catch (Exception e) {
            log.error("Error processing file: " + file.getOriginalFilename(), e);
            throw new RuntimeException("Error processing file: " + file.getOriginalFilename(), e);
        }
    }

    @Override
    public void extractAndTransformAndLoad(byte[] fileContent, String fileName) {
        try {
            Resource resource = new ByteArrayResource(fileContent);
            List<Document> readDocuments = readFile(fileName, resource);
            List<Document> transformedDocuments = documentTransformerService.transformTextDocuments(readDocuments);
            List<Document> keywordMetadataEnrichmentDocuments = documentTransformerService.keywordMetadataEnrichment(transformedDocuments);
            List<Document> summaryMetadataEnrichmentDocuments = documentTransformerService.summaryMetadataEnrichment(keywordMetadataEnrichmentDocuments);
            documentWriterService.writeDocuments(summaryMetadataEnrichmentDocuments);
        } catch (Exception e) {
            log.error("Error processing file: " + fileName, e);
            log.info("Uploading file to Azure Blob Storage: {}", fileName);
            azureBlobService.uploadFile(fileContent, fileName);
            throw new RuntimeException("Error processing file: " + fileName, e);
        }
    }

    private List<Document> readFile(String filename, Resource resource) {
        return switch (FilenameUtils.getExtension(filename)) {
            case "pdf" -> documentReaderService.readPdfDocuments(resource, filename);
            case "txt" -> documentReaderService.readTextDocuments(resource, filename);
            case null -> throw new IllegalArgumentException("Filename is null");
            default -> throw new IllegalArgumentException("Unsupported file type: " + filename);
        };
    }
}
