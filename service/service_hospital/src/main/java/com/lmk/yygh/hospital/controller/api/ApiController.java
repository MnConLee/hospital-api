package com.lmk.yygh.hospital.controller.api;

import com.lmk.yygh.common.exception.YyghException;
import com.lmk.yygh.common.helper.HttpRequestHelper;
import com.lmk.yygh.common.result.Result;
import com.lmk.yygh.common.result.ResultCodeEnum;
import com.lmk.yygh.common.utils.MD5;
import com.lmk.yygh.hospital.service.DepartmentService;
import com.lmk.yygh.hospital.service.HospitalService;
import com.lmk.yygh.hospital.service.HospitalSetService;
import com.lmk.yygh.model.hosp.Hospital;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author 李明康
 * @create 2022/8/21 14:25
 */
@RestController
@RequestMapping("/api/hosp")
public class ApiController {

    @Autowired
    private HospitalService hospitalService;
    @Autowired
    private HospitalSetService hospitalSetService;
    @Autowired
    private DepartmentService departmentService;

    /**
     * 上传医院接口
     * @param request
     * @return
     */
    @PostMapping("saveHospital")
    public Result saveHosp(HttpServletRequest request) {
        //获取传递过来医院信息
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(requestMap);
        //判断签名是否一致
        hospitalSetService.eqSign(paramMap);
        //传输过程中"+"转换成" "，需要转换回去
        String logoData = (String)paramMap.get("logoData");
        logoData = logoData.replaceAll(" ", "+");
        paramMap.put("logoData", logoData);
        //调用service的方法
        hospitalService.save(paramMap);
        return Result.ok();
    }

    /**
     * 查询医院信息
     * @param request
     * @return
     */
    @PostMapping("hospital/show")
    public Result getHospital(HttpServletRequest request) {
        //获取传递过来医院信息
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(requestMap);
        String hoscode = (String) paramMap.get("hoscode");
        //判断签名是否一致
        hospitalSetService.eqSign(paramMap);
        //医院编号查询
        Hospital hospital = hospitalService.getByHoscode(hoscode);
        return Result.ok(hospital);
    }

    @PostMapping("saveDepartment")
    public Result saveDepartment(HttpServletRequest request) {
        //获取传递过来科室信息
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(requestMap);
        hospitalSetService.eqSign(paramMap);
        departmentService.save(paramMap);
        return Result.ok();
    }
}
