package com.lmk.yygh.hospital.controller;

import com.lmk.yygh.hospital.service.HospitalSetService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 李明康
 * @create 2022/8/1 11:41
 */
@RestController
@RequestMapping("/admin/hosp/hospitalSet")
public class HospitalSetController {
    private HospitalSetService hospitalSetService;

}
