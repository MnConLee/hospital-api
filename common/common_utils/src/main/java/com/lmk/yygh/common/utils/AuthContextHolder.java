package com.lmk.yygh.common.utils;

import com.lmk.yygh.common.helper.JwtHelper;

import javax.servlet.http.HttpServletRequest;

/**
 * @author 李明康
 * @create 2022/8/27 23:04
 */
public class AuthContextHolder {
    /**
     * 获取当前用户id
     * @param request
     * @return
     */
    public static Long getUserId(HttpServletRequest request) {
        //从header获取token
        String token = request.getHeader("token");
        //jwt从token获取userid
        Long userId = JwtHelper.getUserId(token);
        return userId;
    }

    /**
     * 获取用户名称
     * @param request
     * @return
     */
    public static String getUserName(HttpServletRequest request) {
        //从header获取token
        String token = request.getHeader("token");
        //jwt从token获取userid
        String userName = JwtHelper.getUserName(token);
        return userName;
    }
}
