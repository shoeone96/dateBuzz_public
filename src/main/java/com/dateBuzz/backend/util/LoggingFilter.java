package com.dateBuzz.backend.util;

import com.dateBuzz.backend.model.AccessLog;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
@Order(1)
@RequiredArgsConstructor
public class LoggingFilter implements Filter {

    private final RabbitTemplate rabbitTemplate;


    private static final String EXCHANGE_NAME = "log_exchange";
    private static final String ROUTING_KEY = "log_key";


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        String userAgent = req.getHeader("User-Agent");

        long startTime = System.currentTimeMillis();
        // 호출 전
        {
            try {
                ContentCachingRequestWrapper cachingRequest = new ContentCachingRequestWrapper(req);
                ContentCachingResponseWrapper cachingResponse = new ContentCachingResponseWrapper(res);

                chain.doFilter(cachingRequest, cachingResponse);

                long endTime = System.currentTimeMillis();

                cachingResponse.copyBodyToResponse();
                Long userId = LoginInfo.getUserId();
                /**
                 * TODO: swagger uri 효과적으로 필터링하는 방법 확인 필요
                 */
                if (!req.getRequestURI().equals("/v3/api-docs") &&
                        !req.getRequestURI().equals("/swagger-ui/swagger-ui.css") &&
                        !req.getRequestURI().equals("/swagger-ui/swagger-ui-standalone-preset.js") &&
                        !req.getRequestURI().equals("/swagger-ui/swagger-ui-bundle.js") &&
                        !req.getRequestURI().equals("/swagger-ui/swagger-initializer.js") &&
                        !req.getRequestURI().equals("/swagger-ui/index.css") &&
                        !req.getRequestURI().equals("/swagger-ui/index.html") &&
                        !req.getRequestURI().equals("/v3/api-docs/swagger-config") &&
                        !req.getRequestURI().equals("/swagger-ui/favicon-32x32.png") &&
                        !req.getRequestURI().equals("/swagger.html")
                ) {
                    String storedRequest = new String(cachingRequest.getContentAsByteArray(), StandardCharsets.UTF_8);
                    String storedResponse = new String(cachingResponse.getContentAsByteArray(), StandardCharsets.UTF_8);
                    storeLog(req, storedRequest, storedResponse, startTime, endTime, userId, userAgent);
                }
            } finally {
                LoginInfo.clear();
            }
        }

        // async로 빠지게
//        rabbitTemplate.convertSendAndReceive(EXCHANGE_NAME, ROUTING_KEY, accessLog);
    }

    @Override
    public void destroy() {
    }

    @Async("logging_thread")
    void storeLog(HttpServletRequest req, String storedRequest, String storedResponse, long startTime, long endTime, Long userId, String userAgent) {
        AccessLog accessLog;

        // 로그인 한 정보에 대해서는 userId 값을 같이 저장(최대한 변할 일이 없는 primary key로 선택)
        // 로그인 하지 않은 정보는 -1 값을 넣어 로그인 하지 않은 정보를 저장
        if (userId != null) accessLog = AccessLog.logging(req.getRequestURI(), req.getMethod(), endTime - startTime, storedRequest, storedResponse, userId, userAgent);
        else accessLog = AccessLog.logging(req.getRequestURI(), req.getMethod(), endTime - startTime, storedRequest, storedResponse, -1L, userAgent);
        rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY, accessLog);
    }

}
