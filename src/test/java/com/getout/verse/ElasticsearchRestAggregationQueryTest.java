//package com.getout.verse;
//
//import static org.junit.Assert.assertEquals;
//
//import java.io.IOException;
//import java.time.LocalDate;
//import java.util.Map;
//
//import com.getout.service.ElasticsearchRestAggregationQuery;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringRunner;
//
//@RunWith(SpringRunner.class)
//@SpringBootTest
//public class ElasticsearchRestAggregationQueryTest {
//
//    @Autowired
//    private ElasticsearchRestAggregationQuery query;
//
//    @Test
//    public void testAggregationQuery() throws IOException {
//        // Define array of keywords to track
//        String[] keywords = {"μητσοτάκης", "τσίπρας", "δημοκρατία"};
//
//        // Define date range for aggregation query
//        LocalDate startDate = LocalDate.of(2022, 1, 1);
//        LocalDate endDate = LocalDate.of(2022, 12, 31);
//
//        // Execute aggregation query
//         query.runAggregationQuery(startDate, endDate, keywords);
//
//        // Check aggregation results
////        assertEquals(365, result.size());
////        assertEquals(500, (int)result.get(LocalDate.of(2022, 1, 1)));
////        assertEquals(1500, (int)result.get(LocalDate.of(2022, 1, 2)));
////        assertEquals(1000, (int)result.get(LocalDate.of(2022, 1, 3)));
//        // ... and so on for each day of the year
//    }
//
//}
