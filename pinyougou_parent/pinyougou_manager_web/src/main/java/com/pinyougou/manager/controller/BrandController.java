package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.BrandService;
import entity.PageResult;
import entity.Result;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/*
@Controller
@ResponseBody*/
@RestController  //相当于@Controller+@ResponseBody
@RequestMapping("/brand")
public class BrandController {

    @Reference
    private BrandService brandService;

    /**
     * 查询所有品牌列表
     * @return
     */
    @RequestMapping("/findAll")
    public List<TbBrand> findAll(){
        return brandService.findAll();
    }

    /**
     * 分页查询品牌列表
     * @return
     */
    @RequestMapping("/findPage")
    public PageResult findPage(int pageNumber,int pageSize){
        return brandService.findPage(pageNumber,pageSize);
    }

    /**
     * 添加品牌数据
     * RequestBody 将前端组成的数据属性与后台实体类属性映射注解
     */
    @RequestMapping("/add")
    public Result add(@RequestBody TbBrand brand){

        try {
            brandService.add(brand);
            return new Result(true,"新增品牌成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"新增品牌失败");
        }

    }

    /**
     * 基于id查询品牌数据
     */
    @RequestMapping("/findOne")
    public TbBrand findOne(Long id){
        return brandService.findOne(id);
    }

    /**
     * 修改品牌数据
     * RequestBody 将前端组成的数据属性与后台实体类属性映射注解
     */
    @RequestMapping("/update")
    public Result update(@RequestBody TbBrand brand){

        try {
            brandService.update(brand);
            return new Result(true,"修改品牌成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"修改品牌失败");
        }

    }

    /**
     * 批量删除品牌数据
     */
    @RequestMapping("/delete")
    public Result delete(Long[] ids){

        try {
            brandService.delete(ids);
            return new Result(true,"删除品牌成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"删除品牌失败");
        }

    }

    /**
     * 查询模板关联的品牌列表
     *      key:value  map
     * @return
     */
    @RequestMapping("/selectBrandOptions")
    public List<Map> selectBrandOptions(){
        return brandService.selectBrandOptions();
    }

}
