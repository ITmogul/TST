package com.pinyougou.sellergoods.service;

import com.pinyougou.pojo.TbSpecification;
import entity.PageResult;
import groupEntity.Specification;

import java.util.List;
import java.util.Map;

public interface SpecificationService {

    /**
     * 条件分页查询
     */
    PageResult search(TbSpecification specification, Integer pageNumber, Integer pageSize);

    /**
     * 添加规格数据
     */
    void add(Specification specification);

    Specification findOne(Long id);

    void update(Specification specification);

    void delete(Long[] ids);

    List<Map> selectSpecOptions();
}
