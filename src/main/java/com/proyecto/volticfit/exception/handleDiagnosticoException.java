package com.proyecto.volticfit.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import java.util.Map;
public class handleDiagnosticoException {

@ExceptionHandler(DiagnosticoException.class)
@ResponseStatus(HttpStatus.BAD_REQUEST)
public Map<String, String> handleDiagnosticoException(
        DiagnosticoException ex
) {

    return Map.of(
            "error",
            ex.getMessage()
    );
}

}