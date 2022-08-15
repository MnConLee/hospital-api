package com.lmk.easyexcel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * @author 李明康
 * @create 2022/8/14 20:04
 */
@Data
public class UserData {
    @ExcelProperty(value = "用户编号",index = 0)
    private int uid;
    @ExcelProperty(value = "用户名称",index = 1)
    private String username;
}
