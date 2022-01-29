package com.example.rsupport.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 파일 업로드 설정 클래스
 *
 * @author MC Lee
 * @created 2022-01-29
 * @since 2.6.3 spring boot
 * @since 0.0.1 dev
 */
@Configuration
public class MultipartConfig implements WebMvcConfigurer {
    
    @Bean
    public StandardServletMultipartResolver multipartResolver() {
        return new StandardServletMultipartResolver();
    }
}
