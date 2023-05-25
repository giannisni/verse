package com.getout.service;

import com.getout.util.Constants;
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

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
public class KeywordFrequencyService {

    public static Map<LocalDate, Integer> getKeywordCounts(String keyword, LocalDate startDate, LocalDate endDate) throws IOException {

        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(new HttpHost(Constants.elastic_host, 9200, "http")));

        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery()
                .must(QueryBuilders.termQuery("keyword.keyword", keyword))
                .must(QueryBuilders.rangeQuery("date").gte(startDate).lte(endDate));

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(boolQuery);
        searchSourceBuilder.sort("date", SortOrder.ASC).size(10000); // Add this line to sort the results by date in ascending order

        SearchRequest searchRequest = new SearchRequest("daily_keyword_counts");
        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

        Map<LocalDate, Integer> keywordCounts = new HashMap<>();
        searchResponse.getHits().forEach(hit -> {
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            LocalDate date = LocalDate.parse((String) sourceAsMap.get("date"));
            Integer value = (Integer) sourceAsMap.get("value");
            keywordCounts.put(date, value);
        });

        return keywordCounts;
    }
}
