package com.lmk.yygh.hospital.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lmk.yygh.hospital.mapper.HospitalSetMapper;
import com.lmk.yygh.hospital.service.HospitalSetService;
import com.lmk.yygh.model.hosp.HospitalSet;
import org.springframework.stereotype.Service;

/**
 * @author 李明康
 * @create 2022/8/1 10:49
 */
@Service
public class HospitalSetServiceImpl  extends ServiceImpl<HospitalSetMapper, HospitalSet> implements HospitalSetService {

    /**
     * 根据传递过来的医院编码，查询数据库签名
     * @param hoscode
     * @return
     */
    @Override
    public String getSignKey(String hoscode) {
        QueryWrapper<HospitalSet> wrapper = new QueryWrapper<>();
        wrapper.eq("hoscode", hoscode);
        HospitalSet hospitalSet = baseMapper.selectOne(wrapper);
        return hospitalSet.getSignKey();
    }
}
