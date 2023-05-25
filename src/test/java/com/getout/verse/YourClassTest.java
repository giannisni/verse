//package com.getout.verse;
//
//import com.getout.service.WordFrequencyBatch;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import java.time.LocalDate;
//import java.time.format.DateTimeFormatter;
//import java.util.Arrays;
//import java.util.Map;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.Future;
//import java.util.concurrent.TimeUnit;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.Mockito.*;
//
//public class YourClassTest {
//
//    @Mock
//    private WordFrequencyBatch wordFrequencyBatch;
//
//    @InjectMocks
//    private YourClass yourClass;
//
//    @BeforeEach
//    public void init() {
//        MockitoAnnotations.initMocks(this);
//    }
//
//    @Test
//    public void scheduleKeywordCountTaskTest() throws Exception {
//        // Arrange
//        String startDate = "2023-04-25";
//        String endDate = "2023-04-27";
//        int numberOfThreads = 4;
//        String keyword = "Μητσοτάκης";
//        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
//        LocalDate now = LocalDate.now();
//        String formattedEndDate = now.format(formatter);
//        String formattedStartDate = now.minusDays(2).format(formatter);
//
//        // Mock the behavior of the wordFrequencyBatch.searchKeywordFrequency method
//        when(wordFrequencyBatch.searchKeywordFrequency(anyString(), anyString(), anyInt(), anyString(), anyString()))
//                .thenReturn(Map.of(LocalDate.now(), 100));
//
//        // Act
//        yourClass.scheduleKeywordCountTask();
//
//        // Wait for all tasks to complete
//        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
//        executorService.awaitTermination(10, TimeUnit.SECONDS);
//        executorService.shutdown();
//
//        // Assert
//        verify(wordFrequencyBatch, times(keywords.size())).searchKeywordFrequency("daily_keyword_counts", keyword, 500, formattedStartDate, formattedEndDate);
//        assertEquals(numberOfThreads, executorService.getPoolSize());
//        assertEquals(0, executorService.getActiveCount());
//        assertEquals(0, executorService.getQueue().size());
//    }
//}
