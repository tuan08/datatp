package net.data.springframework.cloud.sample;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ApplicationConfiguration {
  @Bean 
  @LoadBalanced
  RestTemplate createBalancedRestTemplate() { 
    RestTemplate rest =  new RestTemplate(); 
    System.err.println("create rest template " + rest.hashCode());
    return rest;
  }
}