package com.dengzhanglin.favoriteapi.repository;

import com.dengzhanglin.favoriteapi.domain.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {
    boolean existsByEmail(String email);
}
