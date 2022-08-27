package com.lmk.yygh.user.api;

import com.alibaba.fastjson.JSONObject;
import com.lmk.yygh.common.exception.YyghException;
import com.lmk.yygh.common.helper.JwtHelper;
import com.lmk.yygh.common.result.Result;
import com.lmk.yygh.common.result.ResultCodeEnum;
import com.lmk.yygh.model.acl.User;
import com.lmk.yygh.model.user.UserInfo;
import com.lmk.yygh.user.service.UserInfoService;
import com.lmk.yygh.user.utils.ConstantWxPropertiesUtils;
import com.lmk.yygh.user.utils.HttpClientUtils;
import com.sun.deploy.net.URLEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * 微信操作接口
 *
 * @author 李明康
 * @create 2022/8/27 0:57
 */

@Controller
@RequestMapping("/api/ucenter/wx")
public class WeixinApiController {

    @Autowired
    private UserInfoService userInfoService;
    @Autowired
    private RedisTemplate redisTemplate;


    @GetMapping("callback")
    public String callback(String code, String state) {
        //获取临时票据
        if (StringUtils.isEmpty(state) || StringUtils.isEmpty(code)) {
            throw new YyghException(ResultCodeEnum.ILLEGAL_CALLBACK_REQUEST_ERROR);
        }

        StringBuffer baseAccessTokenUrl = new StringBuffer()
                .append("https://api.weixin.qq.com/sns/oauth2/access_token")
                .append("?appid=%s")
                .append("&secret=%s")
                .append("&code=%s")
                .append("&grant_type=authorization_code");
        String accessTokenUrl = String.format(baseAccessTokenUrl.toString(),
                ConstantWxPropertiesUtils.WX_OPEN_APP_ID,
                ConstantWxPropertiesUtils.WX_OPEN_APP_SECRET,
                code
        );
        try {
            String accesstokenInfo = HttpClientUtils.get(accessTokenUrl);
            //从返回字符串3获取两个值openid和access_token
            JSONObject jsonObject = JSONObject.parseObject(accesstokenInfo);
            String access_token = jsonObject.getString("access_token");
            String openid = jsonObject.getString("openid");
            //判断是否为第一次登录
            UserInfo userInfo = userInfoService.selectWxInfoByOpenid(openid);
            if (userInfo == null) {
                //请求获取扫码人信息
                String baseUserInfoUrl = "https://api.weixin.qq.com/sns/userinfo" +
                        "?access_token=%s" +
                        "&openid=%s";
                String userInfoUrl = String.format(baseUserInfoUrl, access_token, openid);
                String resultInfo = HttpClientUtils.get(userInfoUrl);
                JSONObject resultUserInfojson = JSONObject.parseObject(resultInfo);
                //解析用户信息
                String nickname = resultUserInfojson.getString("nickname");
                String headimgurl = resultUserInfojson.getString("headimgurl");
                //获取扫码人信息添加数据库
                userInfo = new UserInfo();
                userInfo.setNickName(nickname);
                userInfo.setOpenid(openid);
                userInfo.setStatus(1);
                userInfoService.save(userInfo);
            }

            //返回name和token字符串
            Map<String, String> map = new HashMap<>();
            String name = userInfo.getName();
            if (StringUtils.isEmpty(name)) {
                name = userInfo.getNickName();
            }
            if (StringUtils.isEmpty(name)) {
                name = userInfo.getPhone();
            }
            map.put("name", name);
            //判断userInfo是否有手机号，如果手机号为空，返回openid
            //如果手机号不为空，返回openid值为空字符串
            if (StringUtils.isEmpty(userInfo.getPhone())) {
                map.put("openid", userInfo.getOpenid());
            } else {
                map.put("openid", "");
            }

            String token = JwtHelper.createToken(userInfo.getId(), name);
            map.put("token", token);
            //跳转前端页面
            return "redirect:" + ConstantWxPropertiesUtils.YYGH_BASE_URL +
                    "/weixin/callback?token=" +
                    map.get("token") + "&openid=" +
                    map.get("openid") + "&name=" +
                    URLEncoder.encode((String) map.get("name"), "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 获取微信登录参数
     */
    @GetMapping("getLoginParam")
    @ResponseBody
    public Result genQrConnect() {
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("appid", ConstantWxPropertiesUtils.WX_OPEN_APP_ID);
            map.put("scope", "snsapi_login");
            String wxOpenRedirectUrl = ConstantWxPropertiesUtils.WX_OPEN_REDIRECT_URL;
            wxOpenRedirectUrl = URLEncoder.encode(wxOpenRedirectUrl, "utf-8");
            map.put("redirect_uri", wxOpenRedirectUrl);
            map.put("state", System.currentTimeMillis() + "");
            return Result.ok(map);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
