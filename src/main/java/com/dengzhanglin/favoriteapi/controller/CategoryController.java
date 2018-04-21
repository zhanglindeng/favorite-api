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
import java.time.Instant;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

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
            return ResponseEntity.ok(new ApiResponse(false, "分类没有找到"));
        }
        Category category = row.get();
        if (category.getUser() == null) {
            this.logger.info(loggerInfo + " user");
            return ResponseEntity.ok(new ApiResponse(false, "分类没有找到"));
        }
        if (!category.getUser().getId().equals(userId)) {
            this.logger.info(loggerInfo + " user_id");
            return ResponseEntity.ok(new ApiResponse(false, "分类没有找到"));
        }

        // TODO 怎么知道是否删除成功
        this.categoryRepository.delete(category);
        this.logger.info(loggerInfo + " deleted " + userId + " " + id);

        return ResponseEntity.ok(new ApiResponse(true, "OK"));
    }

    @PostMapping(value = "/add")
    public ResponseEntity<?> add(@CurrentUser UserPrincipal userPrincipal,
                                 @Valid @RequestBody AddCategoryRequest addCategoryRequest) {

        String loggerInfo = "[category:add]";

        // user_id
        Long userId = userPrincipal.getId();

        List<Category> categories = this.categoryRepository.findByUserOrderByOrder(userId);

        Iterator<Category> iterator = categories.iterator();

        Category temp;
        while (iterator.hasNext()) {
            temp = iterator.next();
            logger.info(loggerInfo + temp.getName());
        }

        // user
        User user = new User();
        user.setId(userId);

        String name = addCategoryRequest.getName();
        Category category = new Category();
        category.setName(name);
        category.setUser(user);
        // TODO sort
        Instant instant = Instant.now();
        category.setCreatedAt(instant);
        category.setUpdatedAt(instant);
        this.categoryRepository.save(category);

        return ResponseEntity.ok(new ApiResponse(true, "OK"));
    }
}
