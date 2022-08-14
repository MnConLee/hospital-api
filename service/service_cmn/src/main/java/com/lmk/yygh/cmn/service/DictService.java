package com.lmk.yygh.cmn.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lmk.yygh.model.cmn.Dict;

import java.util.List;

/**
 * @author 李明康
 * @create 2022/8/14 15:25
 */
public interface DictService extends IService<Dict> {
    /**
     * 根据id查询子数据
     * @param id
     * @return
     */
    List<Dict> findChild(Long id);
}
