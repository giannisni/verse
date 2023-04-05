package com.getout.verse;

import com.getout.service.WordFrequencyBatch;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/news")
public class WordFrequencyController {

    @GetMapping("/word-frequency")
    public ResponseEntity<Map<LocalDate, Integer>> getWordFrequency(@RequestParam String indexName,
                                                                     @RequestParam String keyword,
                                                                     @RequestParam int batchSize) {
        try {
            Map<LocalDate, Integer> wordFrequency = WordFrequencyBatch.searchKeywordFrequency(indexName, keyword, batchSize);
            return ResponseEntity.ok(wordFrequency);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
