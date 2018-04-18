# Favorite API

## TODO
- ~~参考 [Github](https://github.com/callicoder/spring-security-react-ant-design-polls-app) 完成 `JWT` 认证~~
- `SES` 发送邮件，参考：[AWS Docs](https://docs.aws.amazon.com/ses/latest/DeveloperGuide/examples-send-using-sdk.html)

```
// 注释掉
// static final String CONFIGSET = "ConfigSet";

// 填写实际的 key 和 secret
BasicAWSCredentials basicAWSCredentials = new BasicAWSCredentials("key", "secret");
AWSStaticCredentialsProvider provider = new AWSStaticCredentialsProvider(basicAWSCredentials);
AmazonSimpleEmailService client = AmazonSimpleEmailServiceClientBuilder.standard()
        // Replace US_WEST_2 with the AWS Region you're using for
        // Amazon SES.
        .withRegion(Regions.US_WEST_2)
        .withCredentials(provider)
        .build();
```

