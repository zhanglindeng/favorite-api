package com.dengzhanglin.favoriteapi.repository;

import com.dengzhanglin.favoriteapi.domain.RegisterVerifyCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.time.Instant;

public interface RegisterVerifyCodeRepository extends CrudRepository<RegisterVerifyCode, Long> {
    Page<RegisterVerifyCode> findByEmail(String email, Pageable pageable);

    Long countByEmailAndCreatedAtAfter(String email, Instant createdAt);
}
