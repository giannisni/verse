package com.getout.component;

import com.getout.service.WordFrequencyBatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;

@Service
public class ScheduledTasks {
    private static final Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);

    private final WordFrequencyBatch wordFrequencyBatch;

    public ScheduledTasks(WordFrequencyBatch wordFrequencyBatch) {
        this.wordFrequencyBatch = wordFrequencyBatch;
    }

//    @Scheduled(cron = "0 05 18 * * *")
    public void  scheduleKeywordCountTask(String index,String toindex) {
        logger.info("Starting scheduled task for keyword count...");

        // Define the keywords you want to search for
        //List<String> keywords = Arrays.asList("Κωνσταντοπούλου","Μητσοτάκης", "Τσίπρας", "Βαρουφάκης", "Κουτσούμπας","Ανδρουλάκης","Kασιδιάρης","ΣΥΡΙΖΑ"," Hellenic Train","έγκλημα στα Τέμπη","τηλεδιοίκηση","Καραμανλής","φωτεινή σηματοδότηση","σιδηροδρομικό","ΤΡΑΙΝΟΣΕ","ΟΣΕ","τραγωδία στα Τέμπη","Σταθμάρχη","σύγκρουση των δύο τρένων","57 ανθρώπους","Ουκρανία","Νάτο","Πόλεμος στην Ουκρανία","Ρωσία","Πούτιν","Πλεύση Ελευθεριάς","Μέρα25","Δήμητρα","Δραχμή","Ευρώ","ευρώ");
        //List<String> keywords = Arrays.asList("Τέμπη");
        List<String> keywords = Arrays.asList("israeli","israel","palestine","palestinian","palestinians","hamas", "israelis");

        //List<String> keywords = Arrays.asList("Μητσοτάκης", "Τσίπρας", "Βαρουφάκης", "Κουτσούμπας","Ανδρουλάκης","Kασιδιάρης","ΣΥΡΙΖΑ");

        // Calculate the time frame for the search
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
        LocalDateTime now = LocalDateTime.now();
        String endDate = now.format(formatter);
        String startDate = now.minusDays(100).format(formatter);


        System.out.println("Start date: " + startDate);
        System.out.println("End date: " + endDate);

        // Initialize an ExecutorService with a fixed number of threads
        int numberOfThreads = 4; // Adjust this value according to your system's capabilities
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

        // Prepare a list of futures
        List<Future<Map<LocalDate, Integer>>> futures = new ArrayList<>();

        // Search for keyword counts within the specified time frame using multiple threads
        for (String keyword : keywords) {
            Callable<Map<LocalDate, Integer>> task = () -> {
                try {
                    return wordFrequencyBatch.searchKeywordFrequency(index,toindex, keyword, 500, startDate, endDate);
                } catch (Exception e) {
                    logger.error("Error processing keyword: " + keyword, e);
                    return Collections.emptyMap();
                }
            };
            futures.add(executorService.submit(task));
        }

        // Wait for all tasks to complete and print the results
        for (Future<Map<LocalDate, Integer>> future : futures) {
            try {
                Map<LocalDate, Integer> resultMap = future.get();
                logger.info("Result: " + resultMap);
            } catch (InterruptedException | ExecutionException e) {
                logger.error("Error retrieving keyword count result", e);
            }
        }

        // Shutdown the ExecutorService
        executorService.shutdown();

        logger.info("Finished scheduled task for keyword count.");
    }

    @Scheduled(cron = "0 53  21 * * *")
    public void scheduleTopicCountTask() {
        logger.info("Starting scheduled task for keyword count...");

        // Define the topics and their associated keywords
        Map<String, List<String>> topicKeywords = new HashMap<>();
        topicKeywords.put("Μεταναστευτικό", Arrays.asList("μετανάστης", "διακινητής"));
        topicKeywords.put("Οικονομία", Arrays.asList("ευρώ", "τράπεζα"));
        // add more topics and their associated keywords...

        // Calculate the time frame for the search
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
        LocalDateTime now = LocalDateTime.now();
        String endDate = now.format(formatter);
        String startDate = now.minusDays(30).format(formatter);

        System.out.println("Start date: " + startDate);
        System.out.println("End date: " + endDate);

        // Initialize an ExecutorService with a fixed number of threads
        int numberOfThreads = 4; // Adjust this value according to your system's capabilities
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

        // Prepare a list of futures
        List<Future<Map<LocalDate, Integer>>> futures = new ArrayList<>();

        // Search for keyword counts within the specified time frame using multiple threads
        for (Map.Entry<String, List<String>> entry : topicKeywords.entrySet()) {
            String topic = entry.getKey();
            List<String> keywords = entry.getValue();

            Callable<Map<LocalDate, Integer>> task = () -> {
                try {
                    return wordFrequencyBatch.searchTopicFrequency("norconex2","norconex2_counts", topic, keywords, 500, startDate, endDate);
                } catch (Exception e) {
                    logger.error("Error processing keywords for topic: " + topic, e);
                    return Collections.emptyMap();
                }
            };
            futures.add(executorService.submit(task));
        }

        // Wait for all tasks to complete and print the results
        for (Future<Map<LocalDate, Integer>> future : futures) {
            try {
                Map<LocalDate, Integer> resultMap = future.get();
                logger.info("Result: " + resultMap);
            } catch (InterruptedException | ExecutionException e) {
                logger.error("Error retrieving keyword count result", e);
            }
        }

        // Shutdown the ExecutorService
        executorService.shutdown();

        logger.info("Finished scheduled task for keyword count.");
    }


}
