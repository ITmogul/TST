package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbSpecificationMapper;
import com.pinyougou.mapper.TbSpecificationOptionMapper;
import com.pinyougou.pojo.TbSpecification;
import com.pinyougou.pojo.TbSpecificationExample;
import com.pinyougou.pojo.TbSpecificationOption;
import com.pinyougou.pojo.TbSpecificationOptionExample;
import com.pinyougou.sellergoods.service.SpecificationService;
import entity.PageResult;
import groupEntity.Specification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class SpecificationServiceImpl implements SpecificationService {

    @Autowired
    private TbSpecificationMapper specificationMapper;


    @Override
    public PageResult search(TbSpecification specification, Integer pageNumber, Integer pageSize) {

        //分页条件
        PageHelper.startPage(pageNumber,pageSize);
        //查询条件
        TbSpecificationExample example = new TbSpecificationExample();
        if (specification!=null) {
            //获取规格名称参数
            String specName = specification.getSpecName();
           /* if(specName!=null && !"".equals(specName)){

            }*/
          if (!StringUtils.isBlank(specName)){
              //查询条件组装
              TbSpecificationExample.Criteria criteria = example.createCriteria();
              criteria.andSpecNameLike("%"+specName+"%");
          }

        }
        Page<TbSpecification> page = (Page<TbSpecification>) specificationMapper.selectByExample(example);

        return new PageResult(page.getTotal(),page.getResult());
    }

    @Autowired
    private TbSpecificationOptionMapper specificationOptionMapper;

    @Override
    public void add(Specification specification) {
        //新增规格
        TbSpecification tbSpecification = specification.getSpecification();
        specificationMapper.insert(tbSpecification);

        //int i=1/0;

        //新增规格选项
        List<TbSpecificationOption> specificationOptions = specification.getSpecificationOptions();
        for (TbSpecificationOption specificationOption : specificationOptions) {
            //关联规格选项和规格id
            specificationOption.setSpecId(tbSpecification.getId());
            specificationOptionMapper.insert(specificationOption);
        }

    }

    @Override
    public Specification findOne(Long id) {
        Specification specification = new Specification();
        //查询规格
        TbSpecification tbSpecification = specificationMapper.selectByPrimaryKey(id);
        specification.setSpecification(tbSpecification);
        //选项规格选项列表
        TbSpecificationOptionExample example = new TbSpecificationOptionExample();
        TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
        criteria.andSpecIdEqualTo(id);
        List<TbSpecificationOption> specificationOptions = specificationOptionMapper.selectByExample(example);
        specification.setSpecificationOptions(specificationOptions);
        return specification;
    }

    @Override
    public void update(Specification specification) {

        //更新规格
        TbSpecification tbSpecification = specification.getSpecification();
        specificationMapper.updateByPrimaryKey(tbSpecification);
        //更新规格选项  先删除原来规格选项列表，在新增页面提交规格选项列表
        //删除原来规格选项
        TbSpecificationOptionExample example = new TbSpecificationOptionExample();
        TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
        criteria.andSpecIdEqualTo(tbSpecification.getId());
        specificationOptionMapper.deleteByExample(example);
        //新增页面提交规格选项列表
        List<TbSpecificationOption> specificationOptions = specification.getSpecificationOptions();
        for (TbSpecificationOption specificationOption : specificationOptions) {
            //关联规格选项和规格id
            specificationOption.setSpecId(tbSpecification.getId());
            specificationOptionMapper.insert(specificationOption);
        }

    }

    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            //删除规格
            specificationMapper.deleteByPrimaryKey(id);
            //删除规格选项
            TbSpecificationOptionExample example = new TbSpecificationOptionExample();
            TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
            criteria.andSpecIdEqualTo(id);
            specificationOptionMapper.deleteByExample(example);
        }
    }

    @Override
    public List<Map> selectSpecOptions() {
        return specificationMapper.selectSpecOptions();
    }
}
