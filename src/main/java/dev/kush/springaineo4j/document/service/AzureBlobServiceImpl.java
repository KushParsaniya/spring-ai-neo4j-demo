package dev.kush.springaineo4j.document.service;

import com.azure.core.management.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.WritableResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;

@Service
@Slf4j
public class AzureBlobServiceImpl {
    private final Resource storageBlobResource;

    public AzureBlobServiceImpl(@Value("azure-blob://${spring.cloud.azure.storage.blob.container-name}/demo.txt") Resource storageBlobResource) {
        this.storageBlobResource = storageBlobResource;
    }

    public void uploadFile(byte[] fileContent, String fileName) {
        log.info("Uploading file to Azure Blob Storage: {}", fileName);
        try (OutputStream blobos = ((WritableResource) this.storageBlobResource).getOutputStream()) {
            blobos.write(fileContent);
        } catch (IOException e) {
            log.error("Error uploading file to Azure Blob Storage", e);
            throw new RuntimeException("Error uploading file to Azure Blob Storage", e);
        }
    }
}
