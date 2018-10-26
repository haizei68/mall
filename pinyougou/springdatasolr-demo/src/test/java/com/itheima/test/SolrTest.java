package com.itheima.test;

import com.itheima.domain.Item;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.ScoredPage;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-solr.xml")
public class SolrTest {

    @Autowired
    private SolrTemplate solrTemplate;

    @Test
    public  void  testGetByCondtion(){
        //Query用于做查询条件的
        Query query=new SimpleQuery();

        //构建条件
        Criteria criteria = new Criteria("item_title").is("哈哈");

        //将构建的条件给Query对象
        query.addCriteria(criteria);

        //设置分页
        query.setOffset(0);  //从第几条开始
        query.setRows(3);   //每页显示10条

        //执行查询
        ScoredPage<Item> scoredPage = solrTemplate.queryForPage(query, Item.class);

        //获取所有结束集
        List<Item> items = scoredPage.getContent();

        for (Item item : items) {
            System.out.println(item);
        }
    }

    @Test
    public  void  getPage(){
        //Query用于做查询条件的
        Query query=new SimpleQuery("*:*");

        //设置分页
        query.setOffset(3);  //从第几条开始
        query.setRows(3);   //每页显示10条

        //执行查询
        ScoredPage<Item> scoredPage = solrTemplate.queryForPage(query, Item.class);

        //获取所有结束集
        List<Item> items = scoredPage.getContent();

        for (Item item : items) {
            System.out.println(item);
        }
    }

    @Test
    public void testDeleteAll(){
        solrTemplate.delete(new SimpleQuery("*:*"));
        solrTemplate.commit();
    }

    @Test
    public void testDeleteById(){
        solrTemplate.deleteById("104");
        solrTemplate.commit();
    }

    @Test
    public void testGetAll(){
        //查询所有
        Query query = new SimpleQuery("*:*");
        //查询所有,但默认有分页
        ScoredPage<Item> scoredPage = solrTemplate.queryForPage(query, Item.class);

        List<Item> items = scoredPage.getContent();
        for (Item item : items) {

            System.out.println(item);
        }
    }


    /**
     * 根据ID查询
     */
    @Test
    public void testGetById(){
        Item item = solrTemplate.getById(104L, Item.class);
        System.out.println(item);
    }
    
    
    /**
     * 增加索引测试
     */
    @Test
    public  void testAdd(){
        Item item = new Item();
        item.setId(2L);
        item.setBrand("哈哈唐");
        item.setCategory("哈哈");
        item.setGoodsId(2L);
        item.setSeller("华为");
        item.setTitle("华为");
        item.setPrice(new BigDecimal(2000));

        //执行保存操作
        solrTemplate.saveBean(item);

        //提交
        solrTemplate.commit();
    }


    /**
     * 批量增加
     */
    @Test
    public  void testBatchAdd(){
        List<Item> items = new ArrayList<Item>();

        for (int i=0;i<20;i++){
            Item item = new Item();
            item.setId(100L+(int)(Math.random()*100));
            item.setBrand("哈哈唐");
            item.setCategory("哈哈");
            item.setGoodsId(1L);
            item.setSeller("华为");
            item.setTitle("哈哈");
            item.setPrice(new BigDecimal(10+(int)(Math.random()*100)));

            items.add(item);
        }

        //执行保存操作
            solrTemplate.saveBeans(items);


        //提交
        solrTemplate.commit();
    }

}
