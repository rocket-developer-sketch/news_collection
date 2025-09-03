package com.ddi.assessment.news.api.article.controller;

import com.ddi.assessment.news.api.dto.PageResponse;
import com.ddi.assessment.news.api.dto.ResultResponse;
import com.ddi.assessment.news.api.article.application.CollectedNewsArticleService;

import com.ddi.assessment.news.api.security.JwtUserDetail;
import com.ddi.assessment.news.domain.article.vo.CollectedNewsArticle;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/news")
public class NewsArticleController {

    private final CollectedNewsArticleService collectedNewsArticleService;

    public NewsArticleController(CollectedNewsArticleService collectedNewsArticleService) {
        this.collectedNewsArticleService = collectedNewsArticleService;
    }

    @GetMapping("/all")
    public ResponseEntity<ResultResponse<PageResponse<CollectedNewsArticle>>> getNewsArticles(
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(ResultResponse.success(collectedNewsArticleService.getNewsArticles(pageNo, size)));
    }

    @GetMapping
    public ResponseEntity<ResultResponse<PageResponse<CollectedNewsArticle>>> getUserNewsArticles (
            @AuthenticationPrincipal JwtUserDetail auth,
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(ResultResponse.success(collectedNewsArticleService.getUserNewsArticles(auth.getUserId(), pageNo, size)));
    }

}
