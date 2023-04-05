//package com.getout.service;
//
//import java.io.IOException;
//import java.time.LocalDate;
//import java.util.HashMap;
//import java.util.Map;
//
//import org.apache.http.HttpHost;
//import org.elasticsearch.action.search.SearchRequest;
//import org.elasticsearch.action.search.SearchResponse;
//import org.elasticsearch.client.RequestOptions;
//import org.elasticsearch.client.RestClient;
//import org.elasticsearch.client.RestHighLevelClient;
//import org.elasticsearch.index.query.BoolQueryBuilder;
//import org.elasticsearch.index.query.QueryBuilders;
//import org.elasticsearch.search.aggregations.AggregationBuilders;
//import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
//import org.elasticsearch.search.builder.SearchSourceBuilder;
//import org.springframework.stereotype.Component;
//
//@Component
//public class ElasticsearchRestAggregationQuery {
//
//    public void runAggregationQuery(LocalDate startDate,LocalDate endDate,String[] keywords) throws IOException {
//        // Define array of keywords to track
////        String[] keywords = {"keyword1", "keyword2", "keyword3"};
////
////        // Define date range for aggregation query
////        LocalDate startDate = LocalDate.of(2022, 1, 1);
////        LocalDate endDate = LocalDate.of(2022, 12, 31);
//
//        // Set up Elasticsearch REST client
//        RestHighLevelClient client = new RestHighLevelClient(
//                RestClient.builder(new HttpHost("localhost", 9200, "http")));
//
//        try {
//            // Set up aggregation query
//            BoolQueryBuilder query = QueryBuilders.boolQuery()
//                    .filter(QueryBuilders.rangeQuery("date")
//                            .gte(startDate.toString())
//                            .lte(endDate.toString()));
//            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
//            sourceBuilder.query(query);
//            sourceBuilder.aggregation(AggregationBuilders.dateHistogram("date_histogram")
//                    .field("date")
//                    .format("yyyy-MM-dd")
//                    .fixedInterval(DateHistogramInterval.DAY)
//                    .subAggregation(AggregationBuilders.sum("total_frequency")
//                            .field(keywords[0])
//                            .field(keywords[1])
//                            .field(keywords[2])));
//
//            SearchRequest searchRequest = new SearchRequest("norconex2");
//            searchRequest.source(sourceBuilder);
//
//            // Execute aggregation query
//            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
//
//            // Process aggregation results
//            Map<LocalDate, Integer> dateFrequencyMap = new HashMap<>();
////            searchResponse.getAggregations()
////                    .get("date_histogram")
////                    .getBuckets()
////                    .forEach(bucket -> {
////                        String dateString = bucket.getKeyAsString();
////                        LocalDate date = LocalDate.parse(dateString);
////                        double totalFrequency = ((org.elasticsearch.search.aggregations.metrics.sum.Sum) bucket.getAggregations().get("total_frequency")).getValue();
////                        dateFrequencyMap.put(date, (int)totalFrequency);
////                    });
//
//            // Print out map data
//            for (Map.Entry<LocalDate, Integer> entry : dateFrequencyMap.entrySet()) {
//                System.out.println("Date: " + entry.getKey() + ", Total Frequency: " + entry.getValue());
//            }
//        } finally {
//            // Close Elasticsearch REST client
//            client.close();
//        }
//    }
//}
