package com.pinyougou.manager.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import com.pinyougou.http.Result;
import com.pinyougou.model.Brand;
import com.pinyougou.sellergoods.service.BrandService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/********
 * author:shenkunlin
 * date:2018/8/31 12:36
 * description:深圳黑马
 * version:1.0
 ******/
@RestController
@RequestMapping(value = "/brand")
public class BrandController {

    /*@Autowired*/

    @Reference
    private BrandService brandService;

    /**
     * 品牌列表查询
     * @return
     */
    @RequestMapping(value = "/jsonlist")
    public List<Map<String,Object>> jsonOptions(){
        List<Map<String,Object>> result = brandService.selectOptionList();

        return result;
    }

    @RequestMapping(value = "/delete")
    public Result deleteByIds(@RequestBody List<Long> ids){
        try {
            //调用Service批量删除
            int dcount = brandService.deleteByIds(ids);
            if(dcount>0){
                return new Result(true);
            }
        } catch (Exception e) {
        }
        return new Result(false,"删除失败！");
    }


    /**
     * 修改
     * @param brand
     * @return
     */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public Result modify(@RequestBody Brand brand) {
        try {
            //调用Service执行修改
            int mcount = brandService.updateBrandById(brand);

            if (mcount > 0) {
                return new Result(true);
            }
        } catch (Exception e) {
        }
        return new Result(false, "修改失败!");
    }


    /**
     * 查询,先根据id查询品牌
     */
    @RequestMapping(value = "/{id}")
    public Brand getById(@PathVariable(value = "id") Long id) {
        //Service 根据ID查询
        Brand brand = brandService.getOneById(id);
        return brand;
    }


    /**
     * 用户增加品牌
     */
    @RequestMapping(value = "/add")
    public Result add(@RequestBody Brand brand) {
        //响应操作状态 成功或失败 success=true 成功 false:失败
        //响应消息      失败的原因   message="消息"

        Map<String, Object> result = new HashMap<String, Object>();

        try {
            //实现增加
            int count = brandService.add(brand);

            //增加陈宫
            if (count > 0) {

                return new Result(true);
            }
        } catch (Exception e) {
        }

        return new Result(false, "增加失败");

    }


    /****
     * 集合查询
     * @return
     */

    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public PageInfo<Brand> list(@RequestParam(value = "page", required = false, defaultValue = "1") int page,
                                @RequestParam(value = "size", required = false, defaultValue = "5") int size,
                                @RequestBody Brand brand) {

        //查询获取数据-分页数据
        PageInfo<Brand> pageInfo = brandService.getAll(brand,page, size);

        return pageInfo;
    }


    /****
     * 集合查询
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public List<Brand> list() {
        List<Brand> brands = brandService.getAll();

        return brands;
    }
}
