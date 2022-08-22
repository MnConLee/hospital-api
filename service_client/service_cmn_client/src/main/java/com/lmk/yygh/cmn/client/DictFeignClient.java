package com.lmk.yygh.cmn.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 李明康
 * @create 2022/8/22 23:38
 */
@RestController
@FeignClient(value = "service-cmn",path = "/admin/cmn/dict")
public interface DictFeignClient {

    /**
     * 返回数据字典名称带dictcode，如医院等级
     *
     * @param dictCode
     * @param value
     * @return
     */
    @GetMapping("getName/{dictCode}/{value}")
    public String getName(@PathVariable("dictCode") String dictCode,
                          @PathVariable ("value") String value);

    /**
     * 返回数据字典名称不带dictcode，如民族和区
     *
     * @param value
     * @return
     */
    @GetMapping("getName/{value}")
    public String getName(@PathVariable("value") String value);
}
