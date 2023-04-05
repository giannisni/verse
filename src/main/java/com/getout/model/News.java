//package com.getout.model;
//
//import lombok.Data;
//import org.springframework.data.annotation.Id;
//import org.springframework.data.elasticsearch.annotations.DateFormat;
//import org.springframework.data.elasticsearch.annotations.Document;
//import org.springframework.data.elasticsearch.annotations.Field;
//import org.springframework.data.elasticsearch.annotations.FieldType;
//
//import java.util.Date;
//
//@Document(indexName = "norconex2")
//@Data
//public class News {
//
//    @Id
//    private String id;
//
//    @Field(name = "title", type = FieldType.Text)
//    private String title;
//
//    @Field(name = "description", type = FieldType.Text)
//    private String description;
//
//    @Field(name = "content", type = FieldType.Text)
//    private String content;
//
//    @Field(name = "Date", type = FieldType.Date, format = DateFormat.date_optional_time)
//    private Date date;
//
//    @Field(name = "document.reference", type = FieldType.Keyword)
//    private String documentReference;
//
//    @Field(name = "domain.url", type = FieldType.Keyword)
//    private String domainUrl;
//
//    // Constructors, getters, and setters omitted for brevity
//}
