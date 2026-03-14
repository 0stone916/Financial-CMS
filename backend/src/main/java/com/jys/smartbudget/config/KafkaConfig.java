package com.jys.smartbudget.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class KafkaConfig {

    private static final Logger auditLog = LoggerFactory.getLogger("AUDIT");

    @Bean
    public CommonErrorHandler errorHandler() {
        // Redis 분산락의 자동 락 해제 시간인 10초에 고려해 3초 간격으로 5번 재시도
        FixedBackOff backOff = new FixedBackOff(300L, 5);

            // 모든 재시도 실패 시
            return new DefaultErrorHandler((record, exception) -> {
                  String payload = (String) record.value();
                          
                  // 실제 근본 원인(Cause)을 찾아서 메시지로 추출
                  Throwable rootCause = exception;
                  while (rootCause.getCause() != null) {
                      rootCause = rootCause.getCause();
                  }
                  String detailedReason = rootCause.getMessage();
                  
                  // 감사 로그 기록 
                  auditLog.info("[KAFKA_FINAL_FAILURE] Topic: {}, Payload: {}, Reason: {}", 
                      record.topic(), payload, detailedReason);
                
                log.error(">>>> [Kafka] 최종 재시도 실패로 감사 로그 기록 완료: {}", payload);
        }, backOff);
    }
}