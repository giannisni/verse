package com.getout.service;

import com.getout.util.Constants;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import py4j.GatewayServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.math3.stat.regression.SimpleRegression;

@Service
public class KeywordFrequencyService {

    /**
     * Retrieves keyword counts from Elasticsearch for a given keyword and date range.
     *
     * @param keyword    The keyword to search for.
     * @param startDate  The start date of the range.
     * @param endDate    The end date of the range.
     * @return A map of dates to keyword counts.
     * @throws IOException If there's an issue communicating with Elasticsearch.
     */

    public static Map<LocalDate, Integer> getKeywordCounts(String keyword, LocalDate startDate, LocalDate endDate) throws IOException {

        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost", 9200, "http")));

        //List<String> keywords = Arrays.asList("Μητσοτάκης", "Τσίπρας", "Βαρουφάκης", "Κουτσούμπας","Ανδρουλάκης","Kασιδιάρης","ΣΥΡΙΖΑ","Ουκρανία","Νάτο","Πόλεμος στην Ουκρανία","Ρωσία","Πούτιν");

        Map<String, List<String>> topics = new HashMap<>();
        topics.put("τέμπη", Arrays.asList("Hellenic Train","έγκλημα στα Τέμπη","τηλεδιοίκηση","Καραμανλής","φωτεινή σηματοδότηση","σιδηροδρομικό","ΤΡΑΙΝΟΣΕ","ΟΣΕ","τραγωδία στα Τέμπη","Σταθμάρχη","σύγκρουση των δύο τρένων","57 ανθρώπους"));
        topics.put("ουκρανικό", Arrays.asList("Ουκρανία","Νάτο","Πόλεμος στην Ουκρανία","Ρωσία","Πούτιν"));
        //topics.put("πυρήνικά", Arrays.asList(""));

        // Add more topics and keywords as needed


        List<String> keywords = topics.get(keyword);




        Map<LocalDate, Integer> keywordCounts = new HashMap<>();

        if (keywords != null) {
            //List<String> keywords = topics.get(topicKey);

            for (String key : keywords) {
                BoolQueryBuilder boolQuery = QueryBuilders.boolQuery()
                        .must(QueryBuilders.termQuery("keyword.keyword", key))
                        .must(QueryBuilders.rangeQuery("date").gte(startDate).lte(endDate));

                SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
                searchSourceBuilder.query(boolQuery);
                searchSourceBuilder.sort("date", SortOrder.ASC).size(10000);

                SearchRequest searchRequest = new SearchRequest("daily_keyword_counts");
                searchRequest.source(searchSourceBuilder);

                SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

                searchResponse.getHits().forEach(hit -> {
                    Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                    LocalDate date = LocalDate.parse((String) sourceAsMap.get("date"));
                    Integer value = (Integer) sourceAsMap.get("value");
                    keywordCounts.put(date, keywordCounts.getOrDefault(date, 0) + value);
                });
            }
        } else {
            BoolQueryBuilder boolQuery = QueryBuilders.boolQuery()
                    .must(QueryBuilders.termQuery("keyword.keyword", keyword))
                    .must(QueryBuilders.rangeQuery("date").gte(startDate).lte(endDate));

            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.query(boolQuery);
            searchSourceBuilder.sort("date", SortOrder.ASC).size(10000);

            SearchRequest searchRequest = new SearchRequest("daily_keyword_counts");
            searchRequest.source(searchSourceBuilder);

            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

            searchResponse.getHits().forEach(hit -> {
                Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                LocalDate date = LocalDate.parse((String) sourceAsMap.get("date"));
                Integer value = (Integer) sourceAsMap.get("value");
                keywordCounts.put(date, value);
            });
        }

        return keywordCounts;
    }


    /**
     * Calculates the correlation coefficient between two sets of keyword counts.
     *
     * @param keywordCounts1 The first set of keyword counts.
     * @param keywordCounts2 The second set of keyword counts.
     * @return The correlation coefficient.
     */

    public static double calculateCorrelation(Map<LocalDate, Integer> keywordCounts1, Map<LocalDate, Integer> keywordCounts2) {
        // Convert the keyword counts maps to arrays
        // Get the common keys (dates) in both maps
        Set<LocalDate> commonDates = new HashSet<>(keywordCounts1.keySet());
        commonDates.retainAll(keywordCounts2.keySet());

        // Filter the maps to only include entries with the common keys
        Map<LocalDate, Integer> filteredCounts1 = keywordCounts1.entrySet().stream()
                .filter(entry -> commonDates.contains(entry.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        Map<LocalDate, Integer> filteredCounts2 = keywordCounts2.entrySet().stream()
                .filter(entry -> commonDates.contains(entry.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        // Convert the filtered maps to arrays
        double[] counts1 = filteredCounts1.values().stream().mapToDouble(Integer::doubleValue).toArray();
        double[] counts2 = filteredCounts2.values().stream().mapToDouble(Integer::doubleValue).toArray();

        // Calculate the correlation coefficient
        PearsonsCorrelation pearsonsCorrelation = new PearsonsCorrelation();
        double correlation = pearsonsCorrelation.correlation(counts1, counts2);

        // Format the correlation
        DecimalFormat df = new DecimalFormat("#.##");
        correlation = Double.valueOf(df.format(correlation));

        return correlation;

    }


    /**
     * Predicts the keyword count for a given keyword based on the correlation with another keyword.
     *
     * @param keyword1   The first keyword.
     * @param keyword2   The second keyword.
     * @param startDate  The start date of the range.
     * @param endDate    The end date of the range.
     * @return The predicted keyword count.
     * @throws IOException, InterruptedException If there's an issue executing the Python script or reading its output.
     */
    public static int predictKeywordCount(String keyword1, String keyword2, LocalDate startDate, LocalDate endDate) throws IOException, InterruptedException {
        // Calculate keyword counts maps
        Map<LocalDate, Integer> keywordCountsMap1 = getKeywordCounts(keyword1, startDate, endDate);
        Map<LocalDate, Integer> keywordCountsMap2 = getKeywordCounts(keyword2, startDate, endDate);

        // Convert the maps to csv strings
        String csv1 = convertMapToCsv(keywordCountsMap1);
        String csv2 = convertMapToCsv(keywordCountsMap2);

        //System.out.println(csv1 + " this " + csv2);
        // Write the csv strings to files
        Path file1 = Paths.get("keywordCountsMap1.csv");
        Files.write(file1, csv1.getBytes()
        );

        Path file2 = Paths.get("keywordCountsMap2.csv");
        Files.write(file2, csv2.getBytes());

        // Execute the Python script

        // Execute the Python script
        Process p = Runtime.getRuntime().exec("python3 src/main/resources/time_series_analysis.py " + file1 + " " + file2);
        // Read the output from the Python script
        double forecastedCount = 0.0;
        try (BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
            String line;
            while ((line = in.readLine()) != null) {
                System.out.println(line);  // Print every line of output
                if (!line.isEmpty()) {
                    try {
                        forecastedCount = Double.parseDouble(line);
                    } catch (NumberFormatException e) {
                        System.err.println("Python script output is not a number: " + line);
                    }
                }
            }
        }

        try (BufferedReader err = new BufferedReader(new InputStreamReader(p.getErrorStream()))) {
            String errLine;
            while ((errLine = err.readLine()) != null) {
                System.out.println(errLine);
            }
        }

        p.waitFor();



        // Delete the csv files
        Files.delete(file1);
        Files.delete(file2);

        int roundedForecastedCount = (int) Math.round(forecastedCount);

        // Return the forecasted count
        return roundedForecastedCount;
    }

    /**
     * Converts a map of dates to keyword counts to a CSV string.
     *
     * @param map The map to convert.
     * @return The CSV string.
     */
    private static String convertMapToCsv(Map<LocalDate, Integer> map) {
        StringBuilder sb = new StringBuilder();
        sb.append("date,count\n");
        map.forEach((k, v) -> sb.append(k.toString()).append(",").append(v).append("\n"));
        return sb.toString();
    }



}
