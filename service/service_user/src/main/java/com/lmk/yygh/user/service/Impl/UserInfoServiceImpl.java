package com.lmk.yygh.user.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lmk.yygh.common.exception.YyghException;
import com.lmk.yygh.common.helper.JwtHelper;
import com.lmk.yygh.common.result.ResultCodeEnum;
import com.lmk.yygh.enums.AuthStatusEnum;
import com.lmk.yygh.model.user.UserInfo;
import com.lmk.yygh.user.mapper.UserInfoMapper;
import com.lmk.yygh.user.service.UserInfoService;
import com.lmk.yygh.vo.user.LoginVo;
import com.lmk.yygh.vo.user.UserAuthVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 李明康
 * @create 2022/8/26 11:14
 */
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    /**
     * 用户手机登录
     * @param loginVo
     * @return
     */
    @Override
    public Map<String, Object> loginUser(LoginVo loginVo) {
        //从loginVo获取输入的手机号
        String phone = loginVo.getPhone();
        String code = loginVo.getCode();
        //判断手机号和验证码是否为空
        if (StringUtils.isEmpty(phone) || StringUtils.isEmpty(code)) {
            throw new YyghException(ResultCodeEnum.PARAM_ERROR);
        }
        // 判断手机验证码和输入验证码是否一致
        String redisCode = redisTemplate.opsForValue().get(phone);
        if (!code.equals(redisCode)) {
            throw new YyghException(ResultCodeEnum.CODE_ERROR);
        }
        //如果没有经过微信填充值则走短信
        UserInfo userInfo = null;
        //绑定手机号码
        if(!StringUtils.isEmpty(loginVo.getOpenid())) {
            userInfo = this.selectWxInfoByOpenid(loginVo.getOpenid());
            if(null != userInfo) {
                userInfo.setPhone(loginVo.getPhone());
                this.updateById(userInfo);
            } else {
                throw new YyghException(ResultCodeEnum.DATA_ERROR);
            }
        }

        //如果userInfo为空，进行正常的手机登录
        if (userInfo == null) {

            //判断是否为第一次登录
            QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
            wrapper.eq("phone", phone);
            userInfo = baseMapper.selectOne(wrapper);
            //返回登录信息（返回登录用户名，返回token信息）
            //第一次登录
            if (userInfo == null) {
                userInfo = new UserInfo();
                userInfo.setName("");
                userInfo.setPhone(phone);
                userInfo.setStatus(1);
                //添加信息
                baseMapper.insert(userInfo);
            }
        }


        if (userInfo.getStatus() == 0) {
            throw new YyghException(ResultCodeEnum.LOGIN_DISABLED_ERROR);
        }
        Map<String, Object> map = new HashMap<>();
        String name = userInfo.getName();
        if (StringUtils.isEmpty(name)) {
            name = userInfo.getNickName();
        }
        if (StringUtils.isEmpty(name)) {
            name = userInfo.getPhone();
        }
        map.put("name", name);
        //token的生成
        String token = JwtHelper.createToken(userInfo.getId(), name);
        map.put("token", token);
        return map;
    }

    @Override
    public UserInfo selectWxInfoByOpenid(String openid) {
        QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("openid", openid);
        UserInfo userInfo = baseMapper.selectOne(wrapper);
        return userInfo;
    }

    @Override
    public void userAuth(Long userId, UserAuthVo userAuthVo) {
        //根据用户id查询用户信息
        UserInfo userInfo = baseMapper.selectById(userId);
        //设置认证信息
        userInfo.setName(userAuthVo.getName());
        userInfo.setCertificatesType(userAuthVo.getCertificatesType());
        userInfo.setCertificatesNo(userAuthVo.getCertificatesNo());
        userInfo.setCertificatesUrl(userAuthVo.getCertificatesUrl());
        userInfo.setAuthStatus(AuthStatusEnum.AUTH_RUN.getStatus());
        //进行信息更新
        baseMapper.updateById(userInfo);
    }
}
