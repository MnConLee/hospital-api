package com.lmk.yygh.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lmk.yygh.model.user.Patient;
import com.lmk.yygh.model.user.UserInfo;

import java.util.List;

/**
 * @author 李明康
 * @create 2022/8/28 0:11
 */
public interface PatientService extends IService<Patient> {

    List<Patient> findAllUserId(Long userId);

    Patient getPatientId(Long id);

}
