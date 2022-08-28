package com.lmk.yygh.hospital.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lmk.yygh.common.exception.YyghException;
import com.lmk.yygh.common.result.ResultCodeEnum;
import com.lmk.yygh.common.utils.MD5;
import com.lmk.yygh.hospital.mapper.HospitalSetMapper;
import com.lmk.yygh.hospital.service.HospitalSetService;
import com.lmk.yygh.model.hosp.Hospital;
import com.lmk.yygh.model.hosp.HospitalSet;
import com.lmk.yygh.vo.order.SignInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author 李明康
 * @create 2022/8/1 10:49
 */
@Service
public class HospitalSetServiceImpl  extends ServiceImpl<HospitalSetMapper, HospitalSet> implements HospitalSetService {
    @Autowired
    private HospitalSetService hospitalSetService;

    /**
     * 判断签名是否一致
     * @param paramMap
     */
    @Override
    public void eqSign(Map<String, Object> paramMap) {
        //获取医院编号
        String hoscode = (String)paramMap.get("hoscode");
        //获取医院系统传递过来的签名(进行过MD5加密)
        String hospSign = (String) paramMap.get("sign");
        //根据传递过来的医院编码，查询数据库签名
        String signKey = hospitalSetService.getSignKey(hoscode);
        //查询签名进行加密
        String signKeyMd5 = MD5.encrypt(signKey);
        //判断签名是否一致
        if (!hospSign.equals(signKeyMd5)) {
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }
    }

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

    @Override
    public SignInfoVo getSignInfoVo(String hoscode) {
        QueryWrapper<HospitalSet> wrapper = new QueryWrapper<>();
        wrapper.eq("hoscode", hoscode);
        HospitalSet hospitalSet = baseMapper.selectOne(wrapper);
        if (null == hospitalSet) {
            throw new YyghException(ResultCodeEnum.HOSPITAL_OPEN);
        }
        SignInfoVo signInfoVo = new SignInfoVo();
        signInfoVo.setApiUrl(hospitalSet.getApiUrl());
        signInfoVo.setSignKey(hospitalSet.getSignKey());
        return signInfoVo;
    }
}
