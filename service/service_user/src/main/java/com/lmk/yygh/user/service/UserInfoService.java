package com.lmk.yygh.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lmk.yygh.model.user.UserInfo;
import com.lmk.yygh.vo.user.LoginVo;
import com.lmk.yygh.vo.user.UserAuthVo;

import java.util.Map;

/**
 * @author 李明康
 * @create 2022/8/26 11:13
 */
public interface UserInfoService extends IService<UserInfo> {

    Map<String, Object> loginUser(LoginVo loginVo);

    UserInfo selectWxInfoByOpenid(String openid);

    void userAuth(Long userId, UserAuthVo userAuthVo);
}
