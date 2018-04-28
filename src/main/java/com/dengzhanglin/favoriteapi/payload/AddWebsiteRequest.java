package com.dengzhanglin.favoriteapi.payload;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class AddWebsiteRequest {
    @NotBlank
    @Size(max = 20)
    private String name;
    @NotBlank
    @Size(max = 255)
    private String url;
    @NotBlank
    @Size(max = 255)
    private String description;
    // 是否是第一个
    private Boolean first = false;

    // 放在哪一个 website_id 之后
    private Long after;

    @NotNull
    private Long categoryId;

    @NotBlank
    @Size(max = 255)
    private String icon;

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    @Override
    public String toString() {
        return "AddWebsiteRequest{" +
                "name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", description='" + description + '\'' +
                ", first=" + first +
                ", after=" + after +
                ", categoryId=" + categoryId +
                '}';
    }
}
