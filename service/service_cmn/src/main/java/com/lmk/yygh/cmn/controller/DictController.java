package com.lmk.yygh.cmn.controller;

import com.lmk.yygh.cmn.service.DictService;
import com.lmk.yygh.common.result.Result;
import com.lmk.yygh.model.cmn.Dict;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author 李明康
 * @create 2022/8/14 15:29
 */
@Api(value = "数据字典接口")
@RestController
@RequestMapping("/admin/cmn/dict")
public class DictController {
    @Autowired
    private DictService dictService;

    /**
     * 根据数据id查询子数据
     * @param id
     * @return
     */
    @ApiOperation(value = "根据id查询子数据列表")
    @GetMapping("findChildData/{id}")
    public Result findChildData(@PathVariable Long id) {
        List<Dict> list = dictService.findChild(id);
        return Result.ok(list);
    }

}
