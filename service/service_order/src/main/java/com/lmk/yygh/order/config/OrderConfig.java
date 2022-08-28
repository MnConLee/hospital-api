package com.lmk.yygh.order.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author 李明康
 * @create 2022/8/29 0:20
 */
@Configuration
@MapperScan("com.lmk.yygh.order.mapper")
public class OrderConfig {
}
