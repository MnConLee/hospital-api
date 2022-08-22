package com.lmk.yygh.hospital.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.lmk.yygh.hospital.repository.ScheduleRepository;
import com.lmk.yygh.hospital.service.ScheduleService;
import com.lmk.yygh.model.hosp.Schedule;
import com.lmk.yygh.vo.hosp.ScheduleQueryVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Date;
import java.util.Map;

/**
 * @author 李明康
 * @create 2022/8/22 10:44
 */
@Service
public class ScheduleServiceImpl implements ScheduleService {
    @Autowired
    private ScheduleRepository scheduleRepository;

    /**
     * 上传科室信息
     * @param paramMap
     */
    @Override
    public void save(Map<String, Object> paramMap) {
        //paramMap转换为对象
        String paramMapString = JSONObject.toJSONString(paramMap);
        Schedule schedule = JSONObject.parseObject(paramMapString, Schedule.class);
        Schedule scheduleExist =
                scheduleRepository.getScheduleByHoscodeAndHosScheduleId(schedule.getHoscode(), schedule.getHosScheduleId());
        if (scheduleExist != null) {
            schedule.setId(scheduleExist.getId());
            schedule.setUpdateTime(new Date());
            schedule.setIsDeleted(0);
            scheduleRepository.save(schedule);
        } else {
            schedule.setCreateTime(new Date());
            schedule.setUpdateTime(new Date());
            schedule.setIsDeleted(0);
            scheduleRepository.save(schedule);
        }
    }

    /**
     * 排班查询
     * @param page
     * @param limit
     * @param scheduleQueryVo
     * @return
     */
    @Override
    public Page<Schedule> findPageSchedule(int page, int limit, ScheduleQueryVo scheduleQueryVo) {
        //创建pageable,0是第一页
        Schedule Schedule = new Schedule();
        BeanUtils.copyProperties(scheduleQueryVo, Schedule);
        Schedule.setIsDeleted(0);
        Schedule.setStatus(1);
        Pageable pageable = PageRequest.of(page - 1, limit);
        //创建条件
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase(true);
        Example<Schedule> example = Example.of(Schedule, matcher);
        Page<Schedule> all = scheduleRepository.findAll(example, pageable);
        return all;
    }

    /**
     * 删除排班
     * @param hoscode
     * @param hosScheduleId
     */
    @Override
    public void remove(String hoscode, String hosScheduleId) {
        //根据医院编号和排班编号查询信息
        Schedule schedule = scheduleRepository.getScheduleByHoscodeAndHosScheduleId(hoscode, hosScheduleId);
        if (schedule != null) {
            scheduleRepository.deleteById(schedule.getId());
        }
    }
}
