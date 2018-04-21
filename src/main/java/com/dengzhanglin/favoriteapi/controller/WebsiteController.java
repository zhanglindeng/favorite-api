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
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.Instant;
import java.util.Optional;

@RestController
@RequestMapping(value = "/website")
public class WebsiteController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private WebsiteRepository websiteRepository;

    @Autowired
    public WebsiteController(WebsiteRepository websiteRepository) {
        this.websiteRepository = websiteRepository;
    }

    @PostMapping(value = "/del/{id}")
    public ResponseEntity<?> del(@PathVariable("id") Long id, @CurrentUser UserPrincipal userPrincipal) {
        String loggerInfo = "[website:del]";

        // user_id
        Long userId = userPrincipal.getId();
        this.logger.info(loggerInfo + " " + userId + " " + id);

        Optional<Website> row = this.websiteRepository.findById(id);
        if (!row.isPresent()) {
            this.logger.info(loggerInfo + " website");
            return ResponseEntity.ok(new ApiResponse(false, "网站没有找到"));
        }
        Website website = row.get();
        if (website.getUser() == null) {
            this.logger.info(loggerInfo + " user");
            return ResponseEntity.ok(new ApiResponse(false, "网站没有找到"));
        }
        if (website.getCategory() == null) {
            this.logger.info(loggerInfo + " category");
            return ResponseEntity.ok(new ApiResponse(false, "网站没有找到"));
        }
        if (!website.getUser().getId().equals(userId)) {
            this.logger.info(loggerInfo + " user_id");
            return ResponseEntity.ok(new ApiResponse(false, "网站没有找到"));
        }

        // TODO 怎么知道是否删除成功
        this.websiteRepository.delete(website);
        this.logger.info(loggerInfo + " deleted " + userId + " " + id);

        return ResponseEntity.ok(new ApiResponse(true, "OK"));
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
