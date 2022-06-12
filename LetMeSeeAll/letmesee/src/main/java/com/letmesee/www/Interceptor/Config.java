package com.letmesee.www.Interceptor;

import com.letmesee.www.SpringUtils;
import com.letmesee.www.util.SomeUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class Config implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor((HandlerInterceptor) SpringUtils.getBean("visit")).addPathPatterns("/**");
        registry.addInterceptor((HandlerInterceptor) SpringUtils.getBean("login")).addPathPatterns("/loginJudge/**").excludePathPatterns("/loginJudge/login.do").excludePathPatterns("/loginJudge/register.go");
    }

}
