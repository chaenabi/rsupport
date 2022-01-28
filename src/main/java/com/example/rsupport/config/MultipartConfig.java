package com.example.rsupport.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MultipartConfig implements WebMvcConfigurer {

    private final long MAX_UPLOAD_FILE_SIZE = 10 * 1024 * 1024;

    @Bean
    public CommonsMultipartResolver multipartResolver() {
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
        multipartResolver.setDefaultEncoding("UTF-8");
        multipartResolver.setMaxUploadSizePerFile(MAX_UPLOAD_FILE_SIZE);
        return multipartResolver;
    }
}
