package com.ddi.assessment.news.domain.keyword.service;

import com.ddi.assessment.news.domain.keyword.entity.JpaKeyword;
import com.ddi.assessment.news.domain.keyword.repository.JpaKeywordRepository;
import com.ddi.assessment.news.domain.keyword.vo.ExistingKeyword;
import com.ddi.assessment.news.domain.keyword.vo.NewKeywordCommand;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class KeywordServiceImpl implements KeywordService {

    private final JpaKeywordRepository jpaKeywordRepository;

    public KeywordServiceImpl(JpaKeywordRepository jpaKeywordRepository) {
        this.jpaKeywordRepository = jpaKeywordRepository;
    }

    @Override
    public JpaKeyword getKeyword(Long keywordId) {

        Optional<JpaKeyword> keywordOrEmpty = jpaKeywordRepository.findById(keywordId);

        if(keywordOrEmpty.isEmpty()) {
            throw new IllegalArgumentException("Keyword Not Found");
        }

        return keywordOrEmpty.get();

    }

    @Override
    public JpaKeyword findOrCreateKeyword(String keyword) {

        Optional<JpaKeyword> keywordOrEmpty = jpaKeywordRepository.findByWord(keyword);

        if(keywordOrEmpty.isEmpty()) {
            return jpaKeywordRepository.save(new JpaKeyword(keyword));
        }

        return keywordOrEmpty.get();

    }

    @Override
    public ExistingKeyword findOrCreateKeyword(NewKeywordCommand command) {

        JpaKeyword jpaKeyword = findOrCreateKeyword(command.keyword());

        return new ExistingKeyword(jpaKeyword.getId(), jpaKeyword.getWord());
    }

    @Override
    public Set<ExistingKeyword> findOrCreateKeywords(Set<NewKeywordCommand> command) {

        Set<String> words = command.stream()
                .map(NewKeywordCommand::keyword)
                .collect(Collectors.toSet());

        List<JpaKeyword> existingKeywords = jpaKeywordRepository.findByWordIn(words);

        Set<String> existingWords = existingKeywords.stream()
                .map(JpaKeyword::getWord)
                .collect(Collectors.toSet());

        List<JpaKeyword> newKeywords = words.stream()
                .filter(word -> !existingWords.contains(word))
                .map(JpaKeyword::new)
                .toList();

        List<JpaKeyword> savedKeywords = jpaKeywordRepository.saveAll(newKeywords);

        return Stream.concat(existingKeywords.stream(), savedKeywords.stream())
                .map(k -> new ExistingKeyword(k.getId(), k.getWord()))
                .collect(Collectors.toSet());

    }

}
