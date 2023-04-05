//package com.getout.verse;
//
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.fasterxml.jackson.databind.ObjectMapper;
////import com.getout.model.News;
////import com.getout.repository.NewsRepository;
//import com.getout.model.News;
//import com.getout.repository.NewsRepository;
//import com.getout.service.TopicService;
//
//import org.apache.http.HttpHost;
//import org.elasticsearch.client.RestClient;
//import org.elasticsearch.client.RestHighLevelClient;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.format.annotation.DateTimeFormat;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.Date;
//import org.springframework.format.annotation.DateTimeFormat;
//
//
//
//import java.io.IOException;
//import java.util.*;
//
//@RestController
//@RequestMapping("/api/news")
//public class NewsController {
//
////    @Autowired
////    private NewsRepository newsRepository;
//
//
//
//
//    @Autowired
//    private TopicService topicService;
//
////    @GetMapping("/keywords")
////    public ResponseEntity<Map<String, Integer>> getKeywordCounts(@RequestParam("keyword") List<String> keywords) {
////        Map<String, Integer> keywordCounts = topicService.getKeywordCounts(keywords);
////        return ResponseEntity.ok(keywordCounts);
////    }
//
//
//// ...
//
////    @GetMapping("/keyword-counts")
////    public ResponseEntity<Map<String, Map<String, Long>>> getKeywordCounts(
////            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
////            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate,
////            @RequestParam("keywords") String[] keywords) throws IOException {
////
////
////         RestHighLevelClient client = new RestHighLevelClient(
////                RestClient.builder(new HttpHost("localhost", 9200, "http")));
////        TopicService topicService = new TopicService(client);
////        Map<String, Map<String, Long>> result = topicService.retrieveKeywordCounts(startDate, endDate, keywords);
////        return ResponseEntity.ok(result);
////    }
//
//
//
//
//
//
//
//
//
//
//
////
////    public NewsController(NewsRepository newsRepository) {
////        this.newsRepository = newsRepository;
////    }
////    @PostMapping
////    public News createNews(@RequestBody News news) {
////        return newsRepository.save(news);
////    }
////
////    @GetMapping
////    public ResponseEntity<?> getNews(@RequestParam(required = false) String id) {
////        if (id != null) {
////            Optional<News> news = newsRepository.findById(id);
////            return news.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
////        } else {
////            List<News> allNews = (List<News>) newsRepository.findAll();
////            return ResponseEntity.ok(allNews);
////        }
////    }
//
//
//
//
////    @PutMapping("/{id}")
////    public News updateNews(@PathVariable String id, @RequestBody News updatedNews) {
////        return newsRepository.save(updatedNews);
////    }
////
////    @DeleteMapping("/{id}")
////    public void deleteNews(@PathVariable String id) {
////        newsRepository.deleteById(id);
////    }
//}
