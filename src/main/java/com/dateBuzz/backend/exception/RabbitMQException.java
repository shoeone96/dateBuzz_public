package com.dateBuzz.backend.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RabbitMQException extends RuntimeException{

    private ErrorCode errorCode;
    private String message;

    public RabbitMQException(ErrorCode errorCode){
        this.errorCode = errorCode;
        this.message = null;
    }

    @Override
    public String getMessage(){
        if(message == null){
            return errorCode.getMessage();
        }
        return String.format("%s, %s", errorCode.getMessage(), message);
    }

}

