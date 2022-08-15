package com.lmk.easyexcel;

import com.alibaba.excel.EasyExcel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 李明康
 * @create 2022/8/14 20:06
 */
public class TestWrite {
    public static void main(String[] args) {
        //构建一个list集合
        List<UserData> list = new ArrayList();
        for (int i = 10; i > 0; i--) {
            UserData data = new UserData();
            data.setUid(i);
            data.setUsername("lucy" + i);
            list.add(data);
        }
        // 设置excel文件路径和文件名称
        String fileName = "F:\\javaworkspace\\hospital_excel\\01.xlsx";
        // 调用方法实现写操作
        EasyExcel.write(fileName, UserData.class).sheet("用户信息")
                .doWrite(list);
    }
}
