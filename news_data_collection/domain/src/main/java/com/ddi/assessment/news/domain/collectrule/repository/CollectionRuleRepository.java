package com.ddi.assessment.news.domain.collectrule.repository;

import com.ddi.assessment.news.domain.collectrule.entity.JpaCollectRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CollectionRuleRepository extends JpaRepository<JpaCollectRule, Long>, CollectionRuleCustomRepository  {

    @Modifying(clearAutomatically = true)
    @Query("""
        UPDATE JpaCollectRule j
        SET j.interval.id = :intervalId,
            j.isActive = :isActive
        WHERE j.id = :configId
    """)
    int updateIntervalAndIsActive(@Param("configId") Long configId,
                                  @Param("intervalId") Long intervalId,
                                  @Param("isActive") Boolean isActive);

    @Query("""
        select r from JpaCollectRule r
        join fetch r.keyword
        join fetch r.newsSite
        join fetch r.interval
        where r.isActive = true
    """)
    List<JpaCollectRule> findByIsActiveTrue();

    @Query("SELECT r FROM JpaCollectRule r " +
            "JOIN FETCH r.user " +
            "JOIN FETCH r.keyword " +
            "JOIN FETCH r.newsSite " +
            "WHERE r.id = :id")
    Optional<JpaCollectRule> findByIdWithUserKeywordSite(@Param("id") Long id);

}
