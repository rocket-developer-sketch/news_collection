package com.ddi.assessment.news.batch.collector.dto;

import java.util.List;

public class ParsedArticlesHolder {
    private List<ParsedNewsArticle> articles;
    private List<ParsedNewsPreview> previews;

    public List<ParsedNewsArticle> getArticles() {
        return articles;
    }

    public void setArticles(List<ParsedNewsArticle> articles) {
        this.articles = articles;
    }

    public List<ParsedNewsPreview> getPreviews() {
        return previews;
    }

    public void setPreviews(List<ParsedNewsPreview> previews) {
        this.previews = previews;
    }

    public int size() {
        return articles != null ? articles.size() : 0;
    }

    public boolean isEmpty() {
        return articles == null || articles.isEmpty();
    }
}
