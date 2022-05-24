package com.letmesee.www.Interceptor;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class Config implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new Login()).addPathPatterns("/loginJudge/**").excludePathPatterns("/loginJudge/login.do").excludePathPatterns("/loginJudge/register.go");
    }
}
