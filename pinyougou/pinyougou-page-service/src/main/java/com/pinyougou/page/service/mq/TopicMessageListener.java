package com.pinyougou.page.service.mq;

import com.pinyougou.model.Item;
import com.pinyougou.mq.MessageInfo;
import com.pinyougou.page.service.ItemPageService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TopicMessageListener implements MessageListener {
    @Autowired
    private ItemPageService itemPageService;

    @Override
    public void onMessage(Message message) {
        //ObjectMessage
        if (message instanceof ObjectMessage) {
            //强转成ObjectMessage
            ObjectMessage objectMessage = (ObjectMessage) message;

            //强转成MessageInfo
            try {
                MessageInfo messageInfo = (MessageInfo) objectMessage.getObject();

                //审核通过,则生成静态页 GoodsId
                if (messageInfo.getMethod() == MessageInfo.METHOD_UPDATE || messageInfo.getMethod() == MessageInfo.METHOD_ADD) {
                    //获取审核通过的SKU
                    List<Item> items = (List<Item>) messageInfo.getContext();

                    //获取goodsid
                    Set<Long> goodsIds = getGoodsIds(items);

                    //魂环生成静态页
                    for (Long goodsId : goodsIds) {
                        itemPageService.bulidHtml(goodsId);
                    }
                }else {
                    //获取删除的goodsid集合
                    List<Long> ids = (List<Long>) messageInfo.getContext();
                    for (Long id : ids) {
                        //删除商品,删除静态页
                        itemPageService.deleteHtml(id);
                    }
                }

            } catch (JMSException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }


    /**
     * 去除GoodsId的重复数据
     */
    public Set<Long> getGoodsIds(List<Item> items){
        Set<Long> ids = new HashSet<Long>();

        for (Item item : items) {
            ids.add(item.getGoodsId());
        }

        return ids;
    }
}
