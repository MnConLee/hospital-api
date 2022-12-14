package com.lmk.yygh.order.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lmk.common.rabbit.constant.MqConst;
import com.lmk.common.rabbit.service.RabbitService;
import com.lmk.yygh.common.exception.YyghException;
import com.lmk.yygh.common.helper.HttpRequestHelper;
import com.lmk.yygh.common.result.ResultCodeEnum;
import com.lmk.yygh.common.utils.MD5;
import com.lmk.yygh.enums.OrderStatusEnum;
import com.lmk.yygh.hosp.client.HospitalFeignClient;
import com.lmk.yygh.model.order.OrderInfo;
import com.lmk.yygh.model.user.Patient;
import com.lmk.yygh.order.mapper.OrderMapper;
import com.lmk.yygh.order.service.OrderService;
import com.lmk.yygh.order.service.WeixinService;
import com.lmk.yygh.user.client.PatientFeignClient;
import com.lmk.yygh.vo.hosp.ScheduleOrderVo;
import com.lmk.yygh.vo.msm.MsmVo;
import com.lmk.yygh.vo.order.*;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 李明康
 * @create 2022/8/29 0:09
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, OrderInfo> implements OrderService {
    @Autowired
    private PatientFeignClient patientFeignClient;
    @Autowired
    private HospitalFeignClient hospitalFeignClient;
    @Autowired
    private RabbitService rabbitService;
    @Autowired
    private WeixinService weixinService;
    /**
     * 获取订单信息
     * @param orderId
     * @return
     */
    @Override
    public OrderInfo getOrder(String orderId) {
        OrderInfo orderInfo = baseMapper.selectById(orderId);
        return this.packOrderInfo(orderInfo);
    }


    /**
     * 封装orderInfo(status转)
     * @param orderInfo
     * @return
     */
    private OrderInfo packOrderInfo(OrderInfo orderInfo) {
        orderInfo.getParam().put("orderStatusString", OrderStatusEnum.getStatusNameByStatus(orderInfo.getOrderStatus()));
        return orderInfo;
    }

    /**
     * 生成挂号订单
     * @param scheduleId
     * @param patientId
     * @return
     */
    @Override
    public Long saveOrder(String scheduleId, Long patientId) {
        //获取就诊人信息
        Patient patient = patientFeignClient.getPatientOrder(patientId);

        //获取排班相关信息
        ScheduleOrderVo scheduleOrderVo = hospitalFeignClient.getScheduleOrderVo(scheduleId);

        //TODO 时间处理问题
        //判断当前时间是否还可以预约
        // if (new DateTime(scheduleOrderVo.getStartTime()).isAfterNow()
        //         || new DateTime(scheduleOrderVo.getEndTime()).isBeforeNow()) {
        //     throw new YyghException(ResultCodeEnum.TIME_NO);
        // }

        //获取签名信息
        SignInfoVo signInfoVo = hospitalFeignClient.getSignInfoVo(scheduleOrderVo.getHoscode());

        //添加到订单表
        OrderInfo orderInfo = new OrderInfo();
        //scheduleOrderVo 数据复制到 orderInfo
        BeanUtils.copyProperties(scheduleOrderVo, orderInfo);
        //向orderInfo设置其他数据
        String outTradeNo = System.currentTimeMillis() + "" + new Random().nextInt(100);
        orderInfo.setOutTradeNo(outTradeNo);
        orderInfo.setScheduleId(scheduleOrderVo.getHosScheduleId());
        orderInfo.setUserId(patient.getUserId());
        orderInfo.setPatientId(patientId);
        orderInfo.setPatientName(patient.getName());
        orderInfo.setPatientPhone(patient.getPhone());
        orderInfo.setOrderStatus(OrderStatusEnum.UNPAID.getStatus());
        baseMapper.insert(orderInfo);

        //调用医院接口，实现预约挂号操作
        //设置调用医院接口需要参数，参数放到map集合
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("hoscode", orderInfo.getHoscode());
        paramMap.put("depcode", orderInfo.getDepcode());
        //是否写反？
        paramMap.put("hosScheduleId", orderInfo.getScheduleId());
        paramMap.put("reserveDate", new DateTime(orderInfo.getReserveDate()).toString("yyyy-MM-dd"));
        paramMap.put("reserveTime", orderInfo.getReserveTime());
        paramMap.put("amount", orderInfo.getAmount());

        paramMap.put("name", patient.getName());
        paramMap.put("certificatesType", patient.getCertificatesType());
        paramMap.put("certificatesNo", patient.getCertificatesNo());
        paramMap.put("sex", patient.getSex());
        paramMap.put("birthdate", patient.getBirthdate());
        paramMap.put("phone", patient.getPhone());
        paramMap.put("isMarry", patient.getIsMarry());
        paramMap.put("provinceCode", patient.getProvinceCode());
        paramMap.put("cityCode", patient.getCityCode());
        paramMap.put("districtCode", patient.getDistrictCode());
        paramMap.put("address", patient.getAddress());
        //联系人
        paramMap.put("contactsName", patient.getContactsName());
        paramMap.put("contactsCertificatesType", patient.getContactsCertificatesType());
        paramMap.put("contactsCertificatesNo", patient.getContactsCertificatesNo());
        paramMap.put("contactsPhone", patient.getContactsPhone());
        paramMap.put("timestamp", HttpRequestHelper.getTimestamp());

        String sign = HttpRequestHelper.getSign(paramMap, signInfoVo.getSignKey());
        paramMap.put("sign", sign);

        //请求医院系统接口
        JSONObject result = HttpRequestHelper.sendRequest(paramMap, signInfoVo.getApiUrl() + "/order/submitOrder");

        if (result.getInteger("code") == 200) {
            JSONObject jsonObject = result.getJSONObject("data");
            //预约记录唯一标识（医院预约记录主键）
            String hosRecordId = jsonObject.getString("hosRecordId");
            //预约序号
            Integer number = jsonObject.getInteger("number");
            ;
            //取号时间
            String fetchTime = jsonObject.getString("fetchTime");
            ;
            //取号地址
            String fetchAddress = jsonObject.getString("fetchAddress");
            ;
            //更新订单
            orderInfo.setHosRecordId(hosRecordId);
            orderInfo.setNumber(number);
            orderInfo.setFetchTime(fetchTime);
            orderInfo.setFetchAddress(fetchAddress);
            baseMapper.updateById(orderInfo);
            //排班可预约数
            Integer reservedNumber = jsonObject.getInteger("reservedNumber");
            //排班剩余预约数
            Integer availableNumber = jsonObject.getInteger("availableNumber");
            //发送mq消息，号源更新和短信通知
            //发送mq信息更新号源
            OrderMqVo orderMqVo = new OrderMqVo();
            orderMqVo.setScheduleId(scheduleId);
            orderMqVo.setReservedNumber(reservedNumber);
            orderMqVo.setAvailableNumber(availableNumber);
            //短信提示
            MsmVo msmVo = new MsmVo();
            msmVo.setPhone(orderInfo.getPatientPhone());
            String reserveDate = new DateTime(orderInfo.getReserveDate()).toString("yyyy-MM-dd") + (orderInfo.getReserveTime() == 0 ? "上午" : "下午");
            Map<String, Object> param = new HashMap<String, Object>() {{
                put("title", orderInfo.getHosname() + "|" + orderInfo.getDepname() + "|" + orderInfo.getTitle());
                put("amount", orderInfo.getAmount());
                put("reserveDate", reserveDate);
                put("name", orderInfo.getPatientName());
                put("quitTime", new DateTime(orderInfo.getQuitTime()).toString("yyyy-MM-dd HH:mm"));
                //由于企业账户才有模板，使用111111代替取消预约
                put("code", "111111");
            }};
            msmVo.setParam(param);
            orderMqVo.setMsmVo(msmVo);
            rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_ORDER, MqConst.ROUTING_ORDER, orderMqVo);
        } else {
            throw new YyghException(result.getString("message"), ResultCodeEnum.FAIL.getCode());
        }
        return orderInfo.getId();
    }

    /**
     * 查询订单列表
     * @param pageParam
     * @param orderQueryVo
     * @return
     */
    //TODO 需要增加userid，否则查询的是所有用户的订单
    @Override
    public IPage<OrderInfo> selectPage(Page<OrderInfo> pageParam, OrderQueryVo orderQueryVo) {
        //医院名称的模糊查询
        String name = orderQueryVo.getKeyword();
        Long patientId = orderQueryVo.getPatientId();
        String orderStatus = orderQueryVo.getOrderStatus();
        //安排时间
        String reserveDate = orderQueryVo.getReserveDate();
        String createTimeBegin = orderQueryVo.getCreateTimeBegin();
        String createTimeEnd = orderQueryVo.getCreateTimeEnd();
        QueryWrapper<OrderInfo> wrapper = new QueryWrapper<>();
        if (!StringUtils.isEmpty(name)) {
            wrapper.like("hosname", name);
        }
        if (!StringUtils.isEmpty(patientId)) {
            wrapper.eq("patient_id", patientId);
        }
        if (!StringUtils.isEmpty(orderStatus)) {
            wrapper.eq("order_status", orderStatus);
        }
        //TODO 时间处理似乎出了点问题
        if (!StringUtils.isEmpty(reserveDate)) {
            wrapper.eq("reserve_date", reserveDate);
        }
        if (!StringUtils.isEmpty(createTimeBegin)) {
            wrapper.ge("create_time", createTimeBegin);
        }
        if (!StringUtils.isEmpty(createTimeEnd)) {
            wrapper.le("create_time", createTimeEnd);
        }
        IPage<OrderInfo> pages = baseMapper.selectPage(pageParam, wrapper);
        pages.getRecords().stream().forEach(item -> {
            this.packOrderInfo(item);
        });
        return pages;
    }

    /**
     * 取消预约
     * @param orderId
     * @return
     */
    @Override
    public Boolean cancelOrder(Long orderId) {
        //获取订单信息
        OrderInfo orderInfo = baseMapper.selectById(orderId);
        //判断是否可以取消
        DateTime quitTime = new DateTime(orderInfo.getQuitTime());
        if (quitTime.isBeforeNow()) {
            throw new YyghException(ResultCodeEnum.CANCEL_ORDER_FAIL);
        }
        //调用医院接口
        SignInfoVo signInfoVo
                = hospitalFeignClient.getSignInfoVo(orderInfo.getHoscode());
        if(null == signInfoVo) {
            throw new YyghException(ResultCodeEnum.PARAM_ERROR);
        }
        Map<String, Object> reqMap = new HashMap<>();
        reqMap.put("hoscode",orderInfo.getHoscode());
        reqMap.put("hosRecordId",orderInfo.getHosRecordId());
        reqMap.put("timestamp", HttpRequestHelper.getTimestamp());
        String sign = MD5.encrypt(signInfoVo.getSignKey());
        reqMap.put("sign", sign);
        JSONObject result =
                HttpRequestHelper.sendRequest(reqMap, signInfoVo.getApiUrl() + "/order/updateCancelStatus");
        //根据医院接口返回数据
        if (result.getInteger("code") != 200) {
            throw new YyghException(result.getString("message"), ResultCodeEnum.FAIL.getCode());
        } else {
            //判断当前订单是否可以取消
            if (orderInfo.getOrderStatus().intValue() == OrderStatusEnum.PAID.getStatus().intValue()) {
                Boolean isRefund = weixinService.refund(orderId);
                if (!isRefund) {
                    throw new YyghException(ResultCodeEnum.CANCEL_ORDER_FAIL);
                }
                //更新订单状态
                orderInfo.setOrderStatus(OrderStatusEnum.CANCLE.getStatus());
                baseMapper.updateById(orderInfo);
                //TODO 需要让医院接口那边数量和预约平台一致，我这边只改一边
                //发送mq
                OrderMqVo orderMqVo = new OrderMqVo();
                orderMqVo.setScheduleId(orderInfo.getScheduleId());
                //短信提示
                MsmVo msmVo = new MsmVo();
                msmVo.setPhone(orderInfo.getPatientPhone());
                String reserveDate = new DateTime(orderInfo.getReserveDate()).toString("yyyy-MM-dd") + (orderInfo.getReserveTime()==0 ? "上午": "下午");
                Map<String,Object> param = new HashMap<String,Object>(){{
                    put("title", orderInfo.getHosname()+"|"+orderInfo.getDepname()+"|"+orderInfo.getTitle());
                    put("reserveDate", reserveDate);
                    put("name", orderInfo.getPatientName());
                    //由于企业账户才有模板，使用000000代替取消预约
                    put("code", "000000");
                }};
                msmVo.setParam(param);
                orderMqVo.setMsmVo(msmVo);
                rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_ORDER, MqConst.ROUTING_ORDER, orderMqVo);
                return true;
            }
        }
        return false;
    }

    /**
     * 就诊通知
     */
    @Override
    public void patientTips() {
        QueryWrapper<OrderInfo> wrapper = new QueryWrapper<>();
        //就诊当天发送
        wrapper.eq("reserve_date", new DateTime().toString("yyyy-MM-dd"));
        wrapper.ne("order_status", OrderStatusEnum.CANCLE.getStatus());
        List<OrderInfo> orderInfoList = baseMapper.selectList(wrapper);
            for (OrderInfo orderInfo: orderInfoList ) {
                //短信提示
                MsmVo msmVo = new MsmVo();
                msmVo.setPhone(orderInfo.getPatientPhone());
                String reserveDate = new DateTime(orderInfo.getReserveDate()).toString("yyyy-MM-dd") + (orderInfo.getReserveTime()==0 ? "上午": "下午");
                Map<String,Object> param = new HashMap<String,Object>(){{
                    put("title", orderInfo.getHosname()+"|"+orderInfo.getDepname()+"|"+orderInfo.getTitle());
                    put("reserveDate", reserveDate);
                    put("name", orderInfo.getPatientName());
                    //自己测试，设置了一个表示订单短信提醒的代码代表提醒
                    put("code", "222222");
                }};
                msmVo.setParam(param);
                rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_MSM, MqConst.ROUTING_MSM_ITEM, msmVo);
            }



    }

    /**
     * 预约统计
     * @param orderCountQueryVo
     * @return
     */
    @Override
    public Map<String, Object> getCountMap(OrderCountQueryVo orderCountQueryVo) {
        List<OrderCountVo> orderCountVoList = baseMapper.selectOrderCount(orderCountQueryVo);
        //获取x轴需要数据
        List<String> dateList = orderCountVoList.stream().map(OrderCountVo::getReserveDate).collect(Collectors.toList());
        //获取y轴需要数据
        List<Integer> countList = orderCountVoList.stream().map(OrderCountVo::getCount).collect(Collectors.toList());
        HashMap<String, Object> map = new HashMap<>();
        map.put("dateList", dateList);
        map.put("countList", countList);
        return map;
    }


}
