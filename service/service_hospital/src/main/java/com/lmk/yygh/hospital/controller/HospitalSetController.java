package com.lmk.yygh.hospital.controller;

import com.lmk.yygh.hospital.service.HospitalSetService;
import com.lmk.yygh.model.hospital.HospitalSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 李明康
 * @create 2022/8/1 11:41
 */
@RestController
@RequestMapping("/admin/hosp/hospitalSet")
public class HospitalSetController {
    @Autowired
    private HospitalSetService hospitalSetService;

    /**
     * 查询医院所有表的基本信息
     */
    @GetMapping("findAll")
    public List<HospitalSet> findAllHospitalSet() {
        List<HospitalSet> hospitalSetList = hospitalSetService.list();
        return hospitalSetList;
    }

    @DeleteMapping("{id}")
    public boolean removeHospSet(@PathVariable Long id) {
        boolean flag = hospitalSetService.removeById(id);
        return flag;
    }
}
