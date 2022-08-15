package com.lmk.yygh.cmn.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.lmk.yygh.cmn.mapper.DictMapper;
import com.lmk.yygh.model.cmn.Dict;
import com.lmk.yygh.vo.cmn.DictEeVo;
import org.springframework.beans.BeanUtils;

/**
 * @author 李明康
 * @create 2022/8/15 14:41
 */
public class Dictlistener extends AnalysisEventListener<DictEeVo> {

    private DictMapper dictMapper;

    public Dictlistener(DictMapper dictMapper) {
        this.dictMapper = dictMapper;
    }
    @Override
    public void invoke(DictEeVo dictEeVo, AnalysisContext analysisContext) {
        Dict dict = new Dict();
        BeanUtils.copyProperties(dictEeVo, dict);
        dictMapper.insert(dict);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }
}
