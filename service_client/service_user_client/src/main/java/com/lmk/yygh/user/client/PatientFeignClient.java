package com.lmk.yygh.user.client;

import com.lmk.yygh.model.user.Patient;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author 李明康
 * @create 2022/8/29 0:32
 */
@FeignClient(value = "service-user",path = "/api/user/patient/")
@Repository
public interface PatientFeignClient {
    /**
     * 内部使用获取就诊人
     * @param id
     * @return
     */
    @ApiOperation(value = "获取就诊人")
    @GetMapping("inner/get/{id}")
    public Patient getPatientOrder(@PathVariable("id") Long id);
}
