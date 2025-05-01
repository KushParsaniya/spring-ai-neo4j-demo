package dev.kush.springaineo4j.batch;

import dev.kush.springaineo4j.document.service.DocumentSerializationService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BatchUtils {

    private final DocumentSerializationService documentSerializationService;

    public void putContext(ChunkContext chunkContext, String key, List<Document> documents) {
        chunkContext.getStepContext()
                .getStepExecution()
                .getJobExecution()
                .getExecutionContext()
                .putString(key, documentSerializationService.serializeDocuments(documents));
    }

    public List<Document> getContext(ChunkContext chunkContext, String key) {
        String serializedDocuments = chunkContext.getStepContext()
                .getStepExecution()
                .getJobExecution()
                .getExecutionContext()
                .getString(key);
        return documentSerializationService.deserializeDocuments(serializedDocuments);
    }
}
