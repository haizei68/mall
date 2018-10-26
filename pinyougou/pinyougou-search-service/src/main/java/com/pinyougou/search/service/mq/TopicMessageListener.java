package com.pinyougou.search.service.mq;

import com.pinyougou.model.Item;
import com.pinyougou.mq.MessageInfo;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 消息监听
 */
public class TopicMessageListener implements MessageListener {

    @Autowired
    private ItemSearchService itemSearchService;

    @Override
    public void onMessage(Message message) {
        //ObjectMessage
        if (message instanceof ObjectMessage) {
            //将消息强转成ObjectMessage类型
            // ObjectMessage
            ObjectMessage objectMessage = (ObjectMessage) message;
            try {
                //获取消息体
                MessageInfo messageInfo = (MessageInfo) objectMessage.getObject();

                //如果是修改状态,创建索引
                if (messageInfo.getMethod() == MessageInfo.METHOD_UPDATE || messageInfo.getMethod() == MessageInfo.METHOD_ADD) {
                    //获取发送的内容
                    List<Item> items = (List<Item>) messageInfo.getContext();

                    //创建索引
                    itemSearchService.importList(items);
                } else {
                    //获取发送的内容
                    List<Long> ids = (List<Long>) messageInfo.getContext();
                    //如果是删除,则直接删除索引
                    itemSearchService.deleteByGoodsIds(ids);

                }
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * GoodsId去除重复
     *
     * @param items
     * @return
     */
    public List<Long> getGoodsIds(List<Item> items) {
        //用于存储GoodsId
        Set<Long> goodsIds = new HashSet<Long>();
        for (Item item : items) {
            goodsIds.add(item.getGoodsId());
        }

        List<Long> longs = new ArrayList<Long>();
        longs.addAll(goodsIds);
        return longs;
    }
}
