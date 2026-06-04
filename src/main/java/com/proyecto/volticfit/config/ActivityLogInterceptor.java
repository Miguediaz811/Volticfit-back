package com.proyecto.volticfit.config;

import com.proyecto.volticfit.service.AuditService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class ActivityLogInterceptor implements HandlerInterceptor {

    private final AuditService auditService;

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        String uri = request.getRequestURI();
        String method = request.getMethod();
        
        // Obtenemos el userId (Asegúrate de que tu JwtInterceptor lo guarda como Long y con este nombre)
        Long userId = (Long) request.getAttribute("userId");
        
        if (userId != null) {
            auditService.saveLogAsync(userId, method, uri, response.getStatus());
        }
    }
}