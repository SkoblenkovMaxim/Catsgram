package ru.yandex.practicum.catsgram.exception;

public class ParameterNotValidException extends IllegalArgumentException {
    private String parameter;
    private String reason;

    public ParameterNotValidException(String parameter, String reason) {
        this.parameter = parameter;
        this.reason = reason;
    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
