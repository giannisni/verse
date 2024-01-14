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
import org.elasticsearch.client.RestClient;
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
public class IndexMap {

    /**
     * Indexes a sorted map into Elasticsearch.
     *
     * @param indexName  The name of the Elasticsearch index.
     * @param sortedMap  The map containing date to integer mappings.
     * @param keyword    The keyword associated with the data.
     * @throws IOException, InterruptedException, ExecutionException If there's an issue indexing the data.
     */
    @Autowired
    public static void indexSortedMap(String indexName, Map<LocalDate, Integer> sortedMap, String keyword,String fromIndex) throws IOException, InterruptedException, ExecutionException {
        // Initialize Elasticsearch RestClient
        RestClient restClient = RestClient.builder(new HttpHost("elastic_host", 9200)).build();

        // Create the transport with a Jackson mapper
        ElasticsearchTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());

        // Initialize the Elasticsearch client
        ElasticsearchClient client = new ElasticsearchClient(transport);

        // Determine the number of threads based on available processors and map size
        int numThreads = Math.max(1, Math.min(Runtime.getRuntime().availableProcessors(), sortedMap.size()));
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);

        // List to hold futures of submitted tasks
        List<Future<Void>> futures = new ArrayList<>();

        // Iterate over the sorted map and submit indexing tasks
        for (Map.Entry<LocalDate, Integer> entry : sortedMap.entrySet()) {
            futures.add(executorService.submit(() -> {
                LocalDate date = entry.getKey();
                Integer value = entry.getValue();

                // Construct the JSON object for indexing
                JsonObject jsonObject = Json.createObjectBuilder()
                        .add("date", date.toString())
                        .add("value", value)
                        .add("keyword", keyword)
                        .add("index",fromIndex)
                        .build();

                Reader input = new StringReader(jsonObject.toString().replace('\'', '"'));
                System.out.println(jsonObject);

                System.out.println(input.toString());
                // Create and execute the index request
                IndexRequest<JsonData> request = IndexRequest.of(i -> i.index(indexName).withJson(input));
                IndexResponse response = client.index(request);

                return null; // Return null as Void cannot be instantiated
            }));
        }

        // Wait for all tasks to complete
        for (Future<Void> future : futures) {
            future.get();
        }
        System.out.println("Indexed index : " + indexName + " completed");
        // Cleanup resources
        executorService.shutdown();
        restClient.close();
    }
}
