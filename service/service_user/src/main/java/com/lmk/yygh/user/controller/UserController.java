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

/**
 * @author 李明康
 * @create 2022/8/28 9:48
 */
@RestController
@RequestMapping("/admin/user")
public class UserController {
    @Autowired
    private UserInfoService userInfoService;

    @GetMapping("{page}/{limit}")
    public Result list(@PathVariable Long page,
                       @PathVariable Long limit,
                       UserInfoQueryVo userInfoQueryVo) {
        Page<UserInfo> pageparam = new Page<>(page, limit);
        IPage<UserInfo> pageModel = userInfoService.selectPage(pageparam, userInfoQueryVo);
        return Result.ok(pageModel);

    }
}
