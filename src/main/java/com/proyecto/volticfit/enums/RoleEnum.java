package com.proyecto.volticfit.enums;

public enum RoleEnum {
    ADMIN("admin"),
    APRENDIZ("aprendiz"),
    FUNCIONARIO("funcionario");
 
    private final String value;
 
    RoleEnum(String value) {
        this.value = value;
    }
 
    public String getValue() {
        return value;
    }
}
