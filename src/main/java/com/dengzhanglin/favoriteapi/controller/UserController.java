package com.dengzhanglin.favoriteapi.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dengzhanglin.favoriteapi.domain.Category;
import com.dengzhanglin.favoriteapi.repository.CategoryRepository;
import com.dengzhanglin.favoriteapi.repository.UserRepository;
import com.dengzhanglin.favoriteapi.security.CurrentUser;
import com.dengzhanglin.favoriteapi.security.UserPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Iterator;
import java.util.List;

@RestController
@RequestMapping(value = "/user")
public class UserController extends BaseController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private CategoryRepository categoryRepository;
    private UserRepository userRepository;

    @Autowired
    public UserController(CategoryRepository categoryRepository, UserRepository userRepository) {
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    @GetMapping(value = "/")
    public JSONObject index(@CurrentUser UserPrincipal userPrincipal) {

        String loggerInfo = "[user:index]";

        this.logger.info(loggerInfo + " " + userPrincipal.getId());

        List<Category> categories = this.categoryRepository.findByUserOrderBySort(userPrincipal.getId());
        Iterator<Category> iterator = categories.iterator();

        JSONArray jsonArray = new JSONArray();

        Category temp;
        while (iterator.hasNext()) {
            temp = iterator.next();
            jsonArray.add(temp);
        }

        return new JSONObject() {{
            put("code", 0);
            put("message", "OK");
            put("categories", jsonArray);
            put("user", userRepository.findById(userPrincipal.getId()));
        }};
    }
}
