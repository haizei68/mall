package com.pinyougou.sellergoods.service;
import com.github.pagehelper.PageInfo;
import com.pinyougou.model.TypeTemplate;
import java.util.List;
import java.util.Map;

public interface TypeTemplateService {

	/**
	 * 返回TypeTemplate全部列表
	 * @return
	 */
	public List<TypeTemplate> getAll();

    /***
     * 分页返回TypeTemplate列表
     * @param pageNum
     * @param pageSize
     * @return
     */
    public PageInfo<TypeTemplate> getAll(TypeTemplate typeTemplate, int pageNum, int pageSize);

    /***
     * 增加TypeTemplate信息
     * @param typeTemplate
     * @return
     */
    int add(TypeTemplate typeTemplate);

    /***
     * 根据ID查询TypeTemplate信息
     * @param id
     * @return
     */
    TypeTemplate getOneById(Long id);

    /***
     * 根据ID修改TypeTemplate信息
     * @param typeTemplate
     * @return
     */
    int updateTypeTemplateById(TypeTemplate typeTemplate);

    /***
     * 根据ID批量删除TypeTemplate信息
     * @param ids
     * @return
     */
    int deleteByIds(List<Long> ids);

    /**
     * 根据模板ID查询规格选项数据并封装JSON格式
     * @param id
     * @return
     */
    List<Map> getSpecOptionByTypeId(long id);
}
