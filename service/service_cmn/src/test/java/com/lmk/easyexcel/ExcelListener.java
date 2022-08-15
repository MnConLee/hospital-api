package com.lmk.easyexcel;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;

import java.util.Map;

/**
 * @author 李明康
 * @create 2022/8/14 23:44
 */
public class ExcelListener extends AnalysisEventListener<UserData> {

    /**
     * 读取每行excel内容，从第二行开始读取
     * @param userData
     * @param analysisContext
     */
    @Override
    public void invoke(UserData userData, AnalysisContext analysisContext) {
        System.out.println(userData);
    }

    /**
     * 读取第一行内容（也就是表头）
     * @param headMap
     * @param context
     */
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        System.out.println("表头" + headMap);
    }

    /**
     * 读取之后执行
     * @param analysisContext
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }
}
