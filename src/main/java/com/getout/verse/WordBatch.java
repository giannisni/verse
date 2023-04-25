//package com.getout.service;
//
//import java.io.IOException;
//import java.time.Duration;
//import java.time.Instant;
//import java.time.LocalDate;
//import java.time.format.DateTimeFormatter;
//import java.util.*;
//
//import org.apache.http.HttpHost;
//import org.elasticsearch.action.search.ClearScrollRequest;
//import org.elasticsearch.action.search.SearchRequest;
//import org.elasticsearch.action.search.SearchResponse;
//import org.elasticsearch.action.search.SearchScrollRequest;
//import org.elasticsearch.client.RequestOptions;
//import org.elasticsearch.client.RestClient;
//import org.elasticsearch.client.RestHighLevelClient;
//import org.elasticsearch.core.TimeValue;
//import org.elasticsearch.index.query.QueryBuilders;
//import org.elasticsearch.search.SearchHit;
//import org.elasticsearch.search.builder.SearchSourceBuilder;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Service;
//
//@Service
//public class WordFrequencyBatch {
//
//    private static final Logger LOGGER = LoggerFactory.getLogger(WordFrequencyBatch.class);
//
//    public static Map<LocalDate, Integer> searchKeywordFrequency(String indexName, String keyword, int batchSize) throws IOException {
//        RestHighLevelClient client = new RestHighLevelClient(
//                RestClient.builder(new HttpHost("localhost", 9200, "http")));
//
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
//        Map<LocalDate, Integer> dateFrequencyMap = new HashMap<>();
//
//        // Define search query
//        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
//        searchSourceBuilder.query(QueryBuilders.boolQuery()
//                .must(QueryBuilders.matchQuery("content", keyword))
//                .filter(QueryBuilders.existsQuery("published_date")));
//
//        searchSourceBuilder.size(batchSize);
//
//        // Execute search query in batches using scroll API
//        SearchRequest searchRequest = new SearchRequest(indexName);
//        searchRequest.scroll(TimeValue.timeValueMinutes(1L));
//        searchRequest.source(searchSourceBuilder);
//
//        // Get total hits count for the query
//        SearchResponse countResponse = client.search(searchRequest, RequestOptions.DEFAULT);
//        long totalHits = countResponse.getHits().getTotalHits().value;
//        LOGGER.info("Total hits: {}", totalHits);
//
//        // Start the timer
//        Instant startTime = Instant.now();
//
//        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
//        String scrollId = searchResponse.getScrollId();
//        SearchHit[] searchHits = searchResponse.getHits().getHits();
//
//        while (searchHits != null && searchHits.length > 0) {
//            for (SearchHit hit : searchHits) {
//                Object publishedDateObj = hit.getSourceAsMap().get("published_date");
//                LocalDate date = null;
//
//                if (publishedDateObj instanceof String) {
//                    date = LocalDate.parse(publishedDateObj.toString(), formatter);
//                } else if (publishedDateObj instanceof List) {
//                    List<String> publishedDateList = (List<String>) publishedDateObj;
//                    if (!publishedDateList.isEmpty()) {
//                        date = LocalDate.parse(publishedDateList.get(0), formatter);
//                    }
//                }
//
//                // Count frequency of keyword in content field
//                String content = hit.getSourceAsMap().get("content").toString();
//                int frequency = (content.split(keyword, -1).length) - 1;
//
//                // Check if date already exists in map; if not, create new entry
//                if (!dateFrequencyMap.containsKey(date)) {
//                    dateFrequencyMap.put(date, 0);
//                }
//
//                // Add frequency to existing total for this date
//                int existingTotal = dateFrequencyMap.get(date);
//                dateFrequencyMap.put(date, existingTotal + frequency);
//            }
//
//            // Get the scrollId for the next batch of results
//            // Send scroll request to retrieve next batch of search hits
//            SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId);
//            scrollRequest.scroll(TimeValue.timeValueMinutes(1L));
//            searchResponse = client.scroll(scrollRequest, RequestOptions.DEFAULT);
//            scrollId = searchResponse.getScrollId();
//            searchHits = searchResponse.getHits().getHits();
//        }
//
//        // Clean up Elasticsearch client resources
//        LOGGER.info("Cleaning up Elasticsearch client resources...");
//        ClearScrollRequest clearScrollRequest = new ClearScrollRequest();
//        clearScrollRequest.addScrollId(scrollId);
//        client.clearScroll(clearScrollRequest, RequestOptions.DEFAULT);
//        client.close();
//
//        // Sort and return map of date frequencies
//        LOGGER.info("Sorting date frequency map...");
//        Map<LocalDate, Integer> sortedMap = new TreeMap<>(Comparator.naturalOrder());
//        sortedMap.putAll(dateFrequencyMap);
//        return sortedMap;
//    }
//}
