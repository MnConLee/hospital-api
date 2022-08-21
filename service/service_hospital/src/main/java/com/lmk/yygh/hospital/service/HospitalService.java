package com.lmk.yygh.hospital.service;

import com.lmk.yygh.model.hosp.Hospital;

import java.util.Map;

/**
 * @author 李明康
 * @create 2022/8/21 14:22
 */
public interface HospitalService {
    void save(Map<String, Object> paramMap);

    Hospital getByHoscode(String hoscode);
}