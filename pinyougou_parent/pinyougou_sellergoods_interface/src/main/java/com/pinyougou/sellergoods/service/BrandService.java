package com.pinyougou.sellergoods.service;

import com.pinyougou.pojo.TbBrand;
import entity.PageResult;

import java.util.List;
import java.util.Map;

public interface BrandService {

    /**
     * 查询所有品牌列表
     */
    public List<TbBrand> findAll();

    /**
     * 分页查询品牌列表
     * @return
     */
    PageResult findPage(int pageNumber, int pageSize);

    /**
     * 添加品牌数据
     */
    void add(TbBrand brand);

    /**
     * 基于id查询品牌数据
     */
    TbBrand findOne(Long id);
    /**
     * 修改品牌数据
     * RequestBody 将前端组成的数据属性与后台实体类属性映射注解
     */
    void update(TbBrand brand);
    /**
     * 批量删除品牌数据
     */
    void delete(Long[] ids);

    List<Map> selectBrandOptions();
}
