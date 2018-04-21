package com.dengzhanglin.favoriteapi.repository;

import com.dengzhanglin.favoriteapi.domain.Category;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CategoryRepository extends CrudRepository<Category, Long> {
    @Query(value = "select * from category where user_id = :userId order by sort asc,id desc", nativeQuery = true)
    List<Category> findByUserOrderBySort(@Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query(value = "update category c set c.sort = :sort,c.updated_at = :updatedAt where c.id = :id", nativeQuery = true)
    Integer modifyById(@Param("sort") Integer sort, @Param("id") Long id, @Param("updatedAt") String updatedAt);
}
