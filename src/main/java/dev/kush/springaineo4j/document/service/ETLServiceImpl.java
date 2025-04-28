package dev.kush.springaineo4j.document.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.ai.document.Document;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.print.Doc;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ETLServiceImpl implements ETLService {

    private final DocumentReaderService documentReaderService;
    private final DocumentTransformerService documentTransformerService;
    private final DocumentWriterService documentWriterService;
    private final AzureBlobServiceImpl azureBlobService;
    private final JobLauncher jobLauncher;
    private final Job extractAndTransformAndLoadJob;

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
        Resource resource;
        Path path = null;
        List<Document> documents = new ArrayList<>();
        try {
            resource = new ByteArrayResource(fileContent);
            path = Files.createTempFile("temp-",fileName);
            Files.write(path, fileContent);
            documents = readFile(fileName, resource);

            documents = documentTransformerService.transformTextDocuments(documents);
            documents = documentTransformerService.keywordMetadataEnrichment(documents);
            documents = documentTransformerService.summaryMetadataEnrichment(documents);
            documentWriterService.writeDocuments(documents);
        } catch (IOException e) {
            log.error("Error processing file: " + fileName, e);
            throw new RuntimeException("Error processing file: " + fileName, e);
        } catch (Exception e) {
            log.error("Error processing file: " + fileName, e);
            log.info("Uploading file to Azure Blob Storage: {}", fileName);
            azureBlobService.uploadFile(path, fileName);
            throw new RuntimeException("Error processing file: " + fileName, e);
        }
    }

    @Override
    public void extractAndTransformAndLoadJob(byte[] fileContent, String fileName) {
        Resource resource;
        Path path = null;
        List<Document> documents = new ArrayList<>();
        try {
            resource = new ByteArrayResource(fileContent);
            path = Files.createTempFile("temp-",fileName);
            Files.write(path, fileContent);
            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("fileName", fileName)
                    .addString("filePath", path.toAbsolutePath().toString())
                    .toJobParameters();
            jobLauncher.run(extractAndTransformAndLoadJob, jobParameters);
        } catch (IOException e) {
            log.error("Error processing file: " + fileName, e);
            throw new RuntimeException("Error processing file: " + fileName, e);
        } catch (Exception e) {
            log.error("Error processing file: " + fileName, e);
            log.info("Uploading file to Azure Blob Storage: {}", fileName);
            azureBlobService.uploadFile(path, fileName);
            throw new RuntimeException("Error processing file: " + fileName, e);
        }
    }

    private List<Document> readFile(String filename, Resource resource) {
        return switch (FilenameUtils.getExtension(filename)) {
            case "pdf" -> documentReaderService.readPdfDocuments(resource, filename);
            case "txt" -> documentReaderService.readTextDocuments(resource, filename);
//            case null -> throw new IllegalArgumentException("Filename is null");
            default -> throw new IllegalArgumentException("Unsupported file type: " + filename);
        };
    }
}
