package com.lmk.yygh.hospital.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.lmk.yygh.hospital.repository.DepartmentRepository;
import com.lmk.yygh.hospital.service.DepartmentService;
import com.lmk.yygh.model.hosp.Department;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

/**
 * @author 李明康
 * @create 2022/8/21 19:13
 */
@Service
public class DepartmentServiceImpl implements DepartmentService {
    @Autowired
    DepartmentRepository departmentRepository;

    @Override
    public void save(Map<String, Object> paramMap) {
        //paramMap转换为对象
        String paramMapString = JSONObject.toJSONString(paramMap);
        Department department = JSONObject.parseObject(paramMapString, Department.class);
        Department departmentExist =
                departmentRepository.getDepartmentByHoscodeAndDepcode(department.getHoscode(), department.getDepcode());
        System.out.println("departmentExist"+departmentExist);
        if (departmentExist != null) {
            department.setId(departmentExist.getId());
            department.setUpdateTime(new Date());
            department.setIsDeleted(0);
            departmentRepository.save(department);
        }else {
            department.setCreateTime(new Date());
            department.setUpdateTime(new Date());
            department.setIsDeleted(0);
            departmentRepository.save(department);
        }

    }
}
