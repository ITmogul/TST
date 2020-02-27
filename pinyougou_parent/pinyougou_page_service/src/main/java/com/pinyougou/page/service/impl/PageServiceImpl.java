package com.pinyougou.page.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.TbGoodsDescMapper;
import com.pinyougou.mapper.TbGoodsMapper;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.page.service.PageService;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbGoodsDesc;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;
import groupEntity.Goods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class PageServiceImpl implements PageService {

    @Autowired
    private TbGoodsMapper goodsMapper;

    @Autowired
    private TbGoodsDescMapper goodsDescMapper;

    @Autowired
    private TbItemMapper itemMapper;

    @Autowired
    private TbItemCatMapper itemCatMapper;



    @Override
    public Goods findOne(Long goodsId) {

        Goods goods = new Goods();
        TbGoods tbGoods = goodsMapper.selectByPrimaryKey(goodsId);
        goods.setGoods(tbGoods);
        //组装分类数据
        String categeory1Name = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory1Id()).getName();
        String categeory2Name = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory2Id()).getName();
        String categeory3Name = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory3Id()).getName();

        Map<String,String> categoryMap = new HashMap<>();
        categoryMap.put("categeory1Name",categeory1Name);
        categoryMap.put("categeory2Name",categeory2Name);
        categoryMap.put("categeory3Name",categeory3Name);

        goods.setCategoryMap(categoryMap);

        TbGoodsDesc tbGoodsDesc = goodsDescMapper.selectByPrimaryKey(goodsId);
        goods.setGoodsDesc(tbGoodsDesc);

        TbItemExample example = new TbItemExample();
        example.createCriteria().andGoodsIdEqualTo(goodsId);
        List<TbItem> itemList = itemMapper.selectByExample(example);
        goods.setItemList(itemList);

        return goods;
    }
}
