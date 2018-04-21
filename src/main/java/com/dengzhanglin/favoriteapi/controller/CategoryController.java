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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.Instant;
import java.util.Iterator;
import java.util.List;

@RestController
@RequestMapping(value = "/category")
public class CategoryController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private CategoryRepository categoryRepository;

    @Autowired
    public CategoryController(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
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
