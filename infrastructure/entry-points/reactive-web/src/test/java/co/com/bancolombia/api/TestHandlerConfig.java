package co.com.bancolombia.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@Configuration
public class TestHandlerConfig {

    @Bean
    public Handler handler() {
        return new Handler(null, null, null, new LocalValidatorFactoryBean(), null);
    }
}


