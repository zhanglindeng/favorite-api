# Favorite API

## dev
复制 `application-prod.properties` 命名为 `application-dev.properties`，并填写相关配置。

mvn package
```
mvn -Dmaven.test.skip -U clean package
```
> -U 是更新 jar

运行：
```
java -jar xxx.jar --spring.profiles.active=dev
```

后台运行：
```
nohup java -jar xxx.jar --spring.profiles.active=prod > /dev/null &
```

`nginx` 配置
```
location / {
        proxy_pass http://127.0.0.1:8080;
        proxy_redirect off;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
}
```

编译 `prod`，把 `application.properties` 中 `spring.profiles.active` 的值 `dev` 更改 `prod`
  

