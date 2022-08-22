package com.lmk.yygh.hospital.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.lmk.yygh.hospital.repository.HospitalRepository;
import com.lmk.yygh.hospital.service.HospitalService;
import com.lmk.yygh.model.hosp.Hospital;
import com.lmk.yygh.vo.hosp.HospitalQueryVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

/**
 * @author 李明康
 * @create 2022/8/21 14:23
 */
@Service
public class HospitalServiceImpl implements HospitalService {
    @Autowired
    private HospitalRepository hospitalRepository;

    /**
     * 上传医院
     *
     * @param paramMap
     */
    @Override
    public void save(Map<String, Object> paramMap) {
        //把参数map集合转换对象
        String mapString = JSONObject.toJSONString(paramMap);
        Hospital hospital = JSONObject.parseObject(mapString, Hospital.class);
        //判断是否存在数据
        String hoscode = hospital.getHoscode();
        Hospital hospitalExist = hospitalRepository.getHospitalByHoscode(hoscode);
        //如果存在，进行修改
        if (hospitalExist != null) {
            hospital.setStatus(hospitalExist.getStatus());
            hospital.setCreateTime(hospitalExist.getCreateTime());
            hospital.setUpdateTime(new Date());
            hospital.setIsDeleted(0);
            hospital.setId(hospitalExist.getId());
            hospitalRepository.save(hospital);
        } else {//如果不存在，进行添加
            hospital.setStatus(0);
            hospital.setCreateTime(new Date());
            hospital.setUpdateTime(new Date());
            hospital.setIsDeleted(0);
            hospitalRepository.save(hospital);

        }

    }

    /**
     * 查询医院信息
     *
     * @param hoscode
     * @return
     */
    @Override
    public Hospital getByHoscode(String hoscode) {
        Hospital hospital = hospitalRepository.getHospitalByHoscode(hoscode);
        return hospital;
    }

    /**
     * 医院列表
     * @param page
     * @param limit
     * @param hospitalQueryVo
     * @return
     */
    @Override
    public Page selectHospPage(Integer page, Integer limit, HospitalQueryVo hospitalQueryVo) {
        Pageable pageable = PageRequest.of(page - 1, limit);
        ExampleMatcher matcher = ExampleMatcher.matching().withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase(true);
        Hospital hospital = new Hospital();
        BeanUtils.copyProperties(hospitalQueryVo,hospital);
        //创建对象
        Example<Hospital> example = Example.of(hospital, matcher);
        Page<Hospital> all = hospitalRepository.findAll(example, pageable);
        return all;
    }
}
