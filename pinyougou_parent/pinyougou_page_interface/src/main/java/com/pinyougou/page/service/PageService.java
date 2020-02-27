package com.pinyougou.page.service;

import groupEntity.Goods;

public interface PageService {

    /**
     * 查询静态页商品信息
     */
    public Goods findOne(Long goodsId);
}
