# API 文档

收藏夹 `API` 文档

## 前言

### 名词解释

- `token` -- 用户访问凭证，有效期是 `7` 天
- `Authorization` -- 用户认证 `HTTP Header` 字段
- `status` -- `HTTP` 状态码
- `code` -- `API` 调用返回码，`int` 类型，只有 `0` 表示成功
- `message` -- `API` 调用错误信息 

### 统一要求

- 数据格式：`JSON`
- 请求头 `Content-Type`: `application/json`

## API 地址

### 生产

> 暂无

### 测试
```
https://api.domain.com:8080
```

`API` 地址拼接示例，如注册，则完成的 `URL` 是：`http://api.domain.com:8080/auth/register`

## 发送邮箱验证码

用户注册需要验证邮箱是真实有效并是自己的，所以在用户注册时，需要给注册的邮箱发送一个验证码，就像是手机验证码一样。

> 由于没有办法发送短信，所以使用邮箱验证。

### 地址
```
/auth/verifyCode
```

### 请求方法
```
POST
```

### 参数列表
| 名称 | 数据类型 | 是否必须 | 说明 |
| :--- | :--- | :---: | :--- |
| `email` | `string` | 是 | 邮箱地址 |

### 请求示例
```json
{
	"email": "username@domain.com"
}
```

### 成功返回
```
{
	"code": 0,
	"message": "OK"
}
```

### 失败返回
```
{
	"code": 1,
	"message": "错误信息"
}
```

## 用户注册

### 地址
```
/auth/register
```

### 请求方法
```
POST
```

### 参数列表
| 名称 | 数据类型 | 是否必须 | 说明 |
| :--- | :--- | :---: | :--- |
| `email` | `string` | 是 | 邮箱地址 |
| `password` | `string` | 是 | 登录密码，`6 - 20` 位，字母数字组成 |
| `verifyCode` | `string` | 是 | 邮箱收到的验证码 |
| `name` | `string` | 否 | 用户名（暂时未用到）|


### 请求示例
```json
{
	"email": "username@domain.com",
	"password": "123456",
	"verifyCode": "789012",
	"name": "username"
}
```

### 成功返回
```
{
	"code": 0,
	"message": "OK",
	"token": "xxx"
}
```

> `token` 即上面提到的 `token`，需要保存起来，用于 `Authorization` 认证

### 失败返回

> 参见附录 3

## 用户登录

### 地址
```
/auth/login
```

### 请求方法
```
POST
```

### 参数列表
| 名称 | 数据类型 | 是否必须 | 说明 |
| :--- | :--- | :---: | :--- |
| `email` | `string` | 是 | 邮箱地址 |
| `password` | `string` | 是 | 登录密码 |


### 请求示例
```json
{
	"email": "username@domain.com",
	"password": "123456"
}
```

### 成功返回
```
{
    "accessToken": "xxx",
    "tokenType": "Bearer"
}
```

> `accessToken` 和 `token` 是同一个意思

**注意：** 以后会更改为

```
{
	"code": 0,
	"message": "OK",
	"token": "xxx"
}
```

> `token` 即上面提到的 `token`，需要保存起来，用于 `Authorization` 认证

### 失败返回

> 参见附录 3

## 添加分类

### 地址
```
/category/add
```

### 请求方法
```
POST
```

### 用户认证

> 参见附录 1

### 参数列表
| 名称 | 数据类型 | 是否必须 | 说明 |
| :--- | :--- | :---: | :--- |
| `name` | `string` | 是 | 分类名称，不能超过 `20` 个字符 |
| `first` | `bool` | 是 | 是否排在第一位 |
| `after` | `int` | 是 | 排在哪一个分类 `id` 后面

> 如果 `first` 为 `true`，则 `after` 会被忽略

### 请求示例 1
```json
{
	"name": "团队组织",
	"first": true,
	"after": 0
}
```

### 请求示例 2
```json
{
	"name": "开发社区",
	"first": false,
	"after": 10
}
```

> `"after": 10` 的 `10` 是指某个分类的 `ID`

### 成功返回

> 参见附录 2

### 失败返回

> 参见附录 3


## 删除分类

### 地址
```
/category/del/{id}
```

> `{id}` 表示参数占位符，调用时要替换为分类 `ID`，如，要删除分类 `ID` 为 `10` 的分类，则地址是：`/category/del/10`

### 请求方法
```
POST
```

### 用户认证

> 参见附录 1

### 成功返回

> 参见附录 2

### 失败返回

> 参加附录 3


## 添加网站

网站是放到分类下的，所以要先添加分类后才可以添加网站

### 地址
```
/website/add
```

### 请求方法
```
POST
```

### 用户认证

> 参见附录 1

### 参数列表
| 名称 | 数据类型 | 是否必须 | 说明 |
| :--- | :--- | :---: | :--- |
| `name` | `string` | 是 | 网站名称，不能超过 `20` 个字符 |
| `url` | `string` | 是 | 网站 `URL` |
| `icon` | `string` | 是 | `icon` 链接 |
| `description` | `string` | 是 | 网站简介 |
| `categoryId` | `int` | 是 | 分类 `ID` | 
| `first` | `bool` | 是 | 是否排在第一位 |
| `after` | `int` | 是 | 排在哪一个网站 `id` 后面

> 如果 `first` 为 `true`，则 `after` 会被忽略

### 请求示例 1
```json
{
	"name": "腾讯 AlloyTeam 团队",
	"url": "http://www.alloyteam.com/",
	"icon": "http://www.alloyteam.com/nav/static/images/alloyteam-favicon.jpg",
	"description": "腾讯Web前端团队，代表作品WebQQ，致力于前端技术的研究",
	"categoryId": 10,
	"first": true,
	"after": 0
}
```

> `"categoryId": 10` 的 `10` 是指某个分类 `ID`

### 请求示例 2
```json
{
	"name": "淘宝前端团队（FED）",
	"url": "http://taobaofed.org/",
	"description": "用技术为体验提供无限可能",
	"categoryId": 10,
	"first": false,
	"after": 12
}
```

> `"categoryId": 10` 的 `10` 是指某个分类 `ID`，`"after": 12` 的 `12` 是指某个网站的 `ID`

### 成功返回

> 参见附录 2

### 失败返回

> 参见附录 3


## 删除网站

### 地址
```
/website/del/{id}
```

> `{id}` 表示参数占位符，调用时要替换为网站 `ID`，如，要删除网站 `ID` 为 `10` 的网站，则地址是：`/website/del/10`

### 请求方法
```
POST
```

### 用户认证

> 参见附录 1

### 成功返回

> 参见附录 2

### 失败返回

> 参加附录 3


## 获取用户收藏

### 地址
```
/user/
```

> 以后修正为：`/user` 

### 请求方法
```
GET
```

### 用户认证

> 参加附录 1


### 成功返回
```json
{
    "code": 0,
    "categories": [
        {
            "id": 3,
            "name": "前端设计",
            "websites": [
                {
                    "id": 4,
                    "url": "https://www.baidu.com",
		    "icon": "https://gss1.bdstatic.com/5eN1dDebRNRTm2_p8IuM_a/res/img/baiduzhongban-4.gif?1524924515",
                    "description": "百度一下，你就知道",
                    "createdAt": "2018-04-22 15:45:32",
                    "updatedAt": "2018-04-22 15:45:32",
                    "sort": 0,
                    "name": "百度"
                }
            ],
            "createdAt": "2018-04-22 15:37:58",
            "updatedAt": "2018-04-22 15:37:58",
            "sort": 0
        }
    ],
    "message": "OK",
    "user": {
        "id": 2,
        "email": "username@domain.com",
        "name": null,
        "createdAt": "2018-04-22 15:36:00",
        "updatedAt": "2018-04-22 15:36:00"
    }
}
```

### 返回说明

- `categories` 是该用户下所有分类
- `websites` 是该分类下所有的网站
- `user` 是用户信息
- `sort` 是排序字段，小的排在前面


### 失败返回

> 参加附录 3


## 附录 1：用户认证

请求 `Header` 要添加 `Authorization`，值为 `Bearer + token`，`token` 是上面所指的用户 `token`，在用户注册和登录时返回。

用户未认证或是认证失败，`HTTP status` 统一返回 `401`


## 附录 2：成功返回

未包含数据的 `API` 除特别说明外，成功返回数据如下：

```json
{
	"code": 0,
	"message": "OK"
}
```


## 附录 3：失败返回

`API` 调用失败统一返回

```json
{
	"code": 1,
	"message": "<错误信息>"
}
```

> `code` 是非 `0` 值，不一定是 `1`

## 附录 4：错误码列表

错误码列表

| code | message |
| :--- | :--- |
| `0` | `OK` |

> 未列举完

## 附录 5：Jquery Ajax 示例

```javascript
var settings = {
    "async": true,
    "crossDomain": true,
    "url": "http://api.domain.com:8080/auth/login",
    "method": "POST",
    "headers": {
        "content-type": "application/json"
    },
    "processData": false,
    "data": JSON.stringify({
        "email": "username@domain.com",
        "password": "123456"
    })
};

$.ajax(settings).done(function(response) {
    console.log(response);
}).fail(function(error) {
    console.log(error);
});
```
