package com.lmk.yygh.hospital.repository;

import com.lmk.yygh.model.hosp.Department;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @author 李明康
 * @create 2022/8/21 19:11
 */
@Repository
public interface DepartmentRepository extends MongoRepository<Department, String> {
    /**
     * 查询科室信息
     * @param hoscode
     * @param depcode
     * @return
     */
    Department getDepartmentByHoscodeAndDepcode(String hoscode, String depcode);
}
