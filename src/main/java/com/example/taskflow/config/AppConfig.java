package com.example.taskflow.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * App-wide beans. ModelMapper isn't a Spring bean by default, so we register
 * it here to let it be injected (e.g. UserService uses it to map
 * entity -> DTO via modelMapper.map(user, UserDto.class)).
 */
@Configuration
public class AppConfig {

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
