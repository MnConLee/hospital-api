package com.lmk.yygh.cmn.controller;

import com.lmk.yygh.cmn.service.DictService;
import com.lmk.yygh.common.result.Result;
import com.lmk.yygh.model.cmn.Dict;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author 李明康
 * @create 2022/8/14 15:29
 */
@CrossOrigin
@Api(value = "数据字典接口")
@RestController
@RequestMapping("/admin/cmn/dict")
public class DictController {
    @Autowired
    private DictService dictService;

    /**
     * 下载数据字典文件Excel
     * @param response
     * @return
     */
    @GetMapping("exportData")
    public Result exportDict(HttpServletResponse response){
        dictService.exportDictData(response);
        return Result.ok();
    }

    /**
     * 上传数据字典文件
     * @param file
     * @return
     */
    @PostMapping("importData")
    public Result importDict(MultipartFile file) {
        dictService.importDictData(file);
        return Result.ok();
    }


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
