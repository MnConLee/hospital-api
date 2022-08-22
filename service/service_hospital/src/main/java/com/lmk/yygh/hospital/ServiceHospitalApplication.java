package com.lmk.yygh.hospital;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author 李明康
 * @create 2022/7/31 16:06
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.lmk")
@EnableDiscoveryClient
public class ServiceHospitalApplication {
     public static void main(String[] args) {
           SpringApplication.run(ServiceHospitalApplication.class, args);
      }
}
