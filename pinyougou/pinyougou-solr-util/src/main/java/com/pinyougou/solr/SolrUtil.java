package com.pinyougou.solr;

import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.ItemMapper;
import com.pinyougou.model.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class SolrUtil {

    @Autowired
    private ItemMapper itemMapper;
    @Autowired
    private SolrTemplate solrTemplate;
    /**
     * 查询数据库数据
     * 批量导入到索引库
     */
    public void batchAdd(){
        //select * from tb_items where status=1
        //商品必须已经上架
        Item itemInfo = new Item();
        itemInfo.setStatus("1");

        //查询数据
        List<Item> items = itemMapper.select(itemInfo);

        //循环将item的spec转成Map
        for (Item item : items) {
            //将spec规格转成Map结构
            Map specMap = JSON.parseObject(item.getSpec(), Map.class);
            //设置动态域的值
            item.setSpecMap(specMap);
        }

        //将数据加入到索引库
        solrTemplate.saveBeans(items);

        //提交
        solrTemplate.commit();
    }

    /**
     * 删除所有
     */
    public void deleteAll(){
        //删除所有
        Query query=new SimpleQuery("*:*");
        solrTemplate.delete(query);

        solrTemplate.commit();
    }
}
