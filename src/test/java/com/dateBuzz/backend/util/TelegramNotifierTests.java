//package com.dateBuzz.backend.util;
//
//import com.dateBuzz.backend.exception.DateBuzzException;
//import com.dateBuzz.backend.exception.ErrorCode;
//import com.dateBuzz.backend.exception.RabbitMQException;
//import com.dateBuzz.backend.service.RabbitDto;
//import org.junit.jupiter.api.Test;
//import org.springframework.amqp.rabbit.core.RabbitTemplate;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//@SpringBootTest
//
//public class TelegramNotifierTests {
//    @Autowired
//    RabbitTemplate rabbitTemplate;
//
//    private static final String EXCHANGE_NAME = "error_exchange";
//    private static final String ROUTING_KEY = "error_key";
//
//    @Autowired
//    TelegramNotifier telegramNotifier;
//
//    @Test
//    public void test() throws Exception{
//        for(int i = 0; i < 30000; i ++){
////            rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY, "test");
//            String hi = (String) rabbitTemplate.convertSendAndReceive(EXCHANGE_NAME, ROUTING_KEY + "_with_receive", "test_receive");
//        }
//    }
//
//    @Test
//    public void consumer_예외_test(){
//        for(int i = 0; i < 30; i ++){
//            String hi = (String) rabbitTemplate.convertSendAndReceive(EXCHANGE_NAME, ROUTING_KEY + "_with_receive", new Exception("예외 발생, 정상적으로 예외를 받아 처리"));
//            String hi2 = (String) rabbitTemplate.convertSendAndReceive(EXCHANGE_NAME, ROUTING_KEY + "_with_receive", new Exception("test_receive_error"));
//        }
//    }
//}
