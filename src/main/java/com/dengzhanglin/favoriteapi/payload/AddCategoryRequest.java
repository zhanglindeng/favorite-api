package com.dengzhanglin.favoriteapi.payload;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class AddCategoryRequest {
    @NotBlank
    @Size(max = 20)
    private String name;

    // 是否是第一个
    private Boolean first = false;

    // 放在哪一个 category_id 之后
    private Long after;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getFirst() {
        return first;
    }

    public void setFirst(Boolean first) {
        this.first = first;
    }

    public Long getAfter() {
        return after;
    }

    public void setAfter(Long after) {
        this.after = after;
    }
}
