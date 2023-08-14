package com.dateBuzz.backend.service;

import com.dateBuzz.backend.exception.ErrorCode;
import com.dateBuzz.backend.exception.RabbitMQException;
import com.dateBuzz.backend.model.AccessLog;
import com.dateBuzz.backend.repository.AccessLogRepository;
import com.dateBuzz.backend.util.TelegramNotifier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ErrorListenerService {
    private final AccessLogRepository logRepository;

    private final TelegramNotifier telegramNotifier;
    private final AsyncService asyncService;

    @RabbitListener(queues = "error_queue")
    public void receiveError1(Exception error) {
        log.info(error.getMessage() + "1");
//        telegramNotifier.sendErrorMessage("Consumer1" + error.getMessage());
    }

    @RabbitListener(queues = "error_queue")
    public void receiveError2(Exception error) {
        log.info(error.getMessage() + "2");
//        telegramNotifier.sendErrorMessage("Consumer2" + error.getMessage());
    }

/**
 * 1. @RabbitLisner의 concurrency 옵션을 이용한 멀티스레드 이용 방법
 */

//    @RabbitListener(queues = "error_queue_receive", concurrency = "10-10")
//    public String receiveError3(Exception error) {
//        log.info(error.getMessage() + "1");
//        telegramNotifier.sendErrorMessage("Consumer with receive1" + error.getMessage());
//        return "test";
//    }

    /**
     * 2. @Async를 이용한 multithread 이용 방법
     */
    //
    @RabbitListener(queues = "error_queue_receive")
    public String receiveError3(Exception error){
        try {
            validateMessage(error);
            asyncService.loggingEvent(error);
//        telegramNotifier.sendErrorMessage("Consumer with receive1" + error.getMessage());
            return error.getMessage();
        } catch (RabbitMQException e){
            log.info(e.getErrorCode().getMessage());
            return "RabbitMQ Exception occurred";
        }
    }

    @RabbitListener(queues = "log_queue")
    public void receiveLog(AccessLog accessLog){
        logRepository.save(accessLog);
    }

    private void validateMessage(Exception error) throws RabbitMQException {
        if (error.getMessage().equals("test_receive_error") ) throw new RabbitMQException(ErrorCode.RABBITMQ_ERROR);
    }


}