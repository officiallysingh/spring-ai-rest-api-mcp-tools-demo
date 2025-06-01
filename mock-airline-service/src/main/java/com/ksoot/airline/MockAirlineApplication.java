package com.ksoot.airline;

import io.mongock.runner.springboot.EnableMongock;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableMongock
// @OpenAPIDefinition(servers = {@Server(url = "${server.servlet.context-path}")})
public class MockAirlineApplication {

  public static void main(final String[] args) {
    SpringApplication.run(MockAirlineApplication.class, args);
  }
}
