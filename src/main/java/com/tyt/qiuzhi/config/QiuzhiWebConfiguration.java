package com.tyt.qiuzhi.config;

import com.tyt.qiuzhi.interceptor.LoginRequiredInterceptor;
import com.tyt.qiuzhi.interceptor.ManagePassportInterceptor;
import com.tyt.qiuzhi.interceptor.PassportInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.nio.charset.Charset;
import java.util.List;

@Component
public class QiuzhiWebConfiguration extends WebMvcConfigurationSupport {

    @Autowired
    PassportInterceptor passportInterceptor;

    @Autowired
    LoginRequiredInterceptor loginRequiredInterceptor;

    @Autowired
    ManagePassportInterceptor managePassportInterceptor;

    @Override
    protected void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(passportInterceptor);
        registry.addInterceptor(managePassportInterceptor).addPathPatterns("/manage/index","/manage/toNewQuestionPage","/manage/questionPage");
        registry.addInterceptor(loginRequiredInterceptor).addPathPatterns("/jie/toAdd");
        super.addInterceptors(registry);
    }

    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
        super.addResourceHandlers(registry);
    }


}
