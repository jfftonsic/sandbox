package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main {
    //
    //    @Bean
    //    public CommonsRequestLoggingFilter commonsRequestLoggingFilter() {
    //        CommonsRequestLoggingFilter filter
    //                = new CommonsRequestLoggingFilter();
    //        filter.setIncludeQueryString(true);
    //        filter.setIncludePayload(true);
    //        filter.setMaxPayloadLength(10000);
    //        return filter;
    //    }

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

}
