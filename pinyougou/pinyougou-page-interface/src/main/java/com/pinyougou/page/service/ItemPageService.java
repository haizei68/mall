package com.pinyougou.page.service;

public interface ItemPageService {

    /**
     * 根据商品的ID生成商品详情信息
     * @param goodsId
     * @return
     * @throws Exception
     */
    boolean bulidHtml(Long goodsId) throws Exception;

    /**
     * 根据ID删除静态页
     * @param id
     */
    void deleteHtml(Long id);
}
