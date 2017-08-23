package com.kmacho.juan.nurceapp.entities;

import java.util.List;
import java.util.Map;

/**
 * Created by Videos on 22/07/2017.
 */

public class ApiError {
    String message;
    Map<String, List<String>> errors;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map<String, List<String>> getErrors() {
        return errors;
    }

    public void setErrors(Map<String, List<String>> errors) {
        this.errors = errors;
    }
}
