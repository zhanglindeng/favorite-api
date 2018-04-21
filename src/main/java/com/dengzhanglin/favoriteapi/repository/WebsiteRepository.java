package com.dengzhanglin.favoriteapi.repository;

import com.dengzhanglin.favoriteapi.domain.Website;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface WebsiteRepository extends CrudRepository<Website, Long> {
    @Query(value = "select * from website where user_id = :userId order by sort asc,id desc", nativeQuery = true)
    List<Website> findByUserOrderBySort(@Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query(value = "update website w set w.sort = :sort,w.updated_at = :updatedAt where w.id = :id", nativeQuery = true)
    Integer modifyById(@Param("sort") Integer sort, @Param("id") Long id, @Param("updatedAt") String updatedAt);
}
