package com.pinyougou.utils;

import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class SolrUtils {

    @Autowired
    private TbItemMapper tbItemMapper;

    @Autowired
    private SolrTemplate solrTemplate;

    /**
     * 数据导入
     */
    public void dataImport(){

        //查询所有上架并且是有效状态的商品
        List<TbItem> itemList = tbItemMapper.findAllGrounding();

        //动态域赋值操作
        for (TbItem item : itemList) {
            String spec = item.getSpec();
            //解析spec为map
            Map<String,String> specMap = JSON.parseObject(spec, Map.class);
            item.setSpecMap(specMap);
        }

        solrTemplate.saveBeans(itemList);
        solrTemplate.commit();

    }
}
