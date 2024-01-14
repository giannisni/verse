//package com.getout.verse;
//
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.web.client.TestRestTemplate;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//public class TweetMetricsControllerTest {
//
//    @Autowired
//    private TestRestTemplate restTemplate;
//
//    @Test
//    public void calculateTweetMetricsTest() {
//        String startDate = "2023-04-25";
//        String endDate = "2023-04-27";
//
//        ResponseEntity<Void> response = restTemplate.postForEntity("/api/news/tweet-metrics?startDate=" + startDate + "&endDate=" + endDate, null, Void.class);
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//    }
//}
