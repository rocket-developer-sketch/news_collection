package com.ddi.assessment.news.domain.collectrule.exception;

public class DuplicateCollectRuleException extends RuntimeException {
    public DuplicateCollectRuleException(String message) {
        super(message);
    }
}