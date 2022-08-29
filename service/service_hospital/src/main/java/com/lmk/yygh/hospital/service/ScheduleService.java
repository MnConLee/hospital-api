package com.lmk.yygh.hospital.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lmk.yygh.model.hosp.Schedule;
import com.lmk.yygh.vo.hosp.ScheduleOrderVo;
import com.lmk.yygh.vo.hosp.ScheduleQueryVo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

/**
 * @author 李明康
 * @create 2022/8/22 10:44
 */
public interface ScheduleService extends IService<Schedule> {

    void save(Map<String, Object> paramMap);

    Page<Schedule> findPageSchedule(int page, int limit, ScheduleQueryVo scheduleQueryVo);

    void remove(String hoscode, String hosScheduleId);

    Map<String, Object> getScheduleRule(Long page, Long limit, String hoscode, String depcode);

    List<Schedule> getScheduleDetail(String hoscode, String depcode, String workDate);

    Map<String, Object> getBookingScheduleRule(int page, int limit, String hoscode, String depcode);

    Schedule getScheduleId(String scheduleId);

    ScheduleOrderVo getScheduleOrderVo(String scheduleId);

    /**
     * 更新排班数据
     */
    void update(Schedule schedule);
}
