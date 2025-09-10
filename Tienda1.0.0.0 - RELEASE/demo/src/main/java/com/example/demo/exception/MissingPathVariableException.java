package com.example.demo.exception;


public class MissingPathVariableException extends RuntimeException {

    private final String variableName;
    private final String resourceName;

    public MissingPathVariableException(String variableName, String resourceName) {
        super(String.format("El %s '%s' es requerido en la URL.", resourceName, variableName));
        this.variableName = variableName;
        this.resourceName = resourceName;
    }

    public String getVariableName() {
        return variableName;
    }

    public String getResourceName() {
        return resourceName;
    }
}