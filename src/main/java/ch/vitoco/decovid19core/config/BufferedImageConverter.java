package ch.vitoco.decovid19core.config;

import java.awt.image.BufferedImage;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.BufferedImageHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;

/**
 * Configuration class for a BufferedImage Response.
 */
@Configuration
public class BufferedImageConverter {

  @Bean
  public HttpMessageConverter<BufferedImage> httpMessageConverter() {
    return new BufferedImageHttpMessageConverter();
  }

}
