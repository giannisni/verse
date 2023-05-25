//package com.getout.verse;
//
//import com.getout.verse.WordFrequencyController;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//
//import java.time.LocalDate;
//import java.util.Map;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//
//
//public class WordFrequencyControllerTest {
//
//    private WordFrequencyController wordFrequencyController;
//
//    @BeforeEach
//    public void setUp() {
//        // You may need to modify this line to initialize the controller with its required dependencies.
//        wordFrequencyController = new WordFrequencyController();
//    }
//
//    @Test
//    public void testWordFrequency() {
//        String indexName = "norconex2";
//        String keyword = "Βαρουφάκης";
//        int batchSize = 500;
//
//        // Call the controller method
//        ResponseEntity<Map<LocalDate, Integer>> response = wordFrequencyController.getWordFrequency(indexName, keyword, "2021-01-01", "2021-12-31",batchSize);
//
//        // Assertions
//        assertEquals(HttpStatus.OK, response.getStatusCode(), "Response status should be OK");
//        assertNotNull(response.getBody(), "Response body should not be null");
//        assertTrue(response.getBody().size() > 0, "Response body should contain some data");
//    }
//}
