package se.fulkopinglibraryweb.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
public class LoggingConfig {
    
    @Bean
    public Logger applicationLogger() {
        return LoggerFactory.getLogger("FulkopingLibrary");
    }
}