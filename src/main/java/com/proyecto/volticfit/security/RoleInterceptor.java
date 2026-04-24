package com.proyecto.volticfit.security;

import java.util.Arrays;

import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RoleInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
            Object handler) throws Exception {

        // Verifica que el handler sea un método del controller
        if (!(handler instanceof HandlerMethod method)) {
            return true; // Si no es un endpoint no nos importa
        }

        // Busca la anotación @RequiresRole en el método
        RequiresRole annotation = method.getMethodAnnotation(RequiresRole.class);

        // Si no está en el método, la busca en la clase (controller)
        if (annotation == null) {
            annotation = method.getBeanType().getAnnotation(RequiresRole.class);
        }

        // Si no hay anotación, el endpoint es público
        if (annotation == null) {
            return true;
        }

        Object role = request.getAttribute("role");

        if (!(role instanceof String userRole)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"User not authenticated\"}");
            return false;
        }

        // Verifica si el rol del usuario está dentro de los roles permitidos
        boolean hasRole = Arrays.stream(annotation.value())
                .anyMatch(r -> r.getValue().equalsIgnoreCase(userRole));

        if (!hasRole) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"You do not have permission to perform this action\"}");
            return false;
        }

        return true;
    }
}
