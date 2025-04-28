package dev.kush.springaineo4j.document.service;

import dev.kush.springaineo4j.exception.KeywordMetadataEnricherException;
import dev.kush.springaineo4j.exception.SummaryMetadataEnricherException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.transformer.KeywordMetadataEnricher;
import org.springframework.ai.chat.transformer.SummaryMetadataEnricher;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.ai.chat.transformer.SummaryMetadataEnricher.SummaryType.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class DocumentTransformerServiceImpl implements DocumentTransformerService {

    private final ChatModel chatModel;

    @Override
    public List<Document> transformTextDocuments(List<Document> documents) {
        log.info("DocumentTransformerServiceImpl :: transformTextDocuments :: start");
        List<Document> transformedDocuments = new TokenTextSplitter(200,50,5,1000,true).apply(documents);
        log.info("DocumentTransformerServiceImpl :: transformTextDocuments :: end");
        return transformedDocuments;
    }

    @Override
    public List<Document> keywordMetadataEnrichment(List<Document> documents) {
        try {
            log.info("DocumentTransformerServiceImpl :: keywordMetadataEnrichment :: start");
            KeywordMetadataEnricher enricher = new KeywordMetadataEnricher(chatModel, 5);
            List<Document> finalDocuments = new ArrayList<>();
            for (Document document : documents) {
                List<Document> enrichedDocuments = enricher.apply(List.of(document));
                Thread.sleep(5000);
                finalDocuments.addAll(enrichedDocuments);
            }
            log.info("DocumentTransformerServiceImpl :: keywordMetadataEnrichment :: end");
            return finalDocuments;
        } catch (Exception e) {
            log.error("DocumentTransformerServiceImpl :: keywordMetadataEnrichment :: error", e);
            throw new KeywordMetadataEnricherException("DocumentTransformerServiceImpl :: keywordMetadataEnrichment :: " + e.getMessage());
        }
    }

    @Override
    public List<Document> summaryMetadataEnrichment(List<Document> documents) {
        try {
            log.info("DocumentTransformerServiceImpl :: summaryMetadataEnrichment :: start");
            SummaryMetadataEnricher enricher = new SummaryMetadataEnricher(chatModel, List.of(NEXT, PREVIOUS, CURRENT));
            List<Document> finalDocuments = new ArrayList<>();
            for (Document document : documents) {
                List<Document> enrichedDocuments = enricher.apply(List.of(document));
//                Thread.sleep(10000);
                finalDocuments.addAll(enrichedDocuments);
            }
            log.info("DocumentTransformerServiceImpl :: summaryMetadataEnrichment :: end");
            return finalDocuments;
        } catch (Exception e) {
            log.error("DocumentTransformerServiceImpl :: summaryMetadataEnrichment :: error", e);
            throw new SummaryMetadataEnricherException("DocumentTransformerServiceImpl :: summaryMetadataEnrichment :: " +  e.getMessage());
        }
    }
}
