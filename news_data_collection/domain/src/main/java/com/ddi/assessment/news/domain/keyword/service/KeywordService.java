package com.ddi.assessment.news.domain.keyword.service;

import com.ddi.assessment.news.domain.keyword.entity.JpaKeyword;
import com.ddi.assessment.news.domain.keyword.vo.ExistingKeyword;
import com.ddi.assessment.news.domain.keyword.vo.NewKeywordCommand;

import java.util.Set;

public interface KeywordService {
    JpaKeyword getKeyword(Long keywordId);
    JpaKeyword findOrCreateKeyword(String keyword);
    ExistingKeyword findOrCreateKeyword(NewKeywordCommand command);
    Set<ExistingKeyword> findOrCreateKeywords(Set<NewKeywordCommand> command);
}
