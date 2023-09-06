package com.getout.verse;

import com.getout.service.TweetMetricsService;
import com.getout.service.WordFrequencyBatch;
import com.getout.service.KeywordFrequencyService;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/news")
@CrossOrigin(origins = "http://localhost:3000")
public class WordFrequencyController {

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


    @GetMapping("/correlation")
    public ResponseEntity<Double> getCorrelation(
            @RequestParam String keyword1,
            @RequestParam String keyword2,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) throws IOException {
        // Fetch keyword counts from your service
        Map<LocalDate, Integer> keywordCounts1 = KeywordFrequencyService.getKeywordCounts(keyword1, startDate, endDate);
        Map<LocalDate, Integer> keywordCounts2 = KeywordFrequencyService.getKeywordCounts(keyword2, startDate, endDate);
        // Calculate correlation
        double correlation = KeywordFrequencyService.calculateCorrelation(keywordCounts1, keywordCounts2);
        return ResponseEntity.ok(correlation);
    }

    @GetMapping("/counts")
    public ResponseEntity<Map<LocalDate, Integer>> getKeywordCounts(
            @RequestParam String keyword,
            @RequestParam String startDate,
            @RequestParam String endDate) {
        try {
            Map<LocalDate, Integer> keywordCounts = KeywordFrequencyService.getKeywordCounts(keyword, LocalDate.parse(startDate), LocalDate.parse(endDate));
            return ResponseEntity.ok(keywordCounts);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/forecast")
    public ResponseEntity<Double> getForecast(
            @RequestParam String keyword1,
            @RequestParam String keyword2,
            @RequestParam String startDate,
            @RequestParam String endDate) {
        try {
            double forecast = KeywordFrequencyService.predictKeywordCount(keyword1, keyword2, LocalDate.parse(startDate), LocalDate.parse(endDate));
            return ResponseEntity.ok(forecast);
        } catch (IOException | InterruptedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @GetMapping("/word-frequency")
    public ResponseEntity<Map<LocalDate, Integer>> getWordFrequency(@RequestParam String indexName,
                                                                     @RequestParam String keyword,@RequestParam String startDate,@RequestParam String endDate,
                                                                     @RequestParam int batchSize){
        try {
            Map<LocalDate, Integer> wordFrequency = WordFrequencyBatch.searchKeywordFrequency(indexName, keyword, batchSize,startDate,endDate);
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
    public ResponseEntity<Map<LocalDate, Integer>> getTopicFrequency(@RequestParam String indexName,@RequestParam String topic,
                                                                     @RequestParam List<String> keywords, @RequestParam String startDate, @RequestParam String endDate,
                                                                     @RequestParam int batchSize){
        try {
            Map<LocalDate, Integer> wordFrequency = WordFrequencyBatch.searchTopicFrequency(indexName,topic, keywords, batchSize,startDate,endDate);
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
