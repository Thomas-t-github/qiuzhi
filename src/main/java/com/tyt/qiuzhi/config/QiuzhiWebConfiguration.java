package com.tyt.qiuzhi.config;

import com.tyt.qiuzhi.interceptor.PassportInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

@Component
public class QiuzhiWebConfiguration extends WebMvcConfigurationSupport {

    @Autowired
    PassportInterceptor passportInterceptor;


    @Override
    protected void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(passportInterceptor);
        super.addInterceptors(registry);
    }

    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
        super.addResourceHandlers(registry);
    }
}
