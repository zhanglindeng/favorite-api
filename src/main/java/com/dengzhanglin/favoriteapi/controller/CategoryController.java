package com.dengzhanglin.favoriteapi.controller;

import com.dengzhanglin.favoriteapi.domain.Category;
import com.dengzhanglin.favoriteapi.domain.User;
import com.dengzhanglin.favoriteapi.payload.AddCategoryRequest;
import com.dengzhanglin.favoriteapi.payload.ApiResponse;
import com.dengzhanglin.favoriteapi.repository.CategoryRepository;
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
@RequestMapping(value = "/category")
public class CategoryController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private CategoryRepository categoryRepository;

    @Autowired
    public CategoryController(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @PostMapping(value = "/del/{id}")
    public ResponseEntity<?> del(@PathVariable("id") Long id, @CurrentUser UserPrincipal userPrincipal) {
        String loggerInfo = "[category:del]";

        // user_id
        Long userId = userPrincipal.getId();
        this.logger.info(loggerInfo + " " + userId + " " + id);

        Optional<Category> row = this.categoryRepository.findById(id);
        if (!row.isPresent()) {
            this.logger.info(loggerInfo + " category");
            return ResponseEntity.ok(new ApiResponse(1, "分类没有找到"));
        }
        Category category = row.get();
        if (category.getUser() == null) {
            this.logger.info(loggerInfo + " user");
            return ResponseEntity.ok(new ApiResponse(1, "分类没有找到"));
        }
        if (!category.getUser().getId().equals(userId)) {
            this.logger.info(loggerInfo + " user_id");
            return ResponseEntity.ok(new ApiResponse(1, "分类没有找到"));
        }

        // TODO 怎么知道是否删除成功
        this.categoryRepository.delete(category);
        this.logger.info(loggerInfo + " deleted " + userId + " " + id);

        return ResponseEntity.ok(new ApiResponse(0, "OK"));
    }

    @PostMapping(value = "/add")
    public ResponseEntity<?> add(@CurrentUser UserPrincipal userPrincipal,
                                 @Valid @RequestBody AddCategoryRequest addCategoryRequest) {

        String loggerInfo = "[category:add]";

        // user_id
        Long userId = userPrincipal.getId();

        // sort
        Integer sort = 0;

        List<Category> categories = this.categoryRepository.findByUserOrderBySort(userId);

        Iterator<Category> iterator = categories.iterator();

        Category temp;
        Map<Long, Integer> sorts = new HashMap<>();
        while (iterator.hasNext()) {
            temp = iterator.next();
            logger.info(loggerInfo + temp.getName());
            sorts.put(temp.getId(), temp.getSort());
        }

        Iterator iterator1 = sorts.entrySet().iterator();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String updatedAt = simpleDateFormat.format(new Date());
        // 放在第一个
        if (addCategoryRequest.getFirst()) {
            while (iterator1.hasNext()) {
                Map.Entry entry = (Map.Entry) iterator1.next();
                Long tempId = (Long) entry.getKey();
                Integer tempSort = (Integer) entry.getValue();
                this.logger.info(loggerInfo + " " + tempId + " " + (this.categoryRepository.modifyById(tempSort + 1, tempId, updatedAt)));
            }
        } else {
            // after
            Long after = addCategoryRequest.getAfter();
            while (iterator1.hasNext()) {
                Map.Entry entry = (Map.Entry) iterator1.next();
                Long tempId = (Long) entry.getKey();
                Integer tempSort = (Integer) entry.getValue();
                this.logger.info(loggerInfo + " " + tempId + " " + tempSort);
                if (tempId.equals(after)) {
                    // new category sort
                    sort = tempSort + 1;
                    break;
                }
            }
            Iterator iterator2 = sorts.entrySet().iterator();
            while (iterator2.hasNext()) {
                Map.Entry entry = (Map.Entry) iterator2.next();
                Long tempId = (Long) entry.getKey();
                Integer tempSort = (Integer) entry.getValue();
                this.logger.info(loggerInfo + " " + tempId + " " + tempSort);
                if (tempSort >= sort) {
                    // 后面的加 sort + 1
                    this.logger.info(loggerInfo + " " + tempId + " " + (this.categoryRepository.modifyById(tempSort + 1, tempId, updatedAt)));
                }
            }
        }

        // user
        User user = new User();
        user.setId(userId);

        String name = addCategoryRequest.getName();
        Category category = new Category();
        category.setName(name);
        category.setUser(user);
        category.setSort(sort);
        Instant instant = Instant.now();
        category.setCreatedAt(instant);
        category.setUpdatedAt(instant);
        this.categoryRepository.save(category);

        return ResponseEntity.ok(new ApiResponse(0, "OK"));
    }
}
