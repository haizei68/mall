package com.pinyougou.sellergoods.service.impl;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.mapper.SpecificationMapper;
import com.pinyougou.mapper.SpecificationOptionMapper;
import com.pinyougou.model.Specification;
import com.pinyougou.model.SpecificationOption;
import com.pinyougou.sellergoods.service.SpecificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;
import java.util.List;
import java.util.Map;

@Service
public class SpecificationServiceImpl implements SpecificationService {

    @Autowired
    private SpecificationMapper specificationMapper;



	/**
	 * 返回Specification全部列表
	 * @return
	 */
	@Override
    public List<Specification> getAll(){
        return specificationMapper.selectAll();
    }


    /***
     * 分页返回Specification列表
     * @param pageNum
     * @param pageSize
     * @return
     */
	@Override
    public PageInfo<Specification> getAll(Specification specification,int pageNum, int pageSize) {
        //执行分页
        PageHelper.startPage(pageNum,pageSize);
       
        //执行查询
        List<Specification> all = specificationMapper.select(specification);
        PageInfo<Specification> pageInfo = new PageInfo<Specification>(all);
        return pageInfo;
    }

    @Autowired
    private SpecificationOptionMapper specificationOptionMapper;


    /***
     * 增加Specification信息
     * @param specification
     * @return
     */
    @Override
    public int add(Specification specification) {
        //增加规格信息
        int account=specificationMapper.insertSelective(specification);

        //增加规格选项
        List<SpecificationOption> specificationOptionList = specification.getSpecificationOptionList();

        for (SpecificationOption specificationOption : specificationOptionList) {

            //设置id
            specificationOption.setSpecId(specification.getId());
            specificationOptionMapper.insertSelective(specificationOption);
        }

        return account;

    }


    /***
     * 根据ID查询Specification信息
     * @param id
     * @return
     */
    @Override
    public Specification getOneById(Long id) {
        //规格信息
        Specification specification =  specificationMapper.selectByPrimaryKey(id);

        //规格选项
        SpecificationOption specificationOption = new SpecificationOption();
        specificationOption.setSpecId(id);

        //规格选项集合
        List<SpecificationOption> specificationOptions = specificationOptionMapper.select(specificationOption);
        specification.setSpecificationOptionList(specificationOptions);

        return specification;
    }


    /***
     * 根据ID修改Specification信息
     * @param specification
     * @return
     */
    @Override
    public int updateSpecificationById(Specification specification) {
        //删除之前的规格选项 delete from table where spec_id=?
        SpecificationOption specificationOption = new SpecificationOption();
        specificationOption.setSpecId(specification.getId());

        specificationOptionMapper.delete(specificationOption);

        //修改规格
        int mcount=specificationMapper.updateByPrimaryKeySelective(specification);

        //新增规格选项
        for (SpecificationOption speo : specification.getSpecificationOptionList()) {

            //设置id
            speo.setSpecId(specification.getId());
            specificationOptionMapper.insertSelective(speo);
        }

        return mcount;

    }


    /***
     * 根据ID批量删除Specification信息
     * @param ids
     * @return
     */
    @Override
    public int deleteByIds(List<Long> ids) {
        //创建Example，来构建根据ID删除数据
        Example example = new Example(Specification.class);
        Example.Criteria criteria = example.createCriteria();

        //所需的SQL语句类似 delete from tb_specification where id in(1,2,5,6)
        criteria.andIn("id",ids);

        for (Long id : ids) {
            SpecificationOption specificationOption = new SpecificationOption();
            specificationOption.setSpecId(id);

            //执行删除
            specificationOptionMapper.delete(specificationOption);
        }
        return specificationMapper.deleteByExample(example);
    }

    /**
     * 查询规格信息
     * @return
     */
    @Override
    public List<Map<String, Object>> getOptionList() {
        return specificationMapper.selectOptionList();
    }
}
