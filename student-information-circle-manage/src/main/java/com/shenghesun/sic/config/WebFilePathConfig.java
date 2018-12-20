package com.shenghesun.sic.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class WebFilePathConfig extends WebMvcConfigurerAdapter {
	
	@Value("${file.path.upload}")
	private String uploadFilePath;
	@Value("${file.path.show}")
	private String showFilePath;
	

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
    	System.out.println("静态文件映射--------------------" + showFilePath + uploadFilePath);
        registry.addResourceHandler(showFilePath + "**").addResourceLocations("file:" + uploadFilePath);
        super.addResourceHandlers(registry);
    }
}