package com.lmk.yygh.cmn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author 李明康
 * @create 2022/8/14 13:28
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.lmk")
public class ServiceCmnApplication {
     public static void main(String[] args) {
           SpringApplication.run(ServiceCmnApplication.class, args);
      }
}
