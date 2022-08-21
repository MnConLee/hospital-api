package com.lmk.yygh.hospital.controller.api;

import com.lmk.yygh.common.helper.HttpRequestHelper;
import com.lmk.yygh.common.result.Result;
import com.lmk.yygh.hospital.service.HospitalService;
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
        //调用service的方法
        hospitalService.save(paramMap);
        return Result.ok();
    }
}
