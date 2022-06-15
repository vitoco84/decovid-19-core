package ch.vitoco.decovid19core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import ch.vitoco.decovid19core.config.ConfigProperties;

/**
 * SpringBootApplication DECOVID-19 API.
 */
@SpringBootApplication
@EnableConfigurationProperties(ConfigProperties.class)
public class Decovid19CoreApplication {

  public static void main(String[] args) {
    SpringApplication.run(Decovid19CoreApplication.class, args);
  }

}
