package com.dengzhanglin.favoriteapi.controller;

import com.alibaba.fastjson.JSONObject;

public class BaseController {
    protected JSONObject success() {
        return new JSONObject() {{
            put("status", 0);
            put("message", "OK");
        }};
    }
}
