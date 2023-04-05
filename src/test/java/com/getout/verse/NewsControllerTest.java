//package com.getout.verse;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.when;
//
//import java.io.IOException;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.Arrays;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Map;
//
//import co.elastic.clients.elasticsearch.core.SearchRequest;
//import com.getout.service.TopicService;
//import org.elasticsearch.action.search.SearchResponse;
//import org.elasticsearch.client.RequestOptions;
//import org.elasticsearch.client.RestHighLevelClient;
//import org.elasticsearch.index.query.BoolQueryBuilder;
//import org.elasticsearch.index.query.QueryBuilders;
//import org.elasticsearch.search.SearchHit;
//import org.elasticsearch.search.SearchHits;
//import org.elasticsearch.search.aggregations.bucket.filter.Filter;
//import org.elasticsearch.search.aggregations.bucket.filter.FilterAggregationBuilder;
//import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
//import org.elasticsearch.search.aggregations.bucket.terms.Terms;
//import org.elasticsearch.search.builder.SearchSourceBuilder;
//import org.elasticsearch.search.sort.SortOrder;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//public class TopicServiceTest {
//
//    private RestHighLevelClient client;
//    private TopicService topicService;
//
//    @BeforeEach
//    public void setUp() {
//        client = mock(RestHighLevelClient.class);
//        topicService = new TopicService(client);
//    }
//
//    @Test
//
//    public void testRetrieveKeywordCounts() throws IOException {
//        RestHighLevelClient client = mock(RestHighLevelClient.class);
//        TopicService topicService = new TopicService(client);
//
//        SearchResponse searchResponse = mock(SearchResponse.class);
//        SearchHits searchHits = mock(SearchHits.class);
//
//        SearchHit hit1 = mock(SearchHit.class);
//        Map<String, Object> sourceAsMap1 = new HashMap<>();
//        sourceAsMap1.put("published_date", "2023-03-28T00:00:00Z");
//        sourceAsMap1.put("content", "This is a test message");
//        hit1.sourceRef("test").sourceAsMap(sourceAsMap1);
//
//        SearchHit hit2 = mock(SearchHit.class);
//        Map<String, Object> sourceAsMap2 = new HashMap<>();
//        sourceAsMap2.put("published_date", "2023-03-29T00:00:00Z");
//        sourceAsMap2.put("content", "This is another test message");
//        hit2.sourceRef("test").sourceAsMap(sourceAsMap2);
//
//        SearchHit[] hits = new SearchHit[]{hit1, hit2};
//
//        when(searchResponse.getHits()).thenReturn(searchHits);
//        when(searchHits.iterator()).thenReturn(Arrays.asList(hits).iterator());
//
//        when(client.search(any(SearchRequest.class), any(RequestOptions.class))).thenReturn(searchResponse);
//
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//        Date startDate = sdf.parse("2023-03-28");
//        Date endDate = sdf.parse("2023-03-29");
//
//        String[] keywords = new String[]{"test", "message"};
//        Map<String, Map<String, Long>> result = topicService.retrieveKeywordCounts(startDate, endDate, keywords);
//
//        Map<String, Long> counts1 = result.get("2023-03-28");
//        assertEquals(2L, counts1.get("test").longValue());
//        assertEquals(2L, counts1.get("message").longValue());
//
//        Map<String, Long> counts2 = result.get("2023-03-29");
//        assertEquals(2L, counts2.get("test").longValue());
//        assertEquals(3L, counts2.get("message").longValue());
//    }
//
//
//    private SearchResponse mockSearchResponse(SearchHits hits) {
//        SearchResponse response = mock(SearchResponse.class);
//        when(response.getHits()).thenReturn(hits);
//        return response;
//    }
//}
