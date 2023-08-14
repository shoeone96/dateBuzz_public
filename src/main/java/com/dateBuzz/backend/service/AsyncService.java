package com.dateBuzz.backend.service;

import com.dateBuzz.backend.cachingService.CachingRecordService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@CacheConfig(cacheNames = "listResponse")
public class AsyncService {

    private final CachingRecordService cachingRecordService;

    @Async("logging_thread")
    public void loggingEvent(Exception message) {
        log.info(message.getMessage());
    }

    @Async
    @Scheduled(fixedRate = 10000) // 10초마다 실행
    public void updateCaching(){
        log.info("Caching update ongoing");
        deleteCache();
        updateCache();
        log.info("Caching update successfully finished");
    }

    @CacheEvict(allEntries = true)
    public void deleteCache(){
    }

    @Cacheable(key = "'all'")
    public void updateCache(){
        cachingRecordService.getRecordList();
    }
}
