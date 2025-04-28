package dev.kush.springaineo4j.config;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class OllamaConfig {

    @Bean
    @Primary
    EmbeddingModel embeddingModel() {
        return OllamaEmbeddingModel
                .builder()
                .ollamaApi(new OllamaApi("http://localhost:11434"))
                .defaultOptions(OllamaOptions
                        .builder()
                        .model("nomic-embed-text")
                        .build())
                .build();
    }

    @Bean
    ExecutorService executorService() {
        return Executors.newFixedThreadPool(2);
    }
}
