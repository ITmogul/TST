package com.pinyougou.search.service;

import java.util.Map;

public interface SearchService {

    /**
     * 商品搜索
     */
    public Map<String,Object> search(Map searchMap);
}
