package com.rocket.rocket.utils;

public class CustomResponse<T> {

    private T data;
    private int code;
    private String message;
    private Boolean error;

    // Constructor vacío
    public CustomResponse() {
    }

    // Constructor con parámetros
    public CustomResponse(T data, int code, String message, Boolean error) {
        this.data = data;
        this.code = code;
        this.message = message;
        this.error = error;
    }

    // Constructor para respuestas sin datos (por ejemplo, en errores)
    public CustomResponse(int code, String message, Boolean error) {
        this.data = null;
        this.code = code;
        this.message = message;
        this.error = error;
    }

    // Getters y Setters
    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return "CustomResponse{" +
                "data=" + data +
                ", code=" + code +
                ", message='" + message + '\'' +
                ", error=" + error +
                '}';
    }
}