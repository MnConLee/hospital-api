package com.lmk.yygh.hospital.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author 李明康
 * @create 2022/8/1 12:18
 */
@Configuration
@MapperScan("com.lmk.yygh.hospital.mapper")
public class HospConfig {
}
