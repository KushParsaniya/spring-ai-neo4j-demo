package dev.kush.springaineo4j.document.service;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobHttpHeaders;
import dev.kush.springaineo4j.config.AzureStorageConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.file.Path;

@Service
@Slf4j
@RequiredArgsConstructor
public class AzureBlobServiceImpl implements AzureBlobService {

    private final AzureStorageConfig azureStorageConfig;

    private BlobContainerClient getBlobContainerClient() {
        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                .connectionString(azureStorageConfig.getConnectionString())
                .buildClient();
        return blobServiceClient.getBlobContainerClient(azureStorageConfig.getContainerName());
    }

    private void ensureContainerExists(BlobContainerClient containerClient) {
        if (!containerClient.exists()) {
            containerClient.create();
            containerClient.setAccessPolicy(null, null);
        }
    }

    private BlobHttpHeaders determineContentType(String filePath) {
        BlobHttpHeaders headers = new BlobHttpHeaders();
        String fileExtension = filePath.substring(filePath.lastIndexOf('.')).toLowerCase();
        switch (fileExtension.toLowerCase()) {
            case ".pdf" -> headers.setContentType("application/pdf");
            case ".txt" -> headers.setContentType("text/plain");
            default -> headers.setContentType("application/octet-stream");
        }
        return headers;
    }

    @Override
    public void uploadFile(Path path, String fileName) {
        try {
            BlobContainerClient containerClient = getBlobContainerClient();
            ensureContainerExists(containerClient);
            // TODO: User userId or userName to create a unique blob name
            String blobName = "kush/" + fileName;
            BlobHttpHeaders headers = determineContentType(fileName);
            BlobClient blobClient = containerClient.getBlobClient(blobName);
            blobClient.setHttpHeaders(headers);
            blobClient.uploadFromFile(path.toAbsolutePath().toString(), true);
            log.info("File uploaded to Azure Blob Storage: {}", blobName);
        } catch (Exception e) {
            log.error("Error uploading file to Azure Blob Storage: {}", e.getMessage());
            throw new RuntimeException("Failed to upload file to Azure Blob Storage", e);
        }
    }
}
