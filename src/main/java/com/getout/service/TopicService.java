//package com.getout.service;
//
//import org.elasticsearch.action.search.SearchRequest;
//import org.elasticsearch.action.search.SearchResponse;
//import org.elasticsearch.client.RequestOptions;
//import org.elasticsearch.client.RestHighLevelClient;
//import org.elasticsearch.index.query.BoolQueryBuilder;
//import org.elasticsearch.index.query.QueryBuilders;
//import org.elasticsearch.index.query.RangeQueryBuilder;
//import org.elasticsearch.script.Script;
//import org.elasticsearch.search.SearchHit;
//import org.elasticsearch.search.SearchHits;
//import org.elasticsearch.search.aggregations.Aggregation;
//import org.elasticsearch.search.aggregations.AggregationBuilders;
//import org.elasticsearch.search.aggregations.bucket.filter.Filter;
//import org.elasticsearch.search.aggregations.bucket.filter.FilterAggregationBuilder;
//import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
//import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
//import org.elasticsearch.search.aggregations.bucket.terms.Terms;
//import org.elasticsearch.search.aggregations.metrics.Cardinality;
//import org.elasticsearch.search.aggregations.metrics.Sum;
//import org.elasticsearch.search.builder.SearchSourceBuilder;
//import org.elasticsearch.search.sort.SortOrder;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.io.IOException;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.time.*;
//import java.time.format.DateTimeFormatter;
//import java.util.*;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//
//@Service
//public class TopicService {
//    private static final Logger logger = LoggerFactory.getLogger(TopicService.class);
//
//    @Autowired
//    private static RestHighLevelClient client;
//
//    @Autowired
//    public TopicService(RestHighLevelClient client) {
//        this.client = client;
//    }
//
//    public Map<String, Map<String, Long>> retrieveKeywordCounts(Date startDate, Date endDate, String[] keywords) throws IOException {
//        Map<String, Map<String, Long>> result = new HashMap<>();
//
//        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
//        boolQuery.filter(QueryBuilders.rangeQuery("published_date").gte(startDate).lte(endDate));
//        BoolQueryBuilder keywordsQuery = QueryBuilders.boolQuery();
//
//        for (String keyword : keywords) {
//            keywordsQuery.should(QueryBuilders.matchPhraseQuery("content", keyword));
//        }
//
//        boolQuery.must(keywordsQuery);
//
//        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
//        searchSourceBuilder.query(boolQuery);
//        searchSourceBuilder.size(10000);
//        searchSourceBuilder.sort("published_date", SortOrder.ASC);
//
//        SearchRequest searchRequest = new SearchRequest("staki");
//        searchRequest.source(searchSourceBuilder);
//
//        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
//
//        for (SearchHit hit : searchResponse.getHits()) {
//            String content = (String) hit.getSourceAsMap().get("content");
//            Instant instant = Instant.parse((String) hit.getSourceAsMap().get("published_date"));
//            LocalDate localDate = instant.atZone(ZoneId.systemDefault()).toLocalDate();
//            Map<String, Long> dateCountMap = result.computeIfAbsent(localDate.toString(), k -> new HashMap<>());
//
//            for (String keyword : keywords) {
//                if (content.contains(keyword)) {
//                    dateCountMap.merge(keyword, 1L, Long::sum);
//                }
//            }
//        }
//
//        return result;
//    }
//
//
//
//
//
//
//    private long countWordOccurrences(String text, String word) {
//        Pattern pattern = Pattern.compile("\\b" + Pattern.quote(word) + "\\b", Pattern.CASE_INSENSITIVE);
//        Matcher matcher = pattern.matcher(text);
//        long count = 0;
//        while (matcher.find()) {
//            count++;
//        }
//        return count;
//    }
//
//
//
//}
//
