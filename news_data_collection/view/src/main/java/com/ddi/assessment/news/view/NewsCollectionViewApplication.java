package com.ddi.assessment.news.view;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.ddi.assessment.news.view")
public class NewsCollectionViewApplication {
    public static void main(String[] args) {
        SpringApplication.run(NewsCollectionViewApplication.class, args);
    }
}
