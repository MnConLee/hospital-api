package com.lmk.yygh.hospital.repository;

import com.lmk.yygh.model.hosp.Hospital;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @author 李明康
 * @create 2022/8/21 14:20
 */
@Repository
public interface HospitalRepository extends MongoRepository<Hospital,String> {

    /**
     * 判断是否存在这个数据
     * @param hoscode
     * @return
     */
    Hospital getHospitalByHoscode(String hoscode);
}
