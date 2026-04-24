package com.proyecto.volticfit.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.proyecto.volticfit.enums.RoleEnum;


@Target(ElementType.METHOD)

@Retention(RetentionPolicy.RUNTIME)

public @interface RequiresRole {
    RoleEnum[] value();
}
