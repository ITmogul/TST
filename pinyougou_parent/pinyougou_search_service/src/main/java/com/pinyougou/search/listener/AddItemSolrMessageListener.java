package com.pinyougou.search.listener;

import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.List;

public class AddItemSolrMessageListener implements MessageListener{

    @Autowired
    private TbItemMapper itemMapper;

    @Autowired
    private SolrTemplate solrTemplate;

    @Override
    public void onMessage(Message message) {
        try {
            TextMessage textMessage = (TextMessage)message;
            String goodsId = textMessage.getText();
            //商品上架，同步商品到索引库
            TbItemExample example = new TbItemExample();
            example.createCriteria().andGoodsIdEqualTo(Long.parseLong(goodsId));
            List<TbItem> itemList = itemMapper.selectByExample(example);

            //保存索引库操作
            solrTemplate.saveBeans(itemList);
            solrTemplate.commit();

        } catch (JMSException e) {
            e.printStackTrace();
        }


    }
}
