package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import com.pinyougou.http.Result;
import com.pinyougou.manager.service.MessageSender;
import com.pinyougou.model.Goods;
import com.pinyougou.model.Item;
import com.pinyougou.mq.MessageInfo;
//import com.pinyougou.page.service.ItemPageService;
//import com.pinyougou.search.service.ItemSearchService;
import com.pinyougou.sellergoods.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/goods")
public class GoodsController {

    @Reference
    private GoodsService goodsService;

 /*   @Reference
    private ItemSearchService itemSearchService;*/

  /*  @Reference
    private ItemPageService itemPageService;*/

    //消息发送
    @Autowired
    private MessageSender messageSender;

    /**
     * 生成商品静态页
     *
     * @param goodsId
     * @return
     */
  /*  @RequestMapping(value = "/build/{id}")
    public Boolean buildHtml(@PathVariable(value = "id") Long goodsId) throws Exception {
        return itemPageService.bulidHtml(goodsId);
    }*/

    /**
     * 修改商品状态
     *
     * @param ids
     * @param status
     * @return
     */
    @RequestMapping(value = "/update/status")
    public Result updateStatus(@RequestBody List<Long> ids, String status) {
        try {
            //调用Service实现修改状态
            int mcount = goodsService.updateStatus(ids, status);

            if (mcount > 0) {
                if (status.equals("1")) {
                    //添加对象是Item,将当前goodsId对应的Item查询出来
                    List<Item> items = goodsService.getItemByGoodsIds(ids, status);

                    //如果审核通过,则需要将该商品添加到索引库
                    //itemSearchService.importList(items);




                //发送items到消息队列中
                    MessageInfo messageInfo = new MessageInfo(MessageInfo.METHOD_ADD, items);
                    messageSender.sendObjectMessage(messageInfo);
                }
                return new Result(true);
            }
        } catch (Exception e) {

        }
        return new Result(false, "修改失败!");

    }


    /***
     * 根据ID批量删除
     * @param ids
     * @return
     */
    @RequestMapping(value = "/delete")
    public Result delete(@RequestBody List<Long> ids) {
        try {
            //根据ID删除数据
            int dcount = goodsService.deleteByIds(ids);

            if (dcount > 0) {
                //itemSearchService.deleteByGoodsIds(ids);
                //封装消息
                MessageInfo messageInfo=new MessageInfo(MessageInfo.METHOD_DELETE,ids);
                //发送消息
                messageSender.sendObjectMessage(messageInfo);

                return new Result(true, "删除成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Result(false, "删除失败");
    }

    /***
     * 修改信息
     * @param goods
     * @return
     */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public Result modify(@RequestBody Goods goods) {
        try {

            //商品属于谁
            String name = SecurityContextHolder.getContext().getAuthentication().getName();

            //修改的商品不属于用户自身
            if (!name.equals(goods.getSellerId())) {
                return new Result(false, "非法操作");
            }

            //根据ID修改Goods信息
            int mcount = goodsService.updateGoodsById(goods);
            if (mcount > 0) {
                return new Result(true, "修改成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Result(false, "修改失败");
    }

    /***
     * 根据ID查询Goods信息
     * @param id
     * @return
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Goods getById(@PathVariable(value = "id") long id) {
        //根据ID查询Goods信息
        Goods goods = goodsService.getOneById(id);
        return goods;
    }


    /***
     * 增加Goods数据
     * @param goods
     * 响应数据：success
     *                  true:成功  false：失败
     *           message
     *                  响应的消息
     *
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Result add(@RequestBody Goods goods) {
        try {
            //执行增加
            int acount = goodsService.add(goods);

            if (acount > 0) {
                //增加成功
                return new Result(true, "增加成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Result(false, "增加失败");
    }


    /***
     * 分页查询数据
     * 获取JSON数据
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public PageInfo<Goods> list(@RequestBody Goods goods, @RequestParam(value = "page", required = false, defaultValue = "1") int page,
                                @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        return goodsService.getAll(goods, page, size);
    }


    /***
     * 查询所有
     * 获取JSON数据
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public List<Goods> list() {
        return goodsService.getAll();
    }
}
