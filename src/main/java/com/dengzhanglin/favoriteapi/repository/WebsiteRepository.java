package com.dengzhanglin.favoriteapi.repository;

import com.dengzhanglin.favoriteapi.domain.Website;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WebsiteRepository extends CrudRepository<Website, Long> {
    @Query(value = "select * from website where user_id = :userId order by sort asc,id desc", nativeQuery = true)
    List<Website> findByUserOrderBySort(@Param("userId") Long userId);
}
