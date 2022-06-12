package com.letmesee.www.Interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class Visit implements HandlerInterceptor {

    @Autowired
    private CurrentLimiting currentLimiting;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(currentLimiting.getToken()){
            return true;
        }else{
            return false;
        }
    }
}
