package com.getout.service;

import org.apache.http.HttpHost;

import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.histogram.ParsedDateHistogram;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.Avg;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.getout.util.Constants.elastic_host;
import static org.elasticsearch.xcontent.XContentFactory.jsonBuilder;

@Service
public class TweetMetricsService {



    /**
     * Calculates metrics for tweets within a specified date range and stores the results in a new Elasticsearch index.
     *
     * @param startDate The start date of the range to analyze.
     * @param endDate   The end date of the range to analyze.
     * @throws IOException If there's an issue communicating with Elasticsearch.
     */
    public void calculateTweetMetrics(String startDate, String endDate) throws IOException {
        // Initialize Elasticsearch client
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(new HttpHost(elastic_host, 9200, "http")));

        // Build a query to filter tweets based on the provided date range
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery()
                .filter(QueryBuilders.rangeQuery("data.created_at").gte(startDate).lte(endDate));

        // Define aggregations to group by date and then by tag, calculating the average sentiment score for each tag
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                .query(boolQuery)
                .size(0)
                .aggregation(AggregationBuilders.dateHistogram("by_date")
                        .field("data.created_at")
                        .calendarInterval(DateHistogramInterval.DAY)
                        .subAggregation(AggregationBuilders.terms("by_tag").field("tag.keyword")
                                .subAggregation(AggregationBuilders.avg("avg_sentiment_score").field("sentiment_result.score")))
                );

        // Execute the search request on the "twitter" index
        SearchRequest searchRequest = new SearchRequest("twitter");
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

        // Extract the aggregated results
        ParsedDateHistogram dateHistogram = searchResponse.getAggregations().get("by_date");

        // Process each date bucket
        for (Histogram.Bucket bucket : dateHistogram.getBuckets()) {
            String date = bucket.getKeyAsString();
            ParsedStringTerms tags = bucket.getAggregations().get("by_tag");

            // Process each tag bucket within the date bucket
            for (Terms.Bucket tagBucket : tags.getBuckets()) {
                String tag = tagBucket.getKeyAsString();
                Avg avgSentimentScore = tagBucket.getAggregations().get("avg_sentiment_score");

                // Prepare the data to be indexed
                Map<String, Object> data = new HashMap<>();
                data.put("date", date);
                data.put("tag", tag);
                data.put("count", tagBucket.getDocCount());
                data.put("avg_sentiment_score", avgSentimentScore.getValue());

                // Index the results into the "tweet_metrics" index
                IndexRequest indexRequest = new IndexRequest("tweet_metrics").source(data);
                BulkRequest bulkRequest = new BulkRequest();
                bulkRequest.add(indexRequest);
                client.bulk(bulkRequest, RequestOptions.DEFAULT);
            }
        }
    }


}
