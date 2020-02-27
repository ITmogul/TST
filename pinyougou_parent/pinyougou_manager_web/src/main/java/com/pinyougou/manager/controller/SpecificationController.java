package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbSpecification;
import com.pinyougou.sellergoods.service.SpecificationService;
import entity.PageResult;
import entity.Result;
import groupEntity.Specification;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/specification")
public class SpecificationController {

    @Reference
    private SpecificationService specificationService;

    /**
     * 条件分页查询
     * specification 查询条件实体对象
     */
    @RequestMapping("/search")
    public PageResult search(@RequestBody TbSpecification specification,Integer pageNumber,Integer pageSize){
        return specificationService.search(specification,pageNumber,pageSize);
    }

    /**
     * 添加规格数据
     * RequestBody 将前端组成的数据属性与后台实体类属性映射注解
     */
    @RequestMapping("/add")
    public Result add(@RequestBody Specification specification){

        try {
            specificationService.add(specification);
            return new Result(true,"新增规格成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"新增规格失败");
        }

    }

    /**
     * 基于id查询数据
     */
    @RequestMapping("/findOne")
    public Specification findOne(Long id){
        return specificationService.findOne(id);
    }

    /**
     * 修改品牌数据
     * RequestBody 将前端组成的数据属性与后台实体类属性映射注解
     */
    @RequestMapping("/update")
    public Result update(@RequestBody Specification specification){

        try {
            specificationService.update(specification);
            return new Result(true,"修改规格成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"修改规格失败");
        }

    }

    /**
     * 批量删除数据
     */
    @RequestMapping("/delete")
    public Result delete(Long[] ids){

        try {
            specificationService.delete(ids);
            return new Result(true,"删除规格成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"删除规格失败");
        }

    }
    /**
     * 查询模板管理的规格列表
     */
    @RequestMapping("/selectSpecOptions")
    public List<Map> selectSpecOptions(){
        return specificationService.selectSpecOptions();
    }

}
