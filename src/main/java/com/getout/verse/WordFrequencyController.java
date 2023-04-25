package com.getout.verse;

import com.getout.service.WordFrequencyBatch;
import com.getout.service.KeywordFrequencyService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/news")
public class WordFrequencyController {

    @CrossOrigin(origins = "http://localhost:3000")


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
    @GetMapping("/word-frequency")
    public ResponseEntity<Map<LocalDate, Integer>> getWordFrequency(@RequestParam String indexName,
                                                                     @RequestParam String keyword,
                                                                     @RequestParam int batchSize){
        try {
            Map<LocalDate, Integer> wordFrequency = WordFrequencyBatch.searchKeywordFrequency(indexName, keyword, batchSize);
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
