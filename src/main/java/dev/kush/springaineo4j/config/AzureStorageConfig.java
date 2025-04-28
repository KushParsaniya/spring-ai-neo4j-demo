package dev.kush.springaineo4j.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "azure.storage")
@Getter
@Setter
public class AzureStorageConfig {

    private String connectionString;
    private String containerName;
}
