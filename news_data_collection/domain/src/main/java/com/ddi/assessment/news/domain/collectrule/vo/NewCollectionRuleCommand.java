package com.ddi.assessment.news.domain.collectrule.vo;

import java.util.Objects;

public record NewCollectionRuleCommand(
    Long userId,
    Long keywordId,
    Long siteId,
    String keyword,
    String newsSite,
    String siteUrl,
    String interval
) {
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        NewCollectionRuleCommand that = (NewCollectionRuleCommand) o;
        return Objects.equals(userId, that.userId) && Objects.equals(keyword, that.keyword) && Objects.equals(newsSite, that.newsSite) && Objects.equals(interval, that.interval);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, keyword, newsSite, interval);
    }
}
