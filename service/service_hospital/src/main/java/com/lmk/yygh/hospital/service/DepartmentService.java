package com.lmk.yygh.hospital.service;

import com.lmk.yygh.model.hosp.Department;
import com.lmk.yygh.vo.hosp.DepartmentQueryVo;
import com.lmk.yygh.vo.hosp.DepartmentVo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

/**
 * @author 李明康
 * @create 2022/8/21 19:13
 */
public interface DepartmentService {
    void save(Map<String, Object> paramMap);

    Page<Department> findPageDepartment(int page, int limit, DepartmentQueryVo departmentQueryVo);

    void remove(String hoscode, String depcode);

    List<DepartmentVo> findDeptTree(String hoscode);
}
