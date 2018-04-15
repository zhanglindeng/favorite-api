package com.dengzhanglin.favoriteapi.service;

import com.dengzhanglin.favoriteapi.domain.User;
import com.dengzhanglin.favoriteapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Optional;

@Service
public class UserService {
    private UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User register(String email, String password) {
        if (this.userRepository.existsByEmail(email)) {
            return null;
        }
        User user = new User();
        user.setEmail(email);
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        user.setPassword(passwordEncoder.encode(password));
        user.setStatus(1);
        Long currentTime = System.currentTimeMillis();
        Timestamp timestamp = new Timestamp(currentTime);
        user.setUpdatedAt(timestamp);
        user.setCreatedAt(timestamp);
        this.userRepository.save(user);
        return  user;
    }

    public Optional<User> findById(Long id) {
        return this.userRepository.findById(id);
    }
}
