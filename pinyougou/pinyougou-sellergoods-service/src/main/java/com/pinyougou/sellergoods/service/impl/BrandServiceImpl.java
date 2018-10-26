package com.pinyougou.sellergoods.service.impl;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.mapper.BrandMapper;
import com.pinyougou.model.Brand;
import com.pinyougou.sellergoods.service.BrandService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;
import java.util.List;
import java.util.Map;

@Service
public class BrandServiceImpl implements BrandService {

    @Autowired
    private BrandMapper brandMapper;

	/**
	 * 返回Brand全部列表
	 * @return
	 */
	@Override
    public List<Brand> getAll(){
        return brandMapper.selectAll();
    }


    /***
     * 分页返回Brand列表
     * @param pageNum
     * @param pageSize
     * @return
     */
	@Override
    public PageInfo<Brand> getAll(Brand brand,int pageNum, int pageSize) {
        //静态分页,startPage
        PageHelper.startPage(pageNum,pageSize);

        //Example对象
        Example example = new Example(Brand.class);
        Example.Criteria criteria = example.createCriteria();

        //查询集合-模糊查询
        if (brand!=null){
            //根据名字模糊查询
            if (StringUtils.isNotBlank(brand.getName())){
                criteria.andLike("name","%"+brand.getName()+"%");
            }


            if (StringUtils.isNotBlank(brand.getFirstChar())){
                criteria.andEqualTo("firstChar",brand.getFirstChar());
            }
        }

        //集合查询
        List<Brand> brands = brandMapper.selectByExample(example);

       /* //查询所有数据
        List<Brand> brands = brandMapper.selectAll();*/

        //创建PageInfo<T>

        return new PageInfo<Brand>(brands);
    }



    /***
     * 增加Brand信息
     * @param brand
     * @return
     */
    @Override
    public int add(Brand brand) {
        return brandMapper.insertSelective(brand);
    }


    /***
     * 根据ID查询Brand信息
     * @param id
     * @return
     */
    @Override
    public Brand getOneById(Long id) {
        return brandMapper.selectByPrimaryKey(id);
    }


    /***
     * 根据ID修改Brand信息
     * @param brand
     * @return
     */
    @Override
    public int updateBrandById(Brand brand) {
        return brandMapper.updateByPrimaryKeySelective(brand);
    }


    /***
     * 根据ID批量删除Brand信息
     * @param ids
     * @return
     */
    @Override
    public int deleteByIds(List<Long> ids) {
        //创建Example，来构建根据ID删除数据
        Example example = new Example(Brand.class);
        Example.Criteria criteria = example.createCriteria();

        //所需的SQL语句类似 delete from tb_brand where id in(1,2,5,6)
        criteria.andIn("id",ids);
        return brandMapper.deleteByExample(example);
    }

    @Override
    public List<Map<String, Object>> selectOptionList() {
        return brandMapper.selectOptionList();
    }
}
