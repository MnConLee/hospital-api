package com.lmk.yygh.hospital.controller.api;

import com.lmk.yygh.common.result.Result;
import com.lmk.yygh.hospital.service.DepartmentService;
import com.lmk.yygh.hospital.service.HospitalService;
import com.lmk.yygh.model.hosp.Hospital;
import com.lmk.yygh.vo.hosp.DepartmentVo;
import com.lmk.yygh.vo.hosp.HospitalQueryVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author 李明康
 * @create 2022/8/24 14:35
 */
@RestController
@RequestMapping("/api/hosp/hospital")
public class HospApiController {
    @Autowired
    private HospitalService hospitalService;
    @Autowired
    private DepartmentService departmentService;
    /**
     * 查询医院列表
     *
     * @param page
     * @param limit
     * @param hospitalQueryVo
     * @return
     */
    @ApiOperation(value = "查询医院列表")
    @GetMapping("findHospList/{page}/{limit}")
    public Result findHospList(@PathVariable Integer page,
                               @PathVariable Integer limit,
                               HospitalQueryVo hospitalQueryVo) {
        Page<Hospital> hospitals = hospitalService.selectHospPage(page, limit, hospitalQueryVo);
        return Result.ok(hospitals);
    }

    /**
     * 根据医院名称查询
     * @param hosname
     * @return
     */
    @ApiOperation(value = "根据医院名称查询")
    @GetMapping("findByHosName/{hosname}")
    public Result findByHosName(@PathVariable String hosname) {
        List<Hospital> list = hospitalService.findByHosname(hosname);
        return Result.ok(list);
    }

    /**
     * 根据医院编号获取科室
     * @param hoscode
     * @return
     */
    @ApiOperation(value = "根据医院编号获取科室")
    @GetMapping("department/{hoscode}")
    public Result index(@PathVariable String hoscode){
        List<DepartmentVo> list = departmentService.findDeptTree(hoscode);
        return Result.ok(list);
    }

    /**
     * 根据医院编号获取医院预约挂号详情
     * @param hoscode
     * @return
     */
    @ApiOperation(value = "根据医院编号获取医院预约挂号详情")
    @GetMapping("findHospDetail/{hoscode}")
    public Result findHospDetailByhoscode(@PathVariable String hoscode) {
        Map<String, Object> map = hospitalService.findHospDetailByhoscode(hoscode);
        return Result.ok(map);
    }
}
