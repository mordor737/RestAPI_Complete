package com.base;

import ch.qos.logback.core.net.SyslogOutputStream;
import com.base.security.AppProperties;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Arrays;
import java.util.stream.Collectors;

@SpringBootApplication
public class RestApiApplication extends SpringBootServletInitializer {

  @Override
  protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
    return application.sources(RestApiApplication.class);
  }

  public static void main(String[] args) {
    SpringApplication.run(RestApiApplication.class, args);
  }

  @Bean
  public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
    return args -> {
      System.out.println("Let's inspect the beans provided by Spring Boot:");
      String[] beansNames = ctx.getBeanDefinitionNames();
      Arrays.sort(beansNames);
            /*Arrays.asList(beansNames).stream().map(bean -> {
                return bean + " : ";
            }).forEach(System.out::print);*/
    };
  }

  @Bean
  public BCryptPasswordEncoder bCryptPasswordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public SpringApplicationContext springApplicationContext() {
    return new SpringApplicationContext();
  }

  @Bean(name = "AppProperties")
  public AppProperties appProperties() {
    return new AppProperties();
  }
}
