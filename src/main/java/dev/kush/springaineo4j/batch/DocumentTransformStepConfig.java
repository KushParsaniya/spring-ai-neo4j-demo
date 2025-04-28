package dev.kush.springaineo4j.batch;

import dev.kush.springaineo4j.constant.ProjectConstant;
import dev.kush.springaineo4j.document.service.DocumentTransformerService;
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
public class DocumentTransformStepConfig {

    private final DocumentTransformerService documentTransformerService;

    @Bean
    Step documentTransformStep(JobRepository jobRepository,
                               PlatformTransactionManager platformTransactionManager,
                               @Qualifier("documentTransformTasklet") Tasklet documentTransformTasklet
    ) {
        return new StepBuilder("documentTransformStep", jobRepository)
                .tasklet(documentTransformTasklet, platformTransactionManager)
                .build();
    }

    @Bean
    @StepScope
    Tasklet documentTransformTasklet() {
        return (contribution, chunkContext) -> {
            try {
                log.info("documentTransformTasklet :: execute :: start");
                @SuppressWarnings("unchecked")
                List<Document> documents = (List<Document>) BatchUtils.getContext(chunkContext, ProjectConstant.READ_DOCUMENTS);
                if (documents == null || documents.isEmpty()) {
                    log.warn("No documents to transform");
                    return RepeatStatus.FINISHED;
                }
                List<Document> transformedDocuments = documentTransformerService.transformTextDocuments(documents);
                if (transformedDocuments == null || transformedDocuments.isEmpty()) {
                    log.warn("documentTransformTasklet :: execute :: No documents found");
                    return RepeatStatus.FINISHED;
                }
                BatchUtils.putContext(chunkContext, ProjectConstant.TRANSFORM_DOCUMENTS, transformedDocuments);
                log.info("documentTransformTasklet :: execute :: end");
                return RepeatStatus.FINISHED;
            } catch (Exception e) {
                log.error("documentTransformTasklet :: execute :: error", e);
                return RepeatStatus.CONTINUABLE;
            }
        };
    }
}
