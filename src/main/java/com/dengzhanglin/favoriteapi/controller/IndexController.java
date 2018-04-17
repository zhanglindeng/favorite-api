package com.dengzhanglin.favoriteapi.controller;

import com.dengzhanglin.favoriteapi.security.CurrentUser;
import com.dengzhanglin.favoriteapi.security.UserPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IndexController {
    @GetMapping(value = "/")
    public Object index(@CurrentUser UserPrincipal userPrincipal) {
        return userPrincipal;
    }
}
