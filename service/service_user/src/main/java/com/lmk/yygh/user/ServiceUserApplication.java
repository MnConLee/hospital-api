package com.lmk.yygh.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author 李明康
 * @create 2022/8/26 10:59
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.lmk")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.lmk")
public class ServiceUserApplication {
     public static void main(String[] args) {
           SpringApplication.run(ServiceUserApplication.class, args);
      }
}
