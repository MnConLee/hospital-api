package com.lmk.yygh.user.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lmk.yygh.common.result.Result;
import com.lmk.yygh.model.user.UserInfo;
import com.lmk.yygh.user.service.UserInfoService;
import com.lmk.yygh.vo.user.UserInfoQueryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author 李明康
 * @create 2022/8/28 9:48
 */
@RestController
@RequestMapping("/admin/user")
public class UserController {
    @Autowired
    private UserInfoService userInfoService;

    /**
     * 用户列表
     * @param page
     * @param limit
     * @param userInfoQueryVo
     * @return
     */
    @GetMapping("{page}/{limit}")
    public Result list(@PathVariable Long page,
                       @PathVariable Long limit,
                       UserInfoQueryVo userInfoQueryVo) {
        Page<UserInfo> pageparam = new Page<>(page, limit);
        IPage<UserInfo> pageModel = userInfoService.selectPage(pageparam, userInfoQueryVo);
        return Result.ok(pageModel);
    }

    /**
     * 用户锁定
     * @param userId
     * @param status
     * @return
     */
    @GetMapping("lock/{userId}/{status}")
    public Result lock(@PathVariable Long userId, @PathVariable Integer status) {
        userInfoService.lock(userId, status);
        return Result.ok();
    }

    /**
     * 用户详情
     * @param userId
     * @return
     */
    @GetMapping("show/{userId}")
    public Result show(@PathVariable Long userId) {
        Map<String, Object> map = userInfoService.show(userId);
        return Result.ok(map);
    }

    /**
     * 认证审批
     * @param userId
     * @param authStatus
     * @return
     */
    @GetMapping("approval/{userId}/{authStatus}")
    public Result approval(@PathVariable Long userId, @PathVariable Integer authStatus) {
        userInfoService.approval(userId, authStatus);
        return Result.ok();

    }

}
