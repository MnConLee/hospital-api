package com.lmk.yygh.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lmk.yygh.model.order.OrderInfo;
import com.lmk.yygh.vo.order.OrderCountQueryVo;
import com.lmk.yygh.vo.order.OrderCountVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author 李明康
 * @create 2022/8/29 0:08
 */
public interface OrderMapper extends BaseMapper<OrderInfo> {
    //查询预约统计数据的方法
    List<OrderCountVo> selectOrderCount(@Param("vo") OrderCountQueryVo orderCountQueryVo);
}
