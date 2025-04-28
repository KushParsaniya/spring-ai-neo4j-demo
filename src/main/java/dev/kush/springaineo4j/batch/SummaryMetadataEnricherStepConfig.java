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
public class SummaryMetadataEnricherStepConfig {
    
    private final DocumentTransformerService documentTransformerService;
    
    @Bean
    Step summaryMetadataEnricherStep(JobRepository jobRepository,
                                     PlatformTransactionManager platformTransactionManager,
                                     @Qualifier("summaryMetadataEnricherTasklet") Tasklet summaryMetadataEnricherTasklet
    ) {
        return new StepBuilder("summaryMetadataEnricherStep", jobRepository)
                .tasklet(summaryMetadataEnricherTasklet, platformTransactionManager)
                .build();
    }

    @Bean
    @StepScope
    Tasklet summaryMetadataEnricherTasklet() {
        return (contribution, chunkContext) -> {
            try {
                log.info("summaryMetadataEnricherStepConfig :: summaryMetadataEnricherTasklet :: Start");
                @SuppressWarnings("unchecked")
                List<Document> documents = (List<Document>) BatchUtils.getContext(chunkContext, ProjectConstant.TRANSFORM_DOCUMENTS);
                if (documents == null || documents.isEmpty()) {
                    log.warn("summaryMetadataEnricherStepConfig ::execute :: No documents to enrich");
                    return RepeatStatus.FINISHED;
                }
                List<Document> enrichedDocuments = documentTransformerService.summaryMetadataEnrichment(documents);
                if (enrichedDocuments == null || enrichedDocuments.isEmpty()) {
                    log.warn("summaryMetadataEnricherStepConfig :: execute :: No documents found");
                    return RepeatStatus.FINISHED;
                }
                BatchUtils.putContext(chunkContext, ProjectConstant.SUMMARY_ENRICHMENT_DOCUMENTS, enrichedDocuments);
                log.info("summaryMetadataEnricherStepConfig :: summaryMetadataEnricherTasklet :: End");
                return RepeatStatus.FINISHED;
            } catch (Exception e) {
                log.error("summaryMetadataEnricherStepConfig :: summaryMetadataEnricherTasklet :: error", e);
                return RepeatStatus.CONTINUABLE;
            }
        };
    }
}
