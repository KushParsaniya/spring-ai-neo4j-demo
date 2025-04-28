package dev.kush.springaineo4j.batch;

import org.springframework.batch.core.scope.context.ChunkContext;

public class BatchUtils {

    public static void putContext(ChunkContext chunkContext, String key, Object value) {
        chunkContext.getStepContext().getStepExecution().getExecutionContext().put(key, value);
    }

    public static Object getContext(ChunkContext chunkContext, String key) {
        return chunkContext.getStepContext().getStepExecution().getExecutionContext().get(key);
    }
}
