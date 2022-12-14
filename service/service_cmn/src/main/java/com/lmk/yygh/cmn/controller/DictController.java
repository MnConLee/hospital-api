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
@Api(value = "数据字典接口")
@RestController
@RequestMapping("/admin/cmn/dict")
public class DictController {
    @Autowired
    private DictService dictService;

    /**
     * 下载数据字典文件Excel
     *
     * @param response
     * @return
     */
    @GetMapping("exportData")
    public Result exportDict(HttpServletResponse response) {
        dictService.exportDictData(response);
        return Result.ok();
    }

    /**
     * 上传数据字典文件
     *
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
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "根据id查询子数据列表")
    @GetMapping("findChildData/{id}")
    public Result findChildData(@PathVariable Long id) {
        List<Dict> list = dictService.findChild(id);
        return Result.ok(list);
    }

    /**
     * 返回数据字典名称带dictcode，如医院等级
     *
     * @param dictCode
     * @param value
     * @return
     */
    @GetMapping("getName/{dictCode}/{value}")
    public String getName(@PathVariable String dictCode,
                          @PathVariable String value) {
        String dictName = dictService.getDictName(dictCode, value);
        return dictName;
    }

    /**
     * 返回数据字典名称不带dictcode，如民族和区
     *
     * @param value
     * @return
     */
    @GetMapping("getName/{value}")
    public String getName(@PathVariable String value) {
        String dictName = dictService.getDictName("", value);
        return dictName;
    }


    /**
     * 根据dictCode获取下级节点
     * @param dictCode
     * @return
     */
    @ApiOperation(value = "根据dictCode获取下级节点")
    @GetMapping("findByDictCode/{dictCode}")
    public Result findByDictCode(@PathVariable String dictCode){
        List<Dict> list = dictService.findByDictCode(dictCode);
        return Result.ok(list);
    }

}
