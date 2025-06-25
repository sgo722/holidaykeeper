package com.planitsquare.holidaykeeper.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient nagerRestClient() {
        // Jackson 컨버터에 text/json 추가
        MappingJackson2HttpMessageConverter jacksonConverter = new MappingJackson2HttpMessageConverter();
        List<MediaType> mediaTypes = new ArrayList<>(jacksonConverter.getSupportedMediaTypes());
        mediaTypes.add(MediaType.valueOf("text/json"));
        jacksonConverter.setSupportedMediaTypes(mediaTypes);

        return RestClient.builder()
                .baseUrl("https://date.nager.at")
                .messageConverters(converters -> {
                    converters.add(jacksonConverter);
                })
                .build();
    }

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();

        // 기존 컨버터 가져오기
        List<MappingJackson2HttpMessageConverter> converters = new ArrayList<>();
        for (var converter : restTemplate.getMessageConverters()) {
            if (converter instanceof MappingJackson2HttpMessageConverter jacksonConverter) {
                converters.add(jacksonConverter);
            }
        }

        // text/json도 지원하도록 추가
        for (MappingJackson2HttpMessageConverter converter : converters) {
            List<MediaType> supportedMediaTypes = new ArrayList<>(converter.getSupportedMediaTypes());
            supportedMediaTypes.add(MediaType.valueOf("text/json"));
            converter.setSupportedMediaTypes(supportedMediaTypes);
        }

        return restTemplate;
    }
}