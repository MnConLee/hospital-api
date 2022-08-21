package com.lmk.yygh.hospital.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lmk.yygh.model.hosp.Hospital;
import com.lmk.yygh.model.hosp.HospitalSet;

import java.util.Map;

/**
 * @author 李明康
 * @create 2022/8/1 10:09
 */
public interface HospitalSetService extends IService<HospitalSet> {
    void eqSign(Map<String, Object> paramMap);

    String getSignKey(String hoscode);

}
