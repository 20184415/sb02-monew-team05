package com.part2.monew.repository;

import com.part2.monew.entity.InterestKeyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface InterestKeywordRepository extends JpaRepository<InterestKeyword, UUID> {

    /**
     * 특정 관심사에 연결된 키워드들을 조회합니다
     * @param interestName 관심사 이름
     * @return 해당 관심사의 키워드 목록
     */
    @Query(value = """
        SELECT k.name 
        FROM interests_keywords ik
        JOIN interests i ON ik.interest_id = i.interest_id
        JOIN keywords k ON ik.keyword_id = k.keyword_id
        WHERE i.name = :interestName
        """, nativeQuery = true)
    List<String> findKeywordsByInterestName(@Param("interestName") String interestName);
    
    /**
     * 모든 관심사와 해당 키워드들을 매핑하여 조회합니다
     * @return [관심사명, 키워드명] 배열 목록
     */
    @Query(value = """
        SELECT i.name as interest_name, k.name as keyword_name
        FROM interests_keywords ik
        JOIN interests i ON ik.interest_id = i.interest_id
        JOIN keywords k ON ik.keyword_id = k.keyword_id
        ORDER BY i.name, k.name
        """, nativeQuery = true)
    List<Object[]> findAllInterestKeywordMappings();
} 