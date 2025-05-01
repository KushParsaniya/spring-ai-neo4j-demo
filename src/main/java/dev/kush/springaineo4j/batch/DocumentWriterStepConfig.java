package dev.kush.springaineo4j.batch;

import dev.kush.springaineo4j.constant.ProjectConstant;
import dev.kush.springaineo4j.document.service.DocumentWriterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DocumentWriterStepConfig {

    private final DocumentWriterService documentWriterService;
    private final BatchUtils batchUtils;

    @Bean
    Step documentWriteStep(JobRepository jobRepository,
                           PlatformTransactionManager platformTransactionManager,
                           @Qualifier("documentWriteTasklet") Tasklet documentWriteTasklet
    ) {
        return new StepBuilder("documentWriteStep", jobRepository)
                .tasklet(documentWriteTasklet, platformTransactionManager)
                .build();
    }

    @Bean
    @StepScope
    Tasklet documentWriteTasklet() {
        return (contribution, chunkContext) -> {
            try {
                log.info("documentWriteStepConfig :: documentWriteTasklet :: Start");
                List<Document> documents = batchUtils.getContext(chunkContext, ProjectConstant.TRANSFORM_DOCUMENTS);
                if (documents == null || documents.isEmpty()) {
                    log.warn("documentWriteStepConfig ::execute :: No documents to enrich");
                    return RepeatStatus.FINISHED;
                }
                documentWriterService.writeDocuments(documents);
                log.info("documentWriteStepConfig :: documentWriteTasklet :: End");
                return RepeatStatus.FINISHED;
            } catch (Exception e) {
                log.error("documentWriteStepConfig :: documentWriteTasklet :: error", e);
                return RepeatStatus.FINISHED;
            }
        };
    }
}
