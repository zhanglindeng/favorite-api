package com.dengzhanglin.favoriteapi.controller;

import com.dengzhanglin.favoriteapi.domain.Category;
import com.dengzhanglin.favoriteapi.domain.User;
import com.dengzhanglin.favoriteapi.domain.Website;
import com.dengzhanglin.favoriteapi.payload.AddWebsiteRequest;
import com.dengzhanglin.favoriteapi.payload.ApiResponse;
import com.dengzhanglin.favoriteapi.repository.WebsiteRepository;
import com.dengzhanglin.favoriteapi.security.CurrentUser;
import com.dengzhanglin.favoriteapi.security.UserPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.Instant;

@RestController
@RequestMapping(value = "/website")
public class WebsiteController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private WebsiteRepository websiteRepository;

    @Autowired
    public WebsiteController(WebsiteRepository websiteRepository) {
        this.websiteRepository = websiteRepository;
    }

    @PostMapping(value = "/add")
    public ResponseEntity<?> add(@CurrentUser UserPrincipal userPrincipal,
                                 @Valid @RequestBody AddWebsiteRequest addWebsiteRequest) {
        String loggerInfo = "[website:add]";

        this.logger.info(loggerInfo + " " + addWebsiteRequest.toString());

        // user_id
        Long userId = userPrincipal.getId();

        this.logger.info(loggerInfo + " " + userId);

        Website website = new Website();
        website.setUrl(addWebsiteRequest.getUrl());
        website.setName(addWebsiteRequest.getName());
        website.setDescription(addWebsiteRequest.getDescription());

        // time
        Instant instant = Instant.now();
        website.setCreatedAt(instant);
        website.setUpdatedAt(instant);

        // category
        // TODO 检查 category 是否存在
        Category category = new Category();
        category.setId(addWebsiteRequest.getCategoryId());
        website.setCategory(category);

        // user
        User user = new User();
        user.setId(userId);
        website.setUser(user);

        // TODO sort

        this.websiteRepository.save(website);

        return ResponseEntity.ok(new ApiResponse(true, "OK"));
    }
}
