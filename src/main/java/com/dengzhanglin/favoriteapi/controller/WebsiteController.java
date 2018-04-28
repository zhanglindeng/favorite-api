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
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;

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
            return ResponseEntity.ok(new ApiResponse(1, "网站没有找到"));
        }
        Website website = row.get();
        if (website.getUser() == null) {
            this.logger.info(loggerInfo + " user");
            return ResponseEntity.ok(new ApiResponse(1, "网站没有找到"));
        }
        if (website.getCategory() == null) {
            this.logger.info(loggerInfo + " category");
            return ResponseEntity.ok(new ApiResponse(1, "网站没有找到"));
        }
        if (!website.getUser().getId().equals(userId)) {
            this.logger.info(loggerInfo + " user_id");
            return ResponseEntity.ok(new ApiResponse(1, "网站没有找到"));
        }

        // TODO 怎么知道是否删除成功
        this.websiteRepository.delete(website);
        this.logger.info(loggerInfo + " deleted " + userId + " " + id);

        return ResponseEntity.ok(new ApiResponse(0, "OK"));
    }

    @PostMapping(value = "/add")
    public ResponseEntity<?> add(@CurrentUser UserPrincipal userPrincipal,
                                 @Valid @RequestBody AddWebsiteRequest addWebsiteRequest) {
        String loggerInfo = "[website:add]";

        this.logger.info(loggerInfo + " " + addWebsiteRequest.toString());

        // user_id
        Long userId = userPrincipal.getId();

        this.logger.info(loggerInfo + " " + userId);

        // sort
        Integer sort = 0;
        List<Website> websites = this.websiteRepository.findByUserOrderBySort(userId);
        Iterator<Website> iterator = websites.iterator();
        Website temp;
        Map<Long, Integer> sortMap = new HashMap<>();
        while (iterator.hasNext()) {
            temp = iterator.next();
            this.logger.info(loggerInfo + " " + temp.getName() + " " + temp.getUrl());
            sortMap.put(temp.getId(), temp.getSort());
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String updatedAt = simpleDateFormat.format(new Date());
        Iterator iterator1 = sortMap.entrySet().iterator();
        // 放在第一个
        if (addWebsiteRequest.getFirst()) {
            while (iterator1.hasNext()) {
                Map.Entry entry = (Map.Entry) iterator1.next();
                Long tempId = (Long) entry.getKey();
                Integer tempSort = (Integer) entry.getValue();
                this.logger.info(loggerInfo + " " + tempId + " " + tempSort);
                this.websiteRepository.modifyById(tempSort + 1, tempId, updatedAt);
            }
        } else {
            // after，没有填写或是 after 不存在都相当与 first = true
            Long after = addWebsiteRequest.getAfter();
            while (iterator1.hasNext()) {
                Map.Entry entry = (Map.Entry) iterator1.next();
                Long tempId = (Long) entry.getKey();
                Integer tempSort = (Integer) entry.getValue();
                this.logger.info(loggerInfo + " id " + tempId + " " + tempSort);
                if (tempId.equals(after)) {
                    // new website sort
                    sort = tempSort + 1;
                    break;
                }
            }
            Iterator iterator2 = sortMap.entrySet().iterator();
            while (iterator2.hasNext()) {
                Map.Entry entry = (Map.Entry) iterator2.next();
                Long tempId = (Long) entry.getKey();
                Integer tempSort = (Integer) entry.getValue();
                this.logger.info(loggerInfo + " id " + tempId + " " + tempSort);
                if (tempSort >= sort) {
                    // 后面 sort + 1
                    this.websiteRepository.modifyById(tempSort + 1, tempId, updatedAt);
                }
            }
        }

        Website website = new Website();
        website.setUrl(addWebsiteRequest.getUrl());
        website.setName(addWebsiteRequest.getName());
        website.setDescription(addWebsiteRequest.getDescription());
        website.setIcon(addWebsiteRequest.getIcon());

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

        // sort
        website.setSort(sort);

        this.websiteRepository.save(website);

        return ResponseEntity.ok(new ApiResponse(0, "OK"));
    }
}
