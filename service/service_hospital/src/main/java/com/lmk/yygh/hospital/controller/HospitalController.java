package com.lmk.yygh.hospital.controller;

import com.lmk.yygh.common.result.Result;
import com.lmk.yygh.hospital.service.HospitalService;
import com.lmk.yygh.vo.hosp.HospitalQueryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

/**
 * @author 李明康
 * @create 2022/8/22 19:08
 */
@RestController
@RequestMapping("/admin/hosp/hospital")
@CrossOrigin
public class HospitalController {
    @Autowired
    private HospitalService hospitalService;

    /**
     * 医院列表
     * @param page
     * @param limit
     * @return
     */
    @GetMapping("list/{page}/{limit}")
    public Result listHosp(@PathVariable Integer page,
                           @PathVariable Integer limit,
                           HospitalQueryVo hospitalQueryVo){
        Page pageModel = hospitalService.selectHospPage(page, limit, hospitalQueryVo);
        return Result.ok(pageModel);
    }

}
