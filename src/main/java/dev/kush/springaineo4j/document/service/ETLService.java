package dev.kush.springaineo4j.document.service;

import org.springframework.web.multipart.MultipartFile;

public interface ETLService {

    void extractAndTransformAndLoad(MultipartFile file);

    void extractAndTransformAndLoad(byte[] fileContent, String fileName);

    void extractAndTransformAndLoadJob(byte[] fileContent, String fileName);
}
