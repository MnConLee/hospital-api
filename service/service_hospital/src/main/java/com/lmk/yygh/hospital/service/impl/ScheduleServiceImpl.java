package com.lmk.yygh.hospital.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lmk.yygh.common.exception.YyghException;
import com.lmk.yygh.common.result.ResultCodeEnum;
import com.lmk.yygh.hospital.mapper.ScheduleMapper;
import com.lmk.yygh.hospital.repository.ScheduleRepository;
import com.lmk.yygh.hospital.service.DepartmentService;
import com.lmk.yygh.hospital.service.HospitalService;
import com.lmk.yygh.hospital.service.ScheduleService;
import com.lmk.yygh.model.hosp.BookingRule;
import com.lmk.yygh.model.hosp.Department;
import com.lmk.yygh.model.hosp.Hospital;
import com.lmk.yygh.model.hosp.Schedule;
import com.lmk.yygh.vo.hosp.BookingScheduleRuleVo;
import com.lmk.yygh.vo.hosp.ScheduleOrderVo;
import com.lmk.yygh.vo.hosp.ScheduleQueryVo;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 李明康
 * @create 2022/8/22 10:44
 */
@Service
public class ScheduleServiceImpl extends ServiceImpl<ScheduleMapper,Schedule> implements ScheduleService {
    @Autowired
    private ScheduleRepository scheduleRepository;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private HospitalService hospitalService;
    @Autowired
    private DepartmentService departmentService;

    /**
     * 上传科室信息
     *
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
     *
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
     *
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

    /**
     * 查出排班日期，星期，当日总数，单日可预约和剩余，医院名称
     *
     * @param page
     * @param limit
     * @param hoscode
     * @param depcode
     * @return
     */
    @Override
    public Map<String, Object> getScheduleRule(Long page, Long limit, String hoscode, String depcode) {
        //查出排班数据
        Criteria criteria = Criteria.where("hoscode").is(hoscode).and("depcode").is(depcode);

        //根据工作日分组
        Aggregation aggregation = Aggregation.newAggregation(
                //匹配条件
                Aggregation.match(criteria),
                //分组字段
                Aggregation.group("workDate")
                        .first("workDate").as("workDate")
                        //统计号源数量
                        .count().as("docCount")
                        .sum("reservedNumber").as("reservedNumber")
                        .sum("availableNumber").as("availableNumber"),
                //排序
                Aggregation.sort(Sort.Direction.DESC, "workDate"),
                //实现分页
                Aggregation.skip((page - 1) * limit),
                Aggregation.limit(limit)
        );
        AggregationResults<BookingScheduleRuleVo> aggResult =
                mongoTemplate.aggregate(aggregation, Schedule.class, BookingScheduleRuleVo.class);
        List<BookingScheduleRuleVo> bookingScheduleRuleVoList = aggResult.getMappedResults();
        //分组查询的总记录数
        Aggregation totalAgg = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group("workDate")
        );
        AggregationResults<BookingScheduleRuleVo> totalAggResult =
                mongoTemplate.aggregate(totalAgg, Schedule.class, BookingScheduleRuleVo.class);
        int total = totalAggResult.getMappedResults().size();
        //处理日期转换星期
        for (BookingScheduleRuleVo bookingScheduleRuleVo : bookingScheduleRuleVoList) {
            Date workDate = bookingScheduleRuleVo.getWorkDate();
            String dayOfWeek = this.getDayOfWeek(new DateTime(workDate));
            bookingScheduleRuleVo.setDayOfWeek(dayOfWeek);
        }
        //设置最终数据，进行返回
        Map<String, Object> result = new HashMap<>();
        result.put("bookingScheduleRuleList", bookingScheduleRuleVoList);
        result.put("total", total);
        //获取医院名称
        String hosName = hospitalService.getHospName(hoscode);
        HashMap<String, String> baseMap = new HashMap<>();
        baseMap.put("hosname", hosName);
        result.put("baseMap", baseMap);
        return result;
    }

    /**
     * 查询排班详情信息
     *
     * @param hoscode
     * @param depcode
     * @param workDate
     * @return
     */
    @Override
    public List<Schedule> getScheduleDetail(String hoscode, String depcode, String workDate) {
        List<Schedule> scheduleList =
                scheduleRepository.findScheduleByHoscodeAndDepcodeAndWorkDate(hoscode, depcode, new DateTime(workDate).toDate());
        //遍历list集合，设置其他值
        scheduleList.stream().forEach(item -> {
            this.packageSchedule(item);
        });
        return scheduleList;
    }

    /**
     * 获取可预约的排班数据
     *
     * @param page
     * @param limit
     * @param hoscode
     * @param depcode
     * @return
     */
    @Override
    public Map<String, Object> getBookingScheduleRule(int page, int limit, String hoscode, String depcode) {
        Map<String, Object> result = new HashMap<>();
        //获取预约规则
        Hospital hospital = hospitalService.getByHoscode(hoscode);
        if (hospital == null) {
            throw new YyghException(ResultCodeEnum.DATA_ERROR);
        }
        BookingRule bookingRule = hospital.getBookingRule();
        //获取可预约日期数据(分页)
        IPage iPage = this.getListDate(page, limit, bookingRule);
        //当前预约日期
        List<Date> dateList = iPage.getRecords();

        //获取可预约日期里面科室的剩余预约
        Criteria criteria = Criteria.where("hoscode").is(hoscode).and("depcode").is(depcode)
                .and("workDate").in(dateList);
        Aggregation agg = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group("workDate").first("workDate").as("workDate")
                        .count().as("docCount")
                        .sum("availableNumber").as("availableNumber")
                        .sum("reservedNumber").as("reservedNumber")
        );
        AggregationResults<BookingScheduleRuleVo> aggregateResult = mongoTemplate.aggregate(agg, Schedule.class, BookingScheduleRuleVo.class);
        // scheduleVoList相当于按照时间分出很多的科室
        List<BookingScheduleRuleVo> scheduleVoList = aggregateResult.getMappedResults();
        //合并数据 map集合key日期value预约规则
        Map<Date, BookingScheduleRuleVo> scheduleVoMap = new HashMap<>();
        //TODO 完成后回来审视代码
        if (!CollectionUtils.isEmpty(scheduleVoList)) {
            scheduleVoMap = scheduleVoList.stream().collect(Collectors.toMap(BookingScheduleRuleVo::getWorkDate,
                    BookingScheduleRuleVo -> BookingScheduleRuleVo));
        }
        //获取可预约排班规则(把可预约规则和日期合并)dateList(7天的日期)
        List<BookingScheduleRuleVo> bookingScheduleRuleVoList = new ArrayList<>();
        for (int i = 0, len = dateList.size(); i < len; i++) {
            Date date = dateList.get(i);
            BookingScheduleRuleVo bookingScheduleRuleVo = scheduleVoMap.get(date);
            //如果当天没有科室
            if (bookingScheduleRuleVo == null){
                bookingScheduleRuleVo = new BookingScheduleRuleVo();
                //就诊医生人数
                bookingScheduleRuleVo.setDocCount(0);
                //科室剩余预约数 -1表示无号
                bookingScheduleRuleVo.setAvailableNumber(-1);
            }
            //新建的设置date，value里面放了key
            bookingScheduleRuleVo.setWorkDate(date);
            bookingScheduleRuleVo.setWorkDateMd(date);
            //计算当前预约日期对应星期
            String dayOfWeek = this.getDayOfWeek(new DateTime(date));
            bookingScheduleRuleVo.setDayOfWeek(dayOfWeek);
            //最后一页最后一条记录为即将预约 状态0:正常 1:即将放号 -1:当天已停止放号
            if (i == len - 1 && page == iPage.getPages()) {
                bookingScheduleRuleVo.setStatus(1);
            } else {
                bookingScheduleRuleVo.setStatus(0);
            }
            //当天预约如果过了停号时间，不能预约
            if (i == 0 && page == 1) {
                DateTime stopTime = this.getDateTime(new Date(), bookingRule.getStopTime());
                if (stopTime.isBeforeNow()) {
                    //停止预约
                    bookingScheduleRuleVo.setStatus(-1);
                }
            }
            bookingScheduleRuleVoList.add(bookingScheduleRuleVo);
        }
        //可预约日期规则数据
        result.put("bookingScheduleList", bookingScheduleRuleVoList);
        result.put("total", iPage.getTotal());
        //其他基础数据
        Map<String, String> baseMap = new HashMap<>();
        //医院名称
        baseMap.put("hosname", hospitalService.getHospName(hoscode));
        //科室
        Department department = departmentService.getDepartment(hoscode, depcode);
        //大科室名称
        baseMap.put("bigname", department.getBigname());
        //科室名称
        baseMap.put("depname", department.getDepname());
        //月
        baseMap.put("workDateString", new DateTime().toString("yyyy年MM月"));
        //放号时间
        baseMap.put("releaseTime", bookingRule.getReleaseTime());
        //停号时间
        baseMap.put("stopTime", bookingRule.getStopTime());
        result.put("baseMap", baseMap);
        return result;
    }

    /**
     * 通过排班id获取排班数据
     * @param scheduleId
     * @return
     */
    @Override
    public Schedule getScheduleId(String scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId).get();

        return this.packageSchedule(schedule);
    }

    /**
     * 根据排班id获取预约下单数据
     * @param scheduleId
     * @return
     */
    @Override
    public ScheduleOrderVo getScheduleOrderVo(String scheduleId) {

        ScheduleOrderVo scheduleOrderVo = new ScheduleOrderVo();
        //获取排班信息
        Schedule schedule = baseMapper.selectById(scheduleId);
        if (schedule == null) {
            throw new YyghException(ResultCodeEnum.PARAM_ERROR);
        }
        Hospital hospital = hospitalService.getByHoscode(schedule.getHoscode());
        if (hospital == null) {
            throw new YyghException(ResultCodeEnum.PARAM_ERROR);
        }
        BookingRule bookingRule = hospital.getBookingRule();
        if (bookingRule == null) {
            throw new YyghException(ResultCodeEnum.PARAM_ERROR);
        }
        scheduleOrderVo.setHoscode(schedule.getHoscode());
        scheduleOrderVo.setHosname(hospitalService.getHospName(schedule.getHoscode()));
        scheduleOrderVo.setDepcode(schedule.getDepcode());
        scheduleOrderVo.setDepname(departmentService.getDepName(schedule.getHoscode(), schedule.getDepcode()));
        scheduleOrderVo.setHosScheduleId(schedule.getHosScheduleId());
        scheduleOrderVo.setAvailableNumber(schedule.getAvailableNumber());
        scheduleOrderVo.setTitle(schedule.getTitle());
        scheduleOrderVo.setReserveDate(schedule.getWorkDate());
        scheduleOrderVo.setReserveTime(schedule.getWorkTime());
        scheduleOrderVo.setAmount(schedule.getAmount());
        //退号截止天数（如：就诊前一天为-1，当天为0）
        int quitDay = bookingRule.getQuitDay();
        DateTime quitTime = this.getDateTime(new DateTime(schedule.getWorkDate()).plusDays(quitDay).toDate(), bookingRule.getQuitTime());
        scheduleOrderVo.setQuitTime(quitTime.toDate());

        //预约开始时间
        DateTime startTime = this.getDateTime(new Date(), bookingRule.getReleaseTime());
        scheduleOrderVo.setStartTime(startTime.toDate());

        //预约截止时间
        DateTime endTime = this.getDateTime(new DateTime().plusDays(bookingRule.getCycle()).toDate(), bookingRule.getStopTime());
        scheduleOrderVo.setEndTime(endTime.toDate());

        //当天停止挂号时间
        DateTime stopTime = this.getDateTime(new Date(), bookingRule.getStopTime());
        scheduleOrderVo.setStartTime(stopTime.toDate());
        return scheduleOrderVo;
    }

    /**
     * 更新排班信息 用于mq
     * @param schedule
     */
    @Override
    public void update(Schedule schedule) {
        schedule.setUpdateTime(new Date());
        scheduleRepository.save(schedule);
    }

    /**
     * 获取可预约日期分页数据
     * dateList->总的可预约日期的集合，pageDateList->当前页可预约天数的集合，
     * start->当前页的首个天数的索引，end-1->当前页最后一个天数的索引
     * 当前页iPage对象若要包含位置正确的各个天数对象（比如：属于第二页第二个位置的应该是哪个Date对象），
     * 需要借助start与end来从dataList中复制对应位置的Date对象到自己的集合中
     *
     * @param page
     * @param limit
     * @param bookingRule
     * @return
     */
    private IPage getListDate(int page, int limit, BookingRule bookingRule) {
        //获取当天放号时间
        DateTime releaseTime = this.getDateTime(new Date(), bookingRule.getReleaseTime());
        //获取预约周期
        Integer cycle = bookingRule.getCycle();
        //如果当天放号时间已经过去，预约周期从后一天开始计算，周期+1
        if (releaseTime.isBeforeNow()) {
            cycle += 1;
        }
        //获取可预约的所有日期，最后一天显示即将放号
        List<Date> dateList = new ArrayList<>();
        for (int i = 0; i < cycle; i++) {
            DateTime curDateTime = new DateTime().plusDays(i);
            String dataString = curDateTime.toString("yyyy-MM-dd");
            dateList.add(new DateTime(dataString).toDate());
        }
        //超过7天进行分页
        List<Date> pageDateList = new ArrayList<>();
        int start = (page - 1) * limit;
        int end = page * limit;
        //如果没有超过本页最后一行记录数，则end就等于list最大值
        if (end > dateList.size()) {
            end = dateList.size();
        }
        //从总集合中得到当前页时间的集合
        for (int i = start; i < end; i++) {
            pageDateList.add(dateList.get(i));
        }
        // 如果大于7,进行分页，当前页iPage对象若要包含位置正确的各个天数对象（比如：属于第二页第二个位置的应该是哪个Date对象），
        // 需要借助start与end来从dataList中复制对应位置的Date对象到自己的集合中
        IPage<Date> iPage = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page, limit, dateList.size());
        //设置本页的几条记录
        iPage.setRecords(pageDateList);
        return iPage;
    }

    /**
     * 将日期转换未DateTime
     *
     * @param date
     * @param timeString
     * @return
     */
    private DateTime getDateTime(Date date, String timeString) {
        String dataTimeString = new DateTime(date).toString("yyyy-MM-dd") + " " + timeString;
        DateTime dateTime = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm").parseDateTime(dataTimeString);
        return dateTime;
    }


    /**
     * 额外排班信息封装
     *
     * @param schedule
     */
    private Schedule packageSchedule(Schedule schedule) {
        schedule.getParam().put("hosname", hospitalService.getHospName(schedule.getHoscode()));
        schedule.getParam().put("depname", departmentService.getDepName(schedule.getHoscode(), schedule.getDepcode()));
        schedule.getParam().put("dayOfWeek", this.getDayOfWeek(new DateTime(schedule.getWorkDate())));
        return schedule;
    }

    /**
     * 根据日期获取周几数据
     *
     * @param dateTime
     * @return
     */
    private String getDayOfWeek(DateTime dateTime) {
        String dayOfWeek = "";
        switch (dateTime.getDayOfWeek()) {
            case DateTimeConstants.SUNDAY:
                dayOfWeek = "周日";
                break;
            case DateTimeConstants.MONDAY:
                dayOfWeek = "周一";
                break;
            case DateTimeConstants.TUESDAY:
                dayOfWeek = "周二";
                break;
            case DateTimeConstants.WEDNESDAY:
                dayOfWeek = "周三";
                break;
            case DateTimeConstants.THURSDAY:
                dayOfWeek = "周四";
                break;
            case DateTimeConstants.FRIDAY:
                dayOfWeek = "周五";
                break;
            case DateTimeConstants.SATURDAY:
                dayOfWeek = "周六";
            default:
                break;
        }
        return dayOfWeek;
    }

}
