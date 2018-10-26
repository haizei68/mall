package com.pinyougou.test;

import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.BrandMapper;
import com.pinyougou.model.Brand;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

public class MapperTest {

    private BrandMapper brandMapper;

    @Before
    public void init(){
        ApplicationContext act=new ClassPathXmlApplicationContext("spring/spring-mybatis.xml");
       brandMapper = act.getBean(BrandMapper.class);
    }


    /**
     * 使用通用Mapper实现品牌增加
     */
    @Test
    public void  testAdd(){
        Brand brand = new Brand();
        brand.setName("无线鼠标");
        brandMapper.insert(brand);

    }


    /**
     * 使用通用Mapper实现品牌增加
     */
    @Test
    public void  testInsert(){
        Brand brand = new Brand();
        brand.setName("无线鼠标");
        brandMapper.insertSelective(brand);

    }

    @Test
    public void testUpdate(){
        Brand brand = new Brand();
        brand.setName("小鼠标");
        brand.setId(75L);
        brandMapper.updateByPrimaryKey(brand);


    }



    //构建动态条件
    @Test
    public void testUpdateByExample(){
        Brand brand = new Brand();
        brand.setName("中鼠标");
        brand.setId(75L);

       //填入要操作的对象的字节码
        Example example = new Example(Brand.class);
        //用于动态构建条件
        Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("id",brand.getId());

        brandMapper.updateByExample(brand,example);

    }



    //构建动态条件
    @Test
    public void testUpdateByExampleKeySelective(){
        Brand brand = new Brand();
        brand.setName("大大鼠标");
        brand.setId(75L);

        //填入要操作的对象的字节码
        Example example = new Example(Brand.class);
        //用于动态构建条件
        Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("id",brand.getId());

        brandMapper.updateByExampleSelective(brand,example);

    }

    @Test
    public void testDelete(){
        Brand brand = new Brand();
        brand.setName("大大鼠标");
        brand.setId(75L);

        brandMapper.delete(brand);
    }

    @Test
    public void testDeletePrimaryKey(){
       brandMapper.deleteByPrimaryKey(66L);
    }

    @Test
    public void testSelect(){

        Brand brand = brandMapper.selectByPrimaryKey(67L);
        System.out.println(brand);
    }

    @Test
    public void testSelect02(){
        Brand brand = new Brand();
        //brand.setId(74L);
        brand.setFirstChar("A");
        List<Brand> brands = brandMapper.select(brand);
        for (Brand bd : brands) {
            System.out.println(bd);

        }


    }

    /**
     * 模糊查询
     *
     */
    @Test
    public void testGetBrandByName(){
        //使用Example实现模糊查询
        Example example = new Example(Brand.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andLike("name","%长%");

        //查询
        List<Brand> brands = brandMapper.selectByExample(example);
        for (Brand brand : brands) {
            System.out.println(brand);

        }

    }

    /**
     * 查询所有
     */
    @Test
    public void testGetALL(){

        PageHelper.startPage(1,3);

        List<Brand> brands = brandMapper.selectAll();
        for (Brand brand : brands) {

            System.out.println(brand);
        }
    }



}
