package com.dengzhanglin.favoriteapi.repository;

import com.dengzhanglin.favoriteapi.domain.RegisterVerifyCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;

public interface RegisterVerifyCodeRepository extends CrudRepository<RegisterVerifyCode, Long> {
    Page<RegisterVerifyCode> findByEmail(String email, Pageable pageable);

    Long countByEmailAndCreatedAtAfter(String email, Instant createdAt);

    @Query(value = "select * from register_verify_code rvc where email = :email order by id desc limit 1", nativeQuery = true)
    Optional<RegisterVerifyCode> findLastByEmail(@Param("email") String email);
}
