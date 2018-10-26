package com.pinyougou.page.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.GoodsDescMapper;
import com.pinyougou.mapper.GoodsMapper;
import com.pinyougou.mapper.ItemMapper;
import com.pinyougou.model.Goods;
import com.pinyougou.model.GoodsDesc;
import com.pinyougou.model.Item;
import com.pinyougou.page.service.ItemPageService;
import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactory;
import tk.mybatis.mapper.entity.Example;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ItemPageServiceImpl implements ItemPageService {

    @Autowired
    FreeMarkerConfigurationFactory freeMarkerConfigurationFactory;

    //静态页路径
    @Value("${HTML_PATH}")
    private String HTML_PATH;

    //静态页后缀
    @Value("${HTML_SUFFIX}")
    private String HTML_SUFFIX;

    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private GoodsDescMapper goodsDescMapper;

    @Autowired
    private ItemMapper itemMapper;

    /**
     * 生成商品详情页
     * @param goodsId
     * @return
     * @throws Exception
     */
    @Override
    public boolean bulidHtml(Long goodsId) throws Exception {
        //Goods
        Goods goods = goodsMapper.selectByPrimaryKey(goodsId);
        //GoodsDesc
        GoodsDesc goodsDesc = goodsDescMapper.selectByPrimaryKey(goodsId);

        //查询List<Item>
        List<Item> items=skuList(goodsId);

        //List<Item>
        //获取Configuration对象
        Configuration configuration = freeMarkerConfigurationFactory.createConfiguration();

        //加载模板对象,生成商品详情页
        Template template = configuration.getTemplate("item.ftl");

        //数据模型
        Map<String,Object> dataMap=new HashMap<String, Object>();
        dataMap.put("goods",goods);
        dataMap.put("goodsDesc",goodsDesc);
        //将Item转成JSON
        dataMap.put("items", JSON.toJSONString(items));

        //指定输出对象
        //Writer writer =new FileWriter(HTML_PATH+goodsId+HTML_SUFFIX);
        Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(HTML_PATH+goodsId+HTML_SUFFIX),"UTF-8"));
        //输出
        template.process(dataMap,writer);

        //关闭资源
        writer.flush();
        writer.close();;

        return true;
    }

    /**
     * 根据ID删除静态页
     * @param id
     */
    @Override
    public void deleteHtml(Long id) {
        //构建一个需要删除的文件
        File file = new File(HTML_PATH + id + HTML_SUFFIX);

        if (file.exists()){
            //如果文件存在,则删除
            file.delete();
        }
    }

    /**
     * 根据GoodsId查询Item
     * @param goodsid
     * @return
     */
    public List<Item> skuList(Long goodsid){
        Example example = new Example(Item.class);
        Example.Criteria criteria = example.createCriteria();

        //根据goodsid查询
        criteria.andEqualTo("goodsId",goodsid);

        //已上架
        criteria.andEqualTo("status","1");

        //默认选中某一个商品
        example.orderBy("isDefault").desc();
        //example.setOrderByClause("is_default desc");

        return itemMapper.selectByExample(example);
    }
}
