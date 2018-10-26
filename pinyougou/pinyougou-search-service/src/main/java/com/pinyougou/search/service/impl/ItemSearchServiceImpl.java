package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.pinyougou.model.Item;
import com.pinyougou.search.service.ItemSearchService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ItemSearchServiceImpl implements ItemSearchService {

    @Autowired
    private SolrTemplate solrTemplate;

    /**
     * 商品搜索实现
     *
     * @param searchMap
     * @return
     */
    @Override
    public Map<String, Object> search(Map searchMap) {
        //搜索实现,没有关键词就搜索全部,否则搜索对应关键词数据
        //Query query = new SimpleQuery("*:*");
        HighlightQuery query = new SimpleHighlightQuery(new SimpleStringCriteria("*:*"));

        //高亮设置
        hightlightSettings(query);

        if (searchMap != null) {
            //关键词的key keyword
            String keyword = (String) searchMap.get("keyword");

            //关键词搜索
            if (StringUtils.isNotBlank(keyword)) {

                Criteria criteria = new Criteria("item_keywords").is(keyword.replace(" ", ""));

                query.addCriteria(criteria);
            }

            //分类过滤
            //获取分类
            String category = (String) searchMap.get("category");
            if (StringUtils.isNotBlank(category)) {
                //创建Criteria过滤条件
                Criteria criteria = new Criteria("item_category").is(category);

                //创建过滤查询对象
                FilterQuery filterQuery = new SimpleFilterQuery();
                filterQuery.addCriteria(criteria);

                //将过滤查询对象添加到Query中
                query.addFilterQuery(filterQuery);
            }

            //获取品牌实现品牌过滤
            String brand = (String) searchMap.get("brand");
            if (StringUtils.isNotBlank(brand)) {
                //创建Criteria对象,封装过滤条件
                Criteria criteria = new Criteria("item_brand").contains(brand);
                //创建过滤查询对象
                FilterQuery filterQuery = new SimpleFilterQuery(criteria);
                //将过滤查询对象添加到query中
                query.addFilterQuery(filterQuery);

            }

            //获取规格过滤
            Object spec = searchMap.get("spec");
            if (spec != null) {
                //将搜索的规格条件转成Map
                Map<String, String> specMap = JSON.parseObject(spec.toString(), Map.class);

                for (Map.Entry<String, String> entry : specMap.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();

                    //构建过滤
                    Criteria criteria = new Criteria("item_spec_" + key).is(value);

                    //创建过滤查询对象
                    FilterQuery filterQuery = new SimpleFilterQuery(criteria);

                    //将过滤查询对象添加到Query中
                    query.addFilterQuery(filterQuery);
                }
            }

            //价格区间
            //0-500  500-1000
            String price = (String) searchMap.get("price");
            if (StringUtils.isNotBlank(price)) {
                //创建过滤查询对象
                FilterQuery filterQuery = new SimpleFilterQuery();
                String[] priceRangs = price.split("-");
                if (priceRangs.length == 2) {
                    //x=<price<y
                    Criteria criteria = new Criteria("item_price")
                            .between(Long.parseLong(priceRangs[0]),   //最小值
                                    Long.parseLong(priceRangs[1]),    //最大值
                                    true,        //是否包含最小值
                                    false);     //是否包含最大值
                    //将criteria添加到FilterQuery中
                    filterQuery.addCriteria(criteria);
                }

                priceRangs = price.split(" ");
                if (priceRangs.length == 2) {
                    Criteria criteria = new Criteria("item_price").greaterThanEqual(Long.parseLong(priceRangs[0]));
                    //将criteria添加到FilterQuery中
                    filterQuery.addCriteria(criteria);
                }

                query.addFilterQuery(filterQuery);
            }

            //分页
            Integer pageNum = (Integer) searchMap.get("pageNum");
            Integer size = (Integer) searchMap.get("size");

            if (pageNum == null) {
                size = 1;
            }
            if (size == null) {
                size = 10;

            }
            query.setOffset((pageNum - 1) * size); //从下标为0的数据开始
            query.setRows(size);  //每页最多显示30条数据
        }


        //排序操作
        String sort = (String) searchMap.get("sort");
        String sortField = (String) searchMap.get("sortField");

        //排序实现
        if (StringUtils.isNotBlank(sort) && StringUtils.isNotBlank(sortField)) {
            Sort orders = null;
            //升序 sort=ASC
            if (sort.equalsIgnoreCase("asc")) {
                orders = new Sort(Sort.Direction.ASC, sortField);
            } else {
                //降序 sort=DESC
                orders = new Sort(Sort.Direction.DESC, sortField);
            }
            query.addSort(orders);
        }


        //实现搜索
        //ScoredPage<Item> scoredPage = solrTemplate.queryForPage(query, Item.class);
        //搜索采用高亮搜索
        HighlightPage<Item> scoredPage = solrTemplate.queryForHighlightPage(query, Item.class);

        //高亮替换
        highlightReplace(scoredPage);

        //获取总记录数
        long totalElements = scoredPage.getTotalElements();

        //getContent获取集合数据
        List<Item> items = scoredPage.getContent();

        //查询分类信息
        List<String> categoryList = getCategoryList(query);

        //用于封装数据
        Map<String, Object> dataMap = new HashMap<String, Object>();

        //如果前段提供了分类，则根据分类名字确定模板ID，也就是确定品牌和规格
        String category = (String) searchMap.get("category");
        if (StringUtils.isNotBlank(category)) {
            dataMap.putAll(getBrandAndSpec(category));
        } else {
            //设置一个默认分类，默认分类是第1个分类
            if (categoryList != null && categoryList.size() > 0) {
                dataMap.putAll(getBrandAndSpec(categoryList.get(0)));
            }
        }

        dataMap.put("rows", items);
        dataMap.put("categoryList", categoryList);
        dataMap.put("total", totalElements);

        return dataMap;
    }

    @Override
    public void importList(List<Item> items) {
        solrTemplate.saveBeans(items);
        solrTemplate.commit();
    }

    /**
     * 根据GoodsId删除对应的Item信息
     * @param ids
     */
    @Override
    public void deleteByGoodsIds(List<Long> ids) {
        //delete from table where goodsid in(xxx)
        Criteria criteria = new Criteria("item_goodsid").in(ids);
        Query query=new SimpleQuery(criteria);
        solrTemplate.delete(query);
        solrTemplate.commit();;
    }

    @Autowired
    private RedisTemplate redisTemplate;

    /***
     * 根据分类名字获取模板ID
     * 根据模板ID获取品牌和规格信息
     * @param category
     */
    public Map<String, Object> getBrandAndSpec(String category) {
        //获取模板ID
        Long typeTemplateId = (Long) redisTemplate.boundHashOps("ItemCat").get(category);

        //用于存储规格和品牌信息
        Map<String, Object> dataMap = new HashMap<String, Object>();

        if (typeTemplateId != null) {
            //根据模板ID获取品牌规格信息
            //根据模板ID获取品牌信息
            List<Map> brandList = (List<Map>) redisTemplate.boundHashOps("BrandList").get(typeTemplateId);

            //根据模板ID获取规格信息
            List<Map> specList = (List<Map>) redisTemplate.boundHashOps("SpecList").get(typeTemplateId);

            //将规格和品牌存入Map中
            dataMap.put("brandList", brandList);
            dataMap.put("specList", specList);
        }
        return dataMap;
    }

    /**
     * 分类分组查询
     *
     * @param query
     * @return
     */
    public List<String> getCategoryList(HighlightQuery query) {
        //重置分页
        query.setOffset(0);
        query.setRows(100);

        //搜索之前做分类分组查询
        //创建分组选项
        GroupOptions groupOptions = new GroupOptions();
        //指定分组的域
        groupOptions.addGroupByField("item_category");

        //绑定分组查询
        query.setGroupOptions(groupOptions);

        //实现分组数据查询
        GroupPage<Item> groupPage = solrTemplate.queryForGroupPage(query, Item.class);

        //获取指定域的分组结果数据
        GroupResult<Item> item_category = groupPage.getGroupResult("item_category");
        //循环获取每一条记录,每条记录都是键值对类型
        List<String> categoryList = new ArrayList<String>();

        for (GroupEntry<Item> itemGroupEntry : item_category.getGroupEntries()) {
            //值:域的值
            categoryList.add(itemGroupEntry.getGroupValue());
        }
        return categoryList;
    }

    /**
     * 高亮替换
     *
     * @param scoredPage
     */
    public void highlightReplace(HighlightPage<Item> scoredPage) {
        //获取所有数据 所有数据,拥有高亮和非高亮
        List<HighlightEntry<Item>> highlighted = scoredPage.getHighlighted();

        //循环所有数据
        for (HighlightEntry<Item> itemHighlightEntry : highlighted) {
            //获取非高亮数据
            Item item = itemHighlightEntry.getEntity();

            //获取高亮值
            List<HighlightEntry.Highlight> highlights = itemHighlightEntry.getHighlights();

            //将非高亮替换成高亮
            if (highlights != null && highlights.size() > 0) {
                //普通高亮值
                HighlightEntry.Highlight highlight = highlights.get(0);
                //获取高亮碎片
                List<String> snipplets = highlight.getSnipplets();

                if (snipplets != null && snipplets.size() > 0) {
                    StringBuffer buffer = new StringBuffer();
                    for (String snipplet : snipplets) {
                        buffer.append(snipplet);
                    }
                    //将非高亮替换成高亮
                    item.setTitle(buffer.toString());
                }
            }
        }
    }

    /**
     * 高亮设置
     *
     * @param query
     */
    public void hightlightSettings(HighlightQuery query) {
        //创建高亮选项
        HighlightOptions highlightOptions = new HighlightOptions();

        //设置高亮域
        highlightOptions.addField("item_title");
        //设置前缀
        highlightOptions.setSimplePrefix("<span style=\"color:red;\">");
        //设置后缀
        highlightOptions.setSimplePostfix("</span>");

        //设置高亮配置
        query.setHighlightOptions(highlightOptions);
    }


    /**
     * 商品搜索实现((非高亮显示)
     *
     * @param searchMap
     * @return
     */
    public Map<String, Object> searchNotH1(Map searchMap) {
        //搜索实现,没有关键词就搜索全部,否则搜索对应关键词数据
        Query query = new SimpleQuery("*:*");

        if (searchMap != null) {
            //关键词的key keyword
            String keyword = (String) searchMap.get("keyword");

            //关键词搜索
            if (StringUtils.isNotBlank(keyword)) {

                Criteria criteria = new Criteria("item_keywords").is(keyword);

                query.addCriteria(criteria);
            }
        }

        //分页
        query.setOffset(0); //从下标为0的数据开始
        query.setRows(30);  //每页最多显示30条数据

        //实现搜索
        ScoredPage<Item> scoredPage = solrTemplate.queryForPage(query, Item.class);

        //获取总记录数
        long totalElements = scoredPage.getTotalElements();

        //getContent获取集合数据
        List<Item> items = scoredPage.getContent();

        //用于封装数据
        Map<String, Object> dataMap = new HashMap<String, Object>();

        dataMap.put("rows", items);
        dataMap.put("total", totalElements);

        return dataMap;
    }
}
