package com.lmk.easyexcel;

import com.alibaba.excel.EasyExcel;

/**
 * @author 李明康
 * @create 2022/8/14 23:53
 */
public class TestRead {
    public static void main(String[] args) {
        //读取文件路径
        String fileName = "F:\\javaworkspace\\hospital_excel\\01.xlsx";
        EasyExcel.read(fileName,UserData.class,new ExcelListener()).sheet().doRead();
    }
}
