package com.getout.service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Logger;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.http.HttpHost;
import org.elasticsearch.action.search.*;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.core.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.Scroll;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Service;

import static com.getout.service.indexMap.indexSortedMap;

@Service
public class WordFrequencyBatch {

    private static final Map<Character, String> greekToGreeklishMap = new HashMap<Character, String>() {{
        put('α', "a"); put('Α', "A");
        put('β', "v"); put('Β', "V");
        put('γ', "g"); put('Γ', "G");
        put('δ', "d"); put('Δ', "D");
        put('ε', "e"); put('Ε', "E");
        put('ζ', "z"); put('Ζ', "Z");
        put('η', "i"); put('Η', "H");
        put('θ', "th"); put('Θ', "Th");
        put('ι', "i"); put('Ι', "I");
        put('κ', "k"); put('Κ', "K");
        put('λ', "l"); put('Λ', "L");
        put('μ', "m"); put('Μ', "M");
        put('ν', "n"); put('Ν', "N");
        put('ξ', "x"); put('Ξ', "X");
        put('ο', "o"); put('Ο', "O");
        put('π', "p"); put('Π', "P");
        put('ρ', "r"); put('Ρ', "R");
        put('σ', "s"); put('ς', "s"); put('Σ', "S");
        put('τ', "t"); put('Τ', "T");
        put('υ', "y"); put('Υ', "Y");
        put('φ', "f"); put('Φ', "F");
        put('χ', "x"); put('Χ', "X");
        put('ψ', "ps"); put('Ψ', "Ps");
        put('ω', "o"); put('Ω', "O");
    }};
    private static final Logger logger = Logger.getLogger(WordFrequencyBatch.class.getName());

    public static Map<LocalDate, Integer> searchKeywordFrequency(String indexName, String keyword, int batchSize, String startDate, String endDate) throws IOException, InterruptedException, ExecutionException {
        // Start timer
        //long startTime = System.currentTimeMillis();
        long startTime3 = System.currentTimeMillis();
        AtomicInteger processedHits = new AtomicInteger(0);
        System.out.println("Keyword: " + keyword);
        long startTime1 = System.currentTimeMillis();

        // Set up Elasticsearch client and query for keyword frequency data
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost", 9200, "http")));

        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .appendPattern("yyyy-MM-dd'T'HH:mm:ss")
                .optionalStart()
                .appendFraction(java.time.temporal.ChronoField.NANO_OF_SECOND, 1, 9, true)
                .optionalEnd()
                .optionalStart()
                .appendLiteral('Z')
                .optionalEnd()
                .toFormatter();
        Map<LocalDate, Integer> dateFrequencyMap = new ConcurrentHashMap<>();

        // Define search query
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.boolQuery()
                .must(QueryBuilders.matchQuery("content", keyword))
                .filter(QueryBuilders.existsQuery("published_date"))
                .filter(QueryBuilders.rangeQuery("published_date").gte(startDate).lte(endDate)));


        searchSourceBuilder.size(batchSize);

        // Initialize scroll
        final Scroll scroll = new Scroll(TimeValue.timeValueMinutes(1L));
        SearchRequest searchRequest = new SearchRequest(indexName);
        searchRequest.scroll(scroll);
        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        String scrollId = searchResponse.getScrollId();


        SearchHits hits = searchResponse.getHits();
        final long totalHits = hits.getTotalHits().value;
        SearchHit[] searchHits = hits.getHits();


        // Initialize ExecutorService
        int numberOfThreads = 4; // Adjust this value according to your system's capabilities
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

        long elapsedTimeMillis1 = System.currentTimeMillis() - startTime1;
        double elapsedTimeSec1 = elapsedTimeMillis1 / 1000.0;
        System.out.printf("Request Elapsed time: %.3f seconds%n", elapsedTimeSec1);
        // Process search query results using scroll

        long startTime = System.currentTimeMillis();

        while (searchHits != null && searchHits.length > 0) {
            // Prepare a list of futures
            List<Future<Void>> futures = new ArrayList<>();

            // Iterate through Elasticsearch query results and submit tasks for concurrent processing



            for (SearchHit hit : searchHits) {
                Callable<Void> task = () -> {

                    //logger.info("Processing hit with ID: " + hit.getId());
                    int currentProcessedHits = processedHits.incrementAndGet();
                    logger.info(" Total processed hits: " + currentProcessedHits + " / " + totalHits);

                    Object publishedDateObj = hit.getSourceAsMap().get("published_date");
                    LocalDate date = null;

                    if (publishedDateObj instanceof String) {
                        date = LocalDate.parse(publishedDateObj.toString(), formatter);
                    } else if (publishedDateObj instanceof List) {
                        List<String> publishedDateList = (List<String>) publishedDateObj;
                        if (!publishedDateList.isEmpty()) {
                            date = LocalDate.parse(publishedDateList.get(0), formatter);
                        }
                    }

                    // Count frequency of keyword in content field
                    String content = hit.getSourceAsMap().get("content").toString();
                    int frequency = (content.split(keyword, -1).length) - 1;

                    // Check if date already exists in map; if not, create new entry
                    if (!dateFrequencyMap.containsKey(date)) {
                        dateFrequencyMap.put(date, 0);
                    }

                    // Add frequency to existing total for this date
                    int existingTotal = dateFrequencyMap.get(date);
                    dateFrequencyMap.put(date, existingTotal + frequency);

                    //logger.info("Finished processing hit with ID: " + hit.getId());

                    return null;
                };

                futures.add(executorService.submit(task));
            }


            // Wait for all tasks to complete
            for (Future<Void> future : futures) {
                future.get();
            }
            // Prepare next scroll iteration
            SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId);
            scrollRequest.scroll(scroll);
            searchResponse = client.scroll(scrollRequest, RequestOptions.DEFAULT);
            scrollId = searchResponse.getScrollId();
            searchHits = searchResponse.getHits().getHits();
        }
        long elapsedTimeMillis = System.currentTimeMillis() - startTime;
        double elapsedTimeSec = elapsedTimeMillis / 1000.0;
        System.out.printf("Looping ResultsElapsed time: %.3f seconds%n", elapsedTimeSec);
        // Shutdown the ExecutorService
        executorService.shutdown();

        // Clear scroll
        ClearScrollRequest clearScrollRequest = new ClearScrollRequest();
        clearScrollRequest.addScrollId(scrollId);
        ClearScrollResponse clearScrollResponse = client.clearScroll(clearScrollRequest, RequestOptions.DEFAULT);

        // Clean up Elasticsearch client resources
        client.close();

        // Sort the dateFrequencyMap by date
        Map<LocalDate, Integer> sortedMap = new TreeMap<>(Comparator.naturalOrder());
        sortedMap.putAll(dateFrequencyMap);

        // Index the sorted map
        long startTime2 = System.currentTimeMillis();

        indexSortedMap("daily_keyword_counts", sortedMap,keyword);

        long elapsedTimeMillis2 = System.currentTimeMillis() - startTime2;
        double elapsedTimeSec2 = elapsedTimeMillis2 / 1000.0;
        System.out.printf("Sorting Elapsed time: %.3f seconds%n", elapsedTimeSec2);
        // Calculate elapsed time in seconds and print it

        long elapsedTimeMillis3 = System.currentTimeMillis() - startTime3;
        double elapsedTimeSec3 = elapsedTimeMillis3 / 1000.0;
        System.out.printf("Total Elapsed time: %.3f seconds%n", elapsedTimeSec3);
        // Return the sorted map

        System.out.println(keyword+"sorted");
        return sortedMap;
    }



}



