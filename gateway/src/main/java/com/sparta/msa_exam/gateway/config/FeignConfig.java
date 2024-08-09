package com.sparta.msa_exam.gateway.config;

import feign.codec.Decoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// FeignClient 를 Lazy Load 할 경우 HttpMessageConverters 문제로 아래 config 설정이 필요합니다.
// open issue https://github.com/spring-cloud/spring-cloud-openfeign/issues/235
@Configuration
public class FeignConfig {
    @Bean
    public Decoder feignDecoder() {

        ObjectFactory<HttpMessageConverters> messageConverters = () -> {
            HttpMessageConverters converters = new HttpMessageConverters();
            return converters;
        };
        return new SpringDecoder(messageConverters);
    }
}
