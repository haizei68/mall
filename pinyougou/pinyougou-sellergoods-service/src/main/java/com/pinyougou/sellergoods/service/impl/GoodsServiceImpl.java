package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.mapper.*;
import com.pinyougou.model.*;
import com.pinyougou.sellergoods.service.GoodsService;
import com.sun.org.apache.bcel.internal.generic.NEW;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private GoodsMapper goodsMapper;


    /**
     * 返回Goods全部列表
     *
     * @return
     */
    @Override
    public List<Goods> getAll() {
        return goodsMapper.selectAll();
    }


    /***
     * 分页返回Goods列表
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public PageInfo<Goods> getAll(Goods goods, int pageNum, int pageSize) {
        //执行分页
        PageHelper.startPage(pageNum, pageSize);

        //执行查询
        // List<Goods> all = goodsMapper.select(goods);

        Example example = new Example(Goods.class);
        Example.Criteria criteria = example.createCriteria();
        if (goods != null) {
            //查询商家自己的商品
            if (StringUtils.isNotBlank(goods.getSellerId())) {
                criteria.andEqualTo("sellerId", goods.getSellerId());
            }

            //根据状态查询
            if (StringUtils.isNotBlank(goods.getAuditStatus())) {
                criteria.andEqualTo("auditStatus", goods.getAuditStatus());
            }

            //根据商品名称模糊查询
            if (StringUtils.isNotBlank(goods.getGoodsName())) {
                criteria.andLike("goodsName", "%" + goods.getGoodsName() + "%");
            }

        }

        //查询没有删除的数据
        criteria.andIsNull("isDelete");

        //执行条件 查询
        List<Goods> all = goodsMapper.selectByExample(example);

        PageInfo<Goods> pageInfo = new PageInfo<Goods>(all);
        return pageInfo;
    }

    @Autowired
    private GoodsDescMapper goodsDescMapper;

    @Autowired
    private BrandMapper brandMapper;
    @Autowired
    private SellerMapper sellerMapper;
    @Autowired
    private ItemCatMapper itemCatMapper;


    @Autowired
    private ItemMapper itemMapper;

    /***
     * 增加Goods信息
     * @param goods
     * @return
     */
    @Override
    public int add(Goods goods) {
        //增加tb_goods
        int acount = goodsMapper.insertSelective(goods);

        //增加tb_goods_desc
        //@GeneratedValue(strategy = GenerationType.IDENTITY)   用于获取主键自增值
        GoodsDesc goodsDesc = goods.getGoodsDesc();

        goodsDesc.setGoodsId(goods.getId());
        goodsDescMapper.insertSelective(goodsDesc);

        //增加
        addItems(goods);


        return acount;
    }

    /**
     * 添加商品
     *
     * @param goods
     */
    public void addItems(Goods goods) {
        //获取品牌信息
        Brand brand = brandMapper.selectByPrimaryKey(goods.getBrandId());
        //获取商家信息
        Seller seller = sellerMapper.selectByPrimaryKey(goods.getSellerId());
        //商品分类-三级分类
        ItemCat itemCat = itemCatMapper.selectByPrimaryKey(goods.getCategory3Id());


        //当前时间
        Date now = new Date();

        if ("1".equals(goods.getIsEnableSpec())) {

            //添加SKU item
            for (Item item : goods.getItems()) {
                //获取SPU名字
                String goodsName = goods.getGoodsName();

                String title = goodsName;

                //Item的规格信息：{"机身内存":"16G","网络":"联通3G"}
                Map<String, String> specMap = JSON.parseObject(item.getSpec(), Map.class);
                for (Map.Entry<String, String> entry : specMap.entrySet()) {
                    title += "   " + entry.getValue();
                }
                item.setTitle(title);
                //初始化商品数据
                initItemParameter(goods, brand, seller, itemCat, now, item);

                //实现增加操作
                itemMapper.insertSelective(item);
            }
        } else {
            Item item = new Item();
            //创建一个默认商品
            //获取SPU名字
            String goodsName = goods.getGoodsName();

            item.setTitle(goodsName);

            //设置叶子目录
            initItemParameter(goods, brand, seller, itemCat, now, item);

            //设置默认值
            item.setPrice(goods.getPrice());

            //设置状态
            item.setStatus("1");

            item.setNum(1);

            //实现增加操作
            itemMapper.insertSelective(item);
        }



    }

    //初始化相关item数据
    public void initItemParameter(Goods goods, Brand brand, Seller seller, ItemCat itemCat, Date now, Item item) {

        //图片设置，取GoodsDesc图片的第一张图片
        //[{"color":"黑色","url":"http://192.168.211.128/group1/M00/00/00/wKjTgFqiP5qAA12EAAJqfY9HAUc368.png"},{"color":"白色","url":"http://192.168.211.128/group1/M00/00/00/wKjTgFqiP6SATg1cAAEIWeHu_jQ337.jpg"}]
        List<Map> imageMaps = JSON.parseArray(goods.getGoodsDesc().getItemImages(), Map.class);
        String imageurl = imageMaps.get(0).get("url").toString();
        item.setImage(imageurl);

        //设置叶子目录
        item.setCategoryid(goods.getCategory3Id());
        //时间
        item.setCreateTime(now);
        item.setUpdateTime(now);
        //设置goodsid
        item.setGoodsId(goods.getId());
        //设置sellerID
        item.setSellerId(goods.getSellerId());
        //设置brand
        item.setBrand(brand.getName());
        //设置seller
        item.setSeller(seller.getName());
        //设置category
        item.setCategory(itemCat.getName());
    }


    /***
     * 根据ID查询Goods信息
     * @param id
     * @return
     */
    @Override
    public Goods getOneById(Long id) {
        //查询商品信息
        Goods goods = goodsMapper.selectByPrimaryKey(id);

        //查询商品描述信息
        GoodsDesc goodsDesc = goodsDescMapper.selectByPrimaryKey(id);
        goods.setGoodsDesc(goodsDesc);


        //查询ITems
        //select * from tb_item where goodsid=?
        Item item = new Item();
        item.setGoodsId(id);
        List<Item> items = itemMapper.select(item);
        goods.setItems(items);
        return goods;
    }


    /***
     * 根据ID修改Goods信息
     * @param goods
     * @return
     */
    @Override
    public int updateGoodsById(Goods goods) {
        //修改goods
        goods.setAuditStatus("0");//未审核
        int mcount = goodsMapper.updateByPrimaryKeySelective(goods);

        //修改goodsdesc
        goodsDescMapper.updateByPrimaryKeySelective(goods.getGoodsDesc());

        //删除items  delete from tb_items where goodsid=N;
        Item item = new Item();
        item.setGoodsId(goods.getId());
        itemMapper.delete(item);

        //批量增加行的items
        addItems(goods);

        return mcount;
    }


    /***
     * 根据ID批量删除Goods信息
     * @param ids
     * @return
     */
    @Override
    public int deleteByIds(List<Long> ids) {
        //创建Example，来构建根据ID删除数据
        Example example = new Example(Goods.class);
        Example.Criteria criteria = example.createCriteria();

        //所需的SQL语句类似 delete from tb_goods where id in(1,2,5,6)
        criteria.andIn("id", ids);

        //update tb_goods set is delete=1 where id in(xx,xx)
        //is_delete=null  表示没有删除
        Goods goods = new Goods();
        goods.setIsDelete("1");//删除状态

        //删除操作
        return goodsMapper.updateByExampleSelective(goods,example);
    }

    /**
     * 修改商品状态
     * @param ids
     * @param status
     * @return
     */
    @Override
    public int updateStatus(List<Long> ids, String status) {
        //update tb_goods set auditStatus=status where id in(?,?,?)
        Example example = new Example(Goods.class);
        Example.Criteria criteria = example.createCriteria();

        //创建修改条件
        criteria.andIn("id",ids);

        //构建修改数据
        Goods goods = new Goods();
        goods.setAuditStatus(status);

        return goodsMapper.updateByExampleSelective(goods,example);
    }

    @Override
    public List<Item> getItemByGoodsIds(List<Long> ids, String status) {
        //select * from tb_item where goodsid in(123,3443,435) and status=1;
        Example example = new Example(Item.class);
        Example.Criteria criteria = example.createCriteria();

        //条件实现
        criteria.andIn("goodsId",ids);
        criteria.andEqualTo("status",status);

        return itemMapper.selectByExample(example);
    }
}
