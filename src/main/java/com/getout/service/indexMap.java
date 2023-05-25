package com.getout.service;


import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.json.JsonData;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import org.apache.http.HttpHost;
import org.apache.http.nio.entity.NStringEntity;

import org.elasticsearch.client.RestClient;
import org.elasticsearch.xcontent.XContentBuilder;
import org.elasticsearch.xcontent.XContentFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
public class indexMap {
    // ...

    @Autowired
    public static void indexSortedMap(String indexName, Map<LocalDate, Integer> sortedMap,String keyword) throws IOException, InterruptedException, ExecutionException, ExecutionException {
        // Set up Elasticsearch client
        RestClient restClient = RestClient.builder(
                new HttpHost("localhost", 9200)).build();

        // Create the transport with a Jackson mapper
        ElasticsearchTransport transport = new RestClientTransport(
                restClient, new JacksonJsonpMapper());

        // And create the API client
        ElasticsearchClient client = new ElasticsearchClient(transport);

        // Set up ExecutorService with an appropriate number of threads
        int numThreads = Math.max(1, Math.min(Runtime.getRuntime().availableProcessors(), sortedMap.size()));
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);

        // Submit indexing tasks
        List<Future<Void>> futures = new ArrayList<>();
        for (Map.Entry<LocalDate, Integer> entry : sortedMap.entrySet()) {
            futures.add(executorService.submit(() -> {
                LocalDate date = entry.getKey();
                Integer value = entry.getValue();

                JsonObject jsonObject = Json.createObjectBuilder()
                        .add("date", date.toString())
                        .add("value", value)
                        .add("keyword", keyword)
                        .build();

                Reader input = new StringReader(
                        jsonObject.toString()
                                .replace('\'', '"'));

                IndexRequest<JsonData> request = IndexRequest.of(i -> i
                        .index(indexName)
                        .withJson(input)
                );

                IndexResponse response = client.index(request);
                return null; // Return null as Void cannot be instantiated
            }));
        }

        // Wait for all tasks to complete
        for (Future<Void> future : futures) {
            future.get();
        }

        // Shutdown the executor service
        executorService.shutdown();

        // Close the Elasticsearch client
        restClient.close();
    }
}