package com.lmk.yygh.user.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author 李明康
 * @create 2022/8/26 12:24
 */
@Configuration
@MapperScan("com.lmk.yygh.user.mapper")
public class UserConfig {

}
