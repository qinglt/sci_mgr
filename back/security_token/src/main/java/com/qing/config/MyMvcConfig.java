package com.qing.config;

import com.qing.config.response.NotResultWrap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author qinghua
 * @date 2021/5/11 22:01
 */
@Configuration
public class MyMvcConfig implements WebMvcConfigurer {

    @Value("${file.staticAccessPath}")
    private String staticAccessPath;

    @Value("${file.uploadFolder}")
    private String uploadFolder;

    // 文件下载路径
    @NotResultWrap
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler( staticAccessPath + "/**")
                .addResourceLocations("file:" + uploadFolder);
    }

}
