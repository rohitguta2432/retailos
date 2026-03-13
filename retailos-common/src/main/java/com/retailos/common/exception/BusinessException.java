package com.retailos.common.exception;

import lombok.Getter;

/**
 * Base business exception with error code.
 * All module-specific business exceptions should extend this.
 */
@Getter
public class BusinessException extends RuntimeException {

    private final String code;
    private final int httpStatus;

    public BusinessException(String code, String message) {
        super(message);
        this.code = code;
        this.httpStatus = 400;
    }

    public BusinessException(String code, String message, int httpStatus) {
        super(message);
        this.code = code;
        this.httpStatus = httpStatus;
    }

    public BusinessException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.httpStatus = 500;
    }
}
