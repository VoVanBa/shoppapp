package com.example.shoppapp.Configurations;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapperConfiguartion {
    @Bean
    public ModelMapper modelMapper(){
        return new ModelMapper();
    }

}
