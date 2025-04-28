package dev.kush.springaineo4j.document.service;

import java.nio.file.Path;

public interface AzureBlobService {
    void uploadFile(Path path, String fileName);
}
