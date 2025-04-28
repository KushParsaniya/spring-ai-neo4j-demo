package dev.kush.springaineo4j.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JobConfig {

    @Bean
    Job extractAndTransformAndLoadJob(JobRepository jobRepository,
                    @Qualifier("readFileStep") Step readFileStep,
                    @Qualifier("documentTransformStep") Step documentTransformStep,
                    @Qualifier("keywordMetadataEnricherStep") Step keywordMetadataEnricherStep,
                    @Qualifier("summaryMetadataEnricherStep") Step summaryMetadataEnricherStep,
                    @Qualifier("documentWriteStep") Step documentWriteStep
                    ) {
        return new JobBuilder("processFileJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(readFileStep)
                .next(documentTransformStep)
                .next(keywordMetadataEnricherStep)
                .next(summaryMetadataEnricherStep)
                .next(documentWriteStep)
                .build();
    }
}
