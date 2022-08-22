package com.lmk.yygh.hospital.controller.api;

import com.lmk.yygh.common.helper.HttpRequestHelper;
import com.lmk.yygh.common.result.Result;
import com.lmk.yygh.hospital.service.DepartmentService;
import com.lmk.yygh.hospital.service.HospitalService;
import com.lmk.yygh.hospital.service.HospitalSetService;
import com.lmk.yygh.hospital.service.ScheduleService;
import com.lmk.yygh.model.hosp.Department;
import com.lmk.yygh.model.hosp.Hospital;
import com.lmk.yygh.model.hosp.Schedule;
import com.lmk.yygh.vo.hosp.DepartmentQueryVo;
import com.lmk.yygh.vo.hosp.ScheduleQueryVo;
import javafx.geometry.Pos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;
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
    @Autowired
    private ScheduleService scheduleService;
    /**
     * 上传医院接口
     *
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
        String logoData = (String) paramMap.get("logoData");
        logoData = logoData.replaceAll(" ", "+");
        paramMap.put("logoData", logoData);
        //调用service的方法
        hospitalService.save(paramMap);
        return Result.ok();
    }

    /**
     * 查询医院信息
     *
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

    /**
     * 上传科室接口
     *
     * @param request
     * @return
     */
    @PostMapping("saveDepartment")
    public Result saveDepartment(HttpServletRequest request) {
        //获取传递过来科室信息
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(requestMap);
        hospitalSetService.eqSign(paramMap);
        departmentService.save(paramMap);
        return Result.ok();
    }

    /**
     * 查询科室接口
     *
     * @param request
     * @return
     */
    @PostMapping("department/list")
    public Result findDepartment(HttpServletRequest request) {
        //获取传递过来科室信息
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(requestMap);
        //获取医院编号
        String hoscode = (String) paramMap.get("hoscode");
        //当前页和每页记录数
        int page = StringUtils.isEmpty(paramMap.get("page"))
                ? 1 : Integer.parseInt((String) paramMap.get("page"));
        int limit = StringUtils.isEmpty(paramMap.get("limit"))
                ? 1 : Integer.parseInt((String) paramMap.get("limit"));
        hospitalSetService.eqSign(paramMap);
        DepartmentQueryVo departmentQueryVo = new DepartmentQueryVo();
        departmentQueryVo.setHoscode(hoscode);
        //调用service方法
        Page<Department> pageModel = departmentService.findPageDepartment(page, limit, departmentQueryVo);
        return Result.ok(pageModel);
    }

    /**
     * 删除科室接口
     * @param request
     * @return
     */
    @PostMapping("department/remove")
    public Result removeDepartment(HttpServletRequest request) {
        //获取传递过来科室信息
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(requestMap);
        String hoscode = (String) paramMap.get("hoscode");
        String depcode = (String) paramMap.get("depcode");
        hospitalSetService.eqSign(paramMap);

        departmentService.remove(hoscode, depcode);
        return Result.ok();
    }

    /**
     * 上传排班
     * @param request
     * @return
     */
    @PostMapping("saveSchedule")
    public Result saveSchedule(HttpServletRequest request) {
        //获取传递过来科室信息
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(requestMap);
        hospitalSetService.eqSign(paramMap);
        scheduleService.save(paramMap);
        return Result.ok();
    }

    /**
     * 查询排班接口
     * @param request
     * @return
     */
    @PostMapping("schedule/list")
    public Result findSchedule(HttpServletRequest request) {
        //获取传递过来科室信息
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(requestMap);
        //获取医院编号和科室编号
        String hoscode = (String) paramMap.get("hoscode");
        String depcode = (String) paramMap.get("depcode");
        hospitalSetService.eqSign(paramMap);
        //当前页和每页记录数
        int page = StringUtils.isEmpty(paramMap.get("page"))
                ? 1 : Integer.parseInt((String) paramMap.get("page"));
        int limit = StringUtils.isEmpty(paramMap.get("limit"))
                ? 1 : Integer.parseInt((String) paramMap.get("limit"));

        ScheduleQueryVo scheduleQueryVo = new ScheduleQueryVo();
        scheduleQueryVo.setHoscode(hoscode);
        //调用service方法
        Page<Schedule> pageModel = scheduleService.findPageSchedule(page, limit, scheduleQueryVo);
        return Result.ok(pageModel);
    }

    /**
     * 删除排班
     * @param request
     * @return
     */
    @PostMapping("schedule/remove")
    public Result remove(HttpServletRequest request) {
        //获取传递过来排班信息
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(requestMap);
        String hoscode = (String) paramMap.get("hoscode");
        String hosScheduleId = (String) paramMap.get("hosScheduleId");
        hospitalSetService.eqSign(paramMap);
        scheduleService.remove(hoscode, hosScheduleId);
        return Result.ok();
    }
}
