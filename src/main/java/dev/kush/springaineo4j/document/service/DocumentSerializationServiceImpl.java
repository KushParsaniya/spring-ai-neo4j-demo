package dev.kush.springaineo4j.document.service;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentSerializationServiceImpl implements DocumentSerializationService {

    private final ObjectMapper objectMapper;

    @Override
    public String serializeDocuments(List<Document> documents) {
        try {
            return objectMapper.writeValueAsString(documents);
        } catch (Exception e) {
            log.error("Error serializing documents", e);
            return "";
        }
    }

    @Override
    public List<Document> deserializeDocuments(String serializedDocuments) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            SimpleModule module = new SimpleModule();
            module.addDeserializer(Document.class, new DocumentDeserializer());
            objectMapper.registerModule(module);
            return objectMapper.readValue(serializedDocuments, objectMapper.getTypeFactory().constructCollectionType(List.class, Document.class));
        } catch (Exception e) {
            log.error("Error deserializing documents", e);
            return List.of();
        }
    }

    @Override
    public String serializeDocument(Document document) {
        try {
            return objectMapper.writeValueAsString(document);
        } catch (Exception e) {
            log.error("Error serializing document", e);
            return "";
        }
    }

    @Override
    public Document deserializeDocument(String serializedDocument) {
        try {
            return objectMapper.readValue(serializedDocument, Document.class);
        } catch (Exception e) {
            log.error("Error deserializing document", e);
            return null;
        }
    }
}

class DocumentDeserializer extends JsonDeserializer<Document> {
    @Override
    public Document deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException, JsonProcessingException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        String id = node.get("id").asText();
        String text = node.get("text").asText();
        Map<String, Object> metadata = new HashMap<>();
        node.get("metadata").fields().forEachRemaining(entry -> metadata.put(entry.getKey(), entry.getValue().asText()));

        return new Document.Builder()
                .id(id)
                .text(text)
                .metadata(metadata)
                .build();
    }
}
