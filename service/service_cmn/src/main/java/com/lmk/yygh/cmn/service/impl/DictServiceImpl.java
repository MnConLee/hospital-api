package com.lmk.yygh.cmn.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lmk.yygh.cmn.mapper.DictMapper;
import com.lmk.yygh.cmn.service.DictService;
import com.lmk.yygh.model.cmn.Dict;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 李明康
 * @create 2022/8/1 10:49
 */
@Service
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements DictService {

    /**
     * 根据id查询子数据
     * @param id
     * @return
     */
    @Override
    public List<Dict> findChild(Long id) {
        QueryWrapper<Dict> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_id", id);
        List<Dict> dictList = baseMapper.selectList(wrapper);
        for (Dict dict:dictList
             ) {
            Long dictId = dict.getId();
            boolean isChild = this.isChildren(dictId);
            dict.setHasChildren(isChild);
        }
        return dictList;
    }

    /**
     * 判断id下面是否有子节点
     * @param id
     * @return
     */
    private boolean isChildren(Long id) {
        QueryWrapper<Dict> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_id", id);
        Integer count = baseMapper.selectCount(wrapper);
        return count > 0;
    }
}
