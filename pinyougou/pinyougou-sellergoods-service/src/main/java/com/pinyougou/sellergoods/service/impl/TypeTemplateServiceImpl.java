package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.mapper.SpecificationOptionMapper;
import com.pinyougou.mapper.TypeTemplateMapper;
import com.pinyougou.model.SpecificationOption;
import com.pinyougou.model.TypeTemplate;
import com.pinyougou.sellergoods.service.TypeTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;

@Service
public class TypeTemplateServiceImpl implements TypeTemplateService {

    @Autowired
    private TypeTemplateMapper typeTemplateMapper;

	/**
	 * 返回TypeTemplate全部列表
	 * @return
	 */
	@Override
    public List<TypeTemplate> getAll(){
        return typeTemplateMapper.selectAll();
    }


    /***
     * 分页返回TypeTemplate列表
     * @param pageNum
     * @param pageSize
     * @return
     */
	@Override
    public PageInfo<TypeTemplate> getAll(TypeTemplate typeTemplate,int pageNum, int pageSize) {
        //执行分页
        PageHelper.startPage(pageNum,pageSize);
       
        //执行查询
        List<TypeTemplate> all = typeTemplateMapper.select(typeTemplate);
        PageInfo<TypeTemplate> pageInfo = new PageInfo<TypeTemplate>(all);

        //查询所有,实现品牌和规格添加到缓存
        refreshRedis();

        return pageInfo;
    }

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 查询所有模板,并刷新Redis中模板对应的品牌和规格缓存信息
     */
    public void refreshRedis(){
	    //所有模板
        List<TypeTemplate> typeTemplates = typeTemplateMapper.selectAll();

        //循环获取每个模板的品牌和规格信息
        for (TypeTemplate typeTemplate : typeTemplates) {
            //获取品牌
            String brandIds = typeTemplate.getBrandIds();

            //将品牌信息转成List<Map>存入缓存
            List<Map> brandList = JSON.parseArray(brandIds, Map.class);
            //key:模板ID value :品牌信息
            redisTemplate.boundHashOps("BrandList").put(typeTemplate.getId(),brandList);
            //获取规格信息
            List<Map> specOption = getSpecOptionByTypeId(typeTemplate.getId());
            //将规格信息存入到Redis中 key:模板ID  value:规格信息
            redisTemplate.boundHashOps("SpecList").put(typeTemplate.getId(),specOption);
        }
    }



    /***
     * 增加TypeTemplate信息
     * @param typeTemplate
     * @return
     */
    @Override
    public int add(TypeTemplate typeTemplate) {
        return typeTemplateMapper.insertSelective(typeTemplate);
    }


    /***
     * 根据ID查询TypeTemplate信息
     * @param id
     * @return
     */
    @Override
    public TypeTemplate getOneById(Long id) {
        return typeTemplateMapper.selectByPrimaryKey(id);
    }


    /***
     * 根据ID修改TypeTemplate信息
     * @param typeTemplate
     * @return
     */
    @Override
    public int updateTypeTemplateById(TypeTemplate typeTemplate) {
        return typeTemplateMapper.updateByPrimaryKeySelective(typeTemplate);
    }


    /***
     * 根据ID批量删除TypeTemplate信息
     * @param ids
     * @return
     */
    @Override
    public int deleteByIds(List<Long> ids) {
        //创建Example，来构建根据ID删除数据
        Example example = new Example(TypeTemplate.class);
        Example.Criteria criteria = example.createCriteria();

        //所需的SQL语句类似 delete from tb_typeTemplate where id in(1,2,5,6)
        criteria.andIn("id",ids);
        return typeTemplateMapper.deleteByExample(example);
    }

    @Autowired
    private SpecificationOptionMapper specificationOptionMapper;

    /**
     * 根据模板ID查询规格选项数据并封装JSON格式
     * @param id
     * @return
     */
    @Override
    public List<Map> getSpecOptionByTypeId(long id) {
        //根据模板ID查询规格数据
        TypeTemplate template = typeTemplateMapper.selectByPrimaryKey(id);

        String specIds = template.getSpecIds();

        List<Map> dataMap=JSON.parseArray(specIds,Map.class);

        //循环集合,根据规格的ID去规格选项表(tb_specification_option)中查询规格选线

        for (Map map : dataMap) {
            //规格ID
            long specId = Long.parseLong(map.get("id").toString());
            SpecificationOption specificationOption = new SpecificationOption();
            specificationOption.setSpecId(specId);
            List<SpecificationOption> options = specificationOptionMapper.select(specificationOption);
            System.out.println(options);

            //构造数据集合格式
            map.put("options",options);
        }
        return dataMap;
    }
}
