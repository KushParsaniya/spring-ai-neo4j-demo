package dev.kush.springaineo4j.document.controller;

import dev.kush.springaineo4j.document.service.ETLService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.VectorStoreChatMemoryAdvisor;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;

@RestController
@RequestMapping("/api/v1/documents")
public class DocumentController {
    private final ETLService etlService;
    private final ChatClient chatClient;
    private final VectorStore vectorStore;
    private final ExecutorService executorService;

    public record ChatDto(String chatId, String content) {
    }

    public DocumentController(ETLService etlService, ChatClient.Builder builder, VectorStore vectorStore, ExecutorService executorService) {
        this.etlService = etlService;
        this.executorService = executorService;
        this.chatClient = builder
                .defaultAdvisors(new SimpleLoggerAdvisor(), VectorStoreChatMemoryAdvisor.builder(vectorStore).build())
                .build();
        this.vectorStore = vectorStore;
    }

    @PostMapping("/process")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void processFile(@RequestParam("file") MultipartFile file) {
        try {
            byte[] fileContent = file.getBytes(); // Read file content into a byte array
            executorService.submit(() -> etlService.extractAndTransformAndLoad(fileContent, file.getOriginalFilename()));
        } catch (IOException e) {
            throw new RuntimeException("Failed to process file", e);
        }
    }

    @PostMapping("/query")
    @ResponseStatus(HttpStatus.OK)
    public ChatDto search(@RequestBody ChatDto chatDto) {
        String chatId = StringUtils.defaultIfBlank(chatDto.chatId(), UUID.randomUUID().toString());
        String response = chatClient.prompt()
                .advisors(new QuestionAnswerAdvisor(vectorStore))
                .advisors(advisorParam -> advisorParam.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId))
                .user(chatDto.content())
                .call()
                .content();
        return new ChatDto(chatId, response);
    }
}
