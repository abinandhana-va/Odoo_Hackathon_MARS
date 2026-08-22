package com.example.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

@Configuration
@EnableWebMvc
//Enables Spring MVC features.
//It activates:
//@Controller,@RequestMapping,@GetMapping,@PostMapping
//View Resolver support
//Data binding
//Without this, MVC annotations may not work properly.
@ComponentScan("com.example")
public class WebConfig {
    @Bean
    //it is string framework
    //
    public InternalResourceViewResolver viewResolver() {
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setPrefix("/WEB-INF/views/");
        resolver.setSuffix(".jsp");
        return resolver;
    }
}
