package dev.kush.springaineo4j.batch;

import dev.kush.springaineo4j.constant.ProjectConstant;
import dev.kush.springaineo4j.document.service.DocumentReaderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.ai.document.Document;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ReadFileStepConfig {

    private final DocumentReaderService documentReaderService;
    private final ResourceLoader resourceLoader;
    private final BatchUtils batchUtils;

    private List<Document> readFile(String filename, Resource resource) {
        return switch (FilenameUtils.getExtension(filename)) {
            case "pdf" -> documentReaderService.readPdfDocuments(resource, filename);
            case "txt" -> documentReaderService.readTextDocuments(resource, filename);
//            case null -> throw new IllegalArgumentException("Filename is null");
            default -> throw new IllegalArgumentException("Unsupported file type: " + filename);
        };
    }

    @Bean
    Step readFileStep(JobRepository jobRepository,
                      PlatformTransactionManager platformTransactionManager,
                      @Qualifier("readFileTasklet") Tasklet readFileTasklet
                      ) {
        return new StepBuilder("readFileStep", jobRepository)
                .tasklet(readFileTasklet, platformTransactionManager)
                .build();
    }

    @Bean
    @StepScope
    Tasklet readFileTasklet(@Value("#{jobParameters['filePath']}") String filePath,
                            @Value("#{jobParameters['fileName']}") String fileName) {
        return (contribution, chunkContext) -> {
            try {
                log.info("readFileTasklet :: execute :: start");
                // TODO: Replace with actual resource
                Resource resource = resourceLoader.getResource("file:" + filePath);

                if (fileName == null) {
                    log.error("readFileTasklet :: execute :: Could not determine filename from path: {}", filePath);
                    throw new IllegalArgumentException("Invalid file path provided: " + filePath);
                }

                List<Document> documents = readFile(fileName, resource);
                if (documents == null || documents.isEmpty()) {
                    log.warn("readFileTasklet :: execute :: No documents found");
                    return RepeatStatus.FINISHED;
                }
                batchUtils.putContext(chunkContext, ProjectConstant.READ_DOCUMENTS, documents);
                log.info("readFileTasklet :: execute :: end");
                return RepeatStatus.FINISHED;
            } catch (Exception e) {
                log.error("readFileTasklet :: execute :: error: {}", e.getMessage());
                return RepeatStatus.FINISHED;
            }
        };
    }

}
