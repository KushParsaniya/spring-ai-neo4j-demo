package dev.kush.springaineo4j;

import dev.kush.springaineo4j.config.AzureStorageConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({AzureStorageConfig.class})
public class SpringAiNeo4jApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringAiNeo4jApplication.class, args);
    }
}