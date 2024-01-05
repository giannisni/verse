package com.getout.verse;

import com.getout.service.TweetMetricsService;
import com.getout.service.WordFrequencyBatch;
import com.getout.service.KeywordFrequencyService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static com.getout.service.KeywordFrequencyService.fetchKeywordFrequencies;
import static com.getout.service.KeywordFrequencyService.fetchWordFrequenciesFromTopicNew;

@RestController
@RequestMapping("/api/news")
@CrossOrigin(origins = "http://localhost:5173")
public class WordFrequencyController {
    @Autowired
    private KeywordFrequencyService keywordFrequencyService; // Inject the service

    private final TweetMetricsService tweetMetricsService;

    public WordFrequencyController(TweetMetricsService tweetMetricsService) {
        this.tweetMetricsService = tweetMetricsService;
    }

    @CrossOrigin(origins = "http://localhost:3000")



    @PostMapping("/tweet-metrics")
    public ResponseEntity<Void> calculateTweetMetrics(@RequestParam String startDate, @RequestParam String endDate) throws IOException {
        tweetMetricsService.calculateTweetMetrics(startDate, endDate);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/openai-data")
    public ResponseEntity<?> getOpenAIData(@RequestParam String indexName) {
        try {
            List<KeywordFrequencyService.OpenAIData> openAIDataList = keywordFrequencyService.fetchOpenAIData(indexName); // Use the injected service
            return ResponseEntity.ok(openAIDataList);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("error", e.getMessage()));
        }
    }


    @GetMapping("/word-frequencies")
    public Map<String, Integer> getWordFrequenciesFromTopicNew(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        try {
            LocalDate startLocalDate = LocalDate.parse(startDate);
            LocalDate endLocalDate = LocalDate.parse(endDate);
            return fetchWordFrequenciesFromTopicNew(startLocalDate, endLocalDate);
        } catch (Exception e) {
            // Log the exception for debugging purposes
            e.printStackTrace();
            // You might want to handle this differently, perhaps returning an empty map or a default value
            return new HashMap<>();
        }
    }

    @GetMapping("/wordcloud")
    public List<Map<String, Object>> getWordCloudData(@RequestParam String indexName) {
        try {
            return KeywordFrequencyService.getWordCloudData(indexName);
        } catch (IOException e) {
            throw new RuntimeException("Failed to fetch word cloud data", e);
        }
    }


    @GetMapping("/keyword-frequencies")
    public Map<String, Integer> getKeywordFrequencies(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        try {
            LocalDate startLocalDate = LocalDate.parse(startDate);
            LocalDate endLocalDate = LocalDate.parse(endDate);
            return fetchKeywordFrequencies(startLocalDate, endLocalDate);
        } catch (Exception e) {
            // Log the exception for debugging purposes
            e.printStackTrace();
            // You might want to handle this differently, perhaps returning an empty map or a default value
            return new HashMap<>();
        }
    }








//    @GetMapping("/correlation")
//    public ResponseEntity<Double> getCorrelation(
//            @RequestParam String keyword1,
//            @RequestParam String keyword2,
//            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
//            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) throws IOException {
//        // Fetch keyword counts from your service
//        Map<LocalDate, Integer> keywordCounts1 = KeywordFrequencyService.getKeywordCounts(keyword1, startDate, endDate);
//        Map<LocalDate, Integer> keywordCounts2 = KeywordFrequencyService.getKeywordCounts(keyword2, startDate, endDate);
//        // Calculate correlation
//        double correlation = KeywordFrequencyService.calculateCorrelation(keywordCounts1, keywordCounts2);
//        return ResponseEntity.ok(correlation);
//    }

    @GetMapping("/counts")
    public ResponseEntity<Map<LocalDate, Integer>> getKeywordCounts(
            @RequestParam String keyword,
            @RequestParam String startDate,
            @RequestParam String endDate,@RequestParam String index ) {
        try {
            Map<LocalDate, Integer> keywordCounts = KeywordFrequencyService.getKeywordCounts(index,keyword, LocalDate.parse(startDate), LocalDate.parse(endDate));
            return ResponseEntity.ok(keywordCounts);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/getdocs")
    public ResponseEntity<List<KeywordFrequencyService.DocumentData>> fetchDocuments(
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam List<String> keywords,
            @RequestParam String index) {
        try {
            List<KeywordFrequencyService.DocumentData> documents = KeywordFrequencyService.fetchDocumentsWithWords(LocalDate.parse(startDate), LocalDate.parse(endDate), keywords, index);
            return ResponseEntity.ok(documents);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/by-topic")
    public ResponseEntity<List<KeywordFrequencyService.DocumentData>> getDocumentsByTopic(
            @RequestParam int topicId,
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam String index) {
        try {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            List<KeywordFrequencyService.DocumentData> documents = KeywordFrequencyService.fetchDocumentsByTopic(start, end, topicId, index);
            return ResponseEntity.ok(documents);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

//    @GetMapping("/forecast")
//    public ResponseEntity<Double> getForecast(
//            @RequestParam String keyword1,
//            @RequestParam String keyword2,
//            @RequestParam String startDate,
//            @RequestParam String endDate) {
//        try {
//            double forecast = KeywordFrequencyService.predictKeywordCount(keyword1, keyword2, LocalDate.parse(startDate), LocalDate.parse(endDate));
//            return ResponseEntity.ok(forecast);
//        } catch (IOException | InterruptedException e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//    }
    @GetMapping("/word-frequency")
    public ResponseEntity<Map<LocalDate, Integer>> getWordFrequency(@RequestParam String indexName,
                                                                     @RequestParam String keyword,@RequestParam String toindex,@RequestParam String startDate,@RequestParam String endDate,
                                                                     @RequestParam int batchSize){
        try {
            Map<LocalDate, Integer> wordFrequency = WordFrequencyBatch.searchKeywordFrequency(indexName,toindex, keyword, batchSize,startDate,endDate);
            return ResponseEntity.ok(wordFrequency);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/topic-frequency")
    public ResponseEntity<Map<LocalDate, Integer>> getTopicFrequency(@RequestParam String indexName,@RequestParam String toindex,@RequestParam String topic,
                                                                     @RequestParam List<String> keywords, @RequestParam String startDate, @RequestParam String endDate,
                                                                     @RequestParam int batchSize){
        try {
            Map<LocalDate, Integer> wordFrequency = WordFrequencyBatch.searchTopicFrequency(indexName,toindex,topic, keywords, batchSize,startDate,endDate);
            return ResponseEntity.ok(wordFrequency);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


}
