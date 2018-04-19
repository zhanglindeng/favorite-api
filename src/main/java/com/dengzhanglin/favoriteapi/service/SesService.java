package com.dengzhanglin.favoriteapi.service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SesService {

    @Value("${aws.ses.accessKey}")
    private String accessKey;

    @Value("${aws.ses.accessSecret}")
    private String accessSecret;

    @Value("${aws.ses.region}")
    private String region;

    @Value("${aws.ses.from}")
    private String from;

    @Value("${aws.ses.domain}")
    private String domain;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public Boolean send(String to, String subject, String content) {
        logger.info("[SES]" + to + " " + subject + " " + content);
        try {
            BasicAWSCredentials basicAWSCredentials = new BasicAWSCredentials(this.accessKey, this.accessSecret);
            AWSStaticCredentialsProvider provider = new AWSStaticCredentialsProvider(basicAWSCredentials);
            AmazonSimpleEmailService client = AmazonSimpleEmailServiceClientBuilder.standard()
                    .withRegion(this.region).withCredentials(provider).build();
            SendEmailRequest request = new SendEmailRequest().withDestination(new Destination().withToAddresses(to))
                    .withMessage(new Message()
                            .withBody(new Body().withHtml(new Content().withCharset("UTF-8").withData(content)))
                            .withSubject(new Content().withCharset("UTF-8").withData(subject)))
                    .withSource(this.from);
            SendEmailResult result = client.sendEmail(request);
            // TODO save email message
            logger.info("[SES]" + result.getMessageId());
            return true;
        } catch (Exception ex) {
            System.out.println("The email was not sent. Error message: " + ex.getMessage());
        }
        return false;
    }
}
