package com.pinyougou.content.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.content.service.ContentService;
import com.pinyougou.mapper.ContentMapper;
import com.pinyougou.model.Content;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class ContentServiceImpl implements ContentService {

    @Autowired
    private ContentMapper contentMapper;

    /**
     * 返回Content全部列表
     *
     * @return
     */
    @Override
    public List<Content> getAll() {
        return contentMapper.selectAll();
    }


    /***
     * 分页返回Content列表
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public PageInfo<Content> getAll(Content content, int pageNum, int pageSize) {
        //执行分页
        PageHelper.startPage(pageNum, pageSize);

        //执行查询
        List<Content> all = contentMapper.select(content);
        PageInfo<Content> pageInfo = new PageInfo<Content>(all);
        return pageInfo;
    }


    /***
     * 增加Content信息
     * @param content
     * @return
     */
    @Override
    public int add(Content content) {
        int acount= contentMapper.insertSelective(content);
        if (acount>0){
            //清空对应分类的缓存
            redisTemplate.boundHashOps("Content").delete(content.getCategoryId());
        }
        return  acount;
    }


    /***
     * 根据ID查询Content信息
     * @param id
     * @return
     */
    @Override
    public Content getOneById(Long id) {
        return contentMapper.selectByPrimaryKey(id);
    }


    /***
     * 根据ID修改Content信息
     * @param content
     * @return
     */
    @Override
    public int updateContentById(Content content) {
        int  mcount= contentMapper.updateByPrimaryKeySelective(content);
        if (mcount>0){
            //清空对应分类的缓存
            redisTemplate.boundHashOps("Content").delete(content.getCategoryId());
        }
        return mcount;
    }


    /***
     * 根据ID批量删除Content信息
     * @param ids
     * @return
     */
    @Override
    public int deleteByIds(List<Long> ids) {
        //查询所有对应的广告内容信息,并获取对应的categoryId
        //select * from tb_content where id in (x,xx)
        Example example1=new Example(Content.class);

        Example.Criteria criteria1 = example1.createCriteria();
        criteria1.andIn("id",ids);
        List<Content> contents = contentMapper.selectByExample(example1);

        //创建Example，来构建根据ID删除数据
        Example example = new Example(Content.class);
        Example.Criteria criteria = example.createCriteria();

        //所需的SQL语句类似 delete from tb_content where id in(1,2,5,6)
        criteria.andIn("id", ids);
        int dcount= contentMapper.deleteByExample(example);

        //更新缓存
        for (Content content : contents) {
            redisTemplate.boundHashOps("Content").delete(content.getCategoryId());
        }

        return dcount;
    }

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 根据分类ID查询广告信息
     *
     * @param categoryId
     * @return
     */
    @Override
    public List<Content> findByCategoryId(long categoryId) {
        //第2此查询缓存数据库中是否存在对应数据
        Object result = redisTemplate.boundHashOps("Content").get(categoryId);
        //缓存中有数据,则直接将缓存数据返回,否则将数据库流程查询
        if (result != null) {
            return (List<Content>) result;

        }

        Example example = new Example(Content.class);
        Example.Criteria criteria = example.createCriteria();

        //select * from  tb_content where  category_id=1 and  status=1 order by sort_order desc
        //根据分类查询
        criteria.andEqualTo("categoryId", categoryId);

        //广告有效
        criteria.andEqualTo("status", "1");

        //排序
        example.setOrderByClause("sort_order desc");

        List<Content> contents = contentMapper.selectByExample(example);

        //第一次查询数据库
        if (contents != null && contents.size() > 0) {
            redisTemplate.boundHashOps("Content").put(categoryId, contents);
        }

        return contents;
    }
}
