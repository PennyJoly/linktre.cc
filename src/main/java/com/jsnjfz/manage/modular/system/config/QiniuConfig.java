package com.jsnjfz.manage.modular.system.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
@ConfigurationProperties(prefix = "qiniu")
public class QiniuConfig {

    @Value("${qiniu.accessKey}")
    private String accessKey;

    @Value("${qiniu.secretKey}")
    private String secretKey;

    @Value("${qiniu.bucketName}")
    private String bucketName;

    @Value("${qiniu.url}")
    private String url;

    @Value("${qiniu.qiniuHostUrl}")
    private String qiniuHostUrl;

    @Value("${qiniu.qiniuCheckImgUrl}")
    private String qiniuCheckImgUrl;
}
