package com.pinyougou.search.service;

import com.pinyougou.model.Item;

import java.util.List;
import java.util.Map;
public interface ItemSearchService {
    /**
     * 搜索方法
     */
    Map<String,Object> search(Map searchMap);

    /**
     * 批量将Item导入索引库
     * @param items
     */
    void importList(List<Item> items);

    /**
     * 根据GoodsId删除所有Item信息
     * @param ids
     */
    void deleteByGoodsIds(List<Long> ids);
}
