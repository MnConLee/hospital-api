package com.lmk.yygh.task;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author 李明康
 * @create 2022/9/5 22:54
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableDiscoveryClient
@ComponentScan(basePackages = "com.lmk")
public class ServiceTaskApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceTaskApplication.class, args);
    }
}


