package com.getout.service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Service;

@Service
public class WordFrequencyBatch {

    public static Map<LocalDate, Integer> searchKeywordFrequency(String indexName, String keyword, int batchSize) throws IOException {
        // Set up Elasticsearch client and query for keyword frequency data
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost", 9200, "http")));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        Map<LocalDate, Integer> dateFrequencyMap = new HashMap<>();

        // Define search query
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.boolQuery()
                .must(QueryBuilders.matchQuery("content", keyword))
                .filter(QueryBuilders.existsQuery("published_date")));

        searchSourceBuilder.size(batchSize);

        // Define initial offset
        int offset = 0;

        // Execute search query in batches until all results have been processed
        while (true) {
            // Set offset
            searchSourceBuilder.from(offset);

            // Define search request
            SearchRequest searchRequest = new SearchRequest(indexName);
            searchRequest.source(searchSourceBuilder);

            // Execute search request
            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            SearchHit[] searchHits = searchResponse.getHits().getHits();

            // Break out of loop if no hits are returned
            if (searchHits.length == 0) {
                break;
            }

            // Iterate through Elasticsearch query results and populate map
            for (SearchHit hit : searchHits) {
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
            }

            // Increment offset to retrieve next batch of results
            offset += batchSize;
        }

        // Clean up Elasticsearch client resources
        client.close();

        Map<LocalDate, Integer> sortedMap = new TreeMap<>(Comparator.naturalOrder());
        sortedMap.putAll(dateFrequencyMap);
        return sortedMap;
    }
}
