package com.cjemison.config;

import com.cjemison.config.messageConverter.JwtMessageConverter;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;

/**
 * Created by cjemison on 6/21/17.
 */
@Configuration
@ComponentScan(basePackages = "com.cjemison.config")
@EnableWebMvc
public class WebConfig extends WebMvcConfigurerAdapter {

  @Bean
  public ObjectMapper objectMapper() {
    return new ObjectMapper();
  }

  @Override
  public void extendMessageConverters(final List<HttpMessageConverter<?>> converters) {
    super.extendMessageConverters(converters);
    converters.add(new JwtMessageConverter(new ObjectMapper()));
  }

  @Override
  public void configureContentNegotiation(final ContentNegotiationConfigurer configurer) {
    // Turn off suffix-based content negotiation
    configurer.favorPathExtension(false);
  }
}
