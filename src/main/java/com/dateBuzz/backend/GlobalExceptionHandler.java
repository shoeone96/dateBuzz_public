package com.dateBuzz.backend;

import com.dateBuzz.backend.controller.responseDto.ErrorResponse;
import com.dateBuzz.backend.exception.DateBuzzException;
import com.dateBuzz.backend.exception.RabbitMQException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {


    private final RabbitTemplate rabbitTemplate;
    private static final String EXCHANGE_NAME = "error_exchange";
    private static final String ROUTING_KEY = "error_key";

    @ExceptionHandler(DateBuzzException.class)
    @ResponseBody
    public ErrorResponse<HttpStatus> handleMyException(DateBuzzException ex) {
        String errorMessage = ex.getErrorCode().getMessage();
        HttpStatus status = ex.getErrorCode().getStatus();
        return ErrorResponse.error(errorMessage, status);
    }

    @ExceptionHandler(RabbitMQException.class)
    @ResponseBody
    public ErrorResponse<HttpStatus> handleMyException(RabbitMQException ex) {
        String errorMessage = ex.getErrorCode().getMessage();
        HttpStatus status = ex.getErrorCode().getStatus();
        log.info("errorMessage: {}, status: {}", errorMessage, status);
        return ErrorResponse.error(errorMessage, status);
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ErrorResponse<HttpStatus> handleMyException(Exception ex) {
        // Send error message to RabbitMQ
        rabbitTemplate.convertSendAndReceive(EXCHANGE_NAME, ROUTING_KEY+"_with_receive", ex);
        rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY, ex);
        return ErrorResponse.error(ex.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
