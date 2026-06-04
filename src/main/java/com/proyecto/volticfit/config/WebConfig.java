package com.proyecto.volticfit.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.proyecto.volticfit.security.RoleInterceptor;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
public class WebConfig implements WebMvcConfigurer{
    
    private final RoleInterceptor roleInterceptor;
    private final ActivityLogInterceptor activityLogInterceptor; 
 
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(roleInterceptor);
        registry.addInterceptor(activityLogInterceptor).addPathPatterns("/api/**"); 
    }

}
