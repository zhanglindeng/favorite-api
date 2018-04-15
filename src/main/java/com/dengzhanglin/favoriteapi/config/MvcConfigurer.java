package com.dengzhanglin.favoriteapi.config;

import com.dengzhanglin.favoriteapi.controller.interceptor.JSONRequiredInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfigurer implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new JSONRequiredInterceptor()).addPathPatterns("/**");
    }
}
