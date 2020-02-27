package com.pinyougou.solr.test;

import com.pinyougou.pojo.TbItem;
import com.pinyougou.utils.SolrUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.SolrDataQuery;
import org.springframework.data.solr.core.query.result.ScoredPage;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:spring/applicationContext*.xml")
public class SolrTest {

    @Autowired
    private SolrTemplate solrTemplate;

    @Autowired
    private SolrUtils solrUtils;

    @Test
    public void dataImportTest(){
        solrUtils.dataImport();
    }

    /**
     * 新增或修改
     */
    @Test
    public void saveItem(){
        TbItem item = new TbItem();
        item.setId(2L);
        item.setBrand("华为");
        item.setSeller("华为旗舰店");
        item.setTitle("华为p20 移动3G 128G");
        //保存索引库
        solrTemplate.saveBean(item);
        //提交操作
        solrTemplate.commit();
    }

    /**
     * 基于id查询
     */
    @Test
    public void queryById(){
        TbItem item = solrTemplate.getById(1L, TbItem.class);
        System.out.println(item.getId()+"   "+item.getBrand()+"  "+item.getSeller()+"   "+item.getTitle());
    }

    @Test
    public void deleteById(){
        solrTemplate.deleteById("1");
        solrTemplate.commit();
    }

    @Test
    public void deleteAll(){
        //*:* 查询所有
        SolrDataQuery query = new SimpleQuery("*:*");
        solrTemplate.delete(query);
        solrTemplate.commit();
    }


    /**
     * 批量插入100条数据
     */
    @Test
    public void saveBatch(){

        List<TbItem> list = new ArrayList<>();

        for (long i = 1; i <=100 ; i++) {
            TbItem item = new TbItem();
            item.setId(i);
            item.setBrand("华为");
            item.setSeller("华为"+i+"号旗舰店");
            item.setTitle(i+"华为p20 移动3G 128G");
            list.add(item);
        }

        //保存索引库
        solrTemplate.saveBeans(list);
        //提交操作
        solrTemplate.commit();

    }

    /**
     * 分页查询
     */
    @Test
    public void queryByPage(){
        //获取查询对象
        Query query = new SimpleQuery("*:*");

        //设置分页条件
        query.setOffset(3);//查询起始值 默认：0开始  (page-1)*rows
        query.setRows(5);//每页记录数 默认：10条记录

        ScoredPage<TbItem> page = solrTemplate.queryForPage(query, TbItem.class);

        System.out.println("总页码:"+page.getTotalPages());
        System.out.println("总记录数:"+page.getTotalElements());

        //获取当前页结果集
        List<TbItem> content = page.getContent();
        for (TbItem item : content) {
            System.out.println(item.getId()+"   "+item.getBrand()+"  "+item.getSeller()+"   "+item.getTitle());
        }
    }

    /**
     * 多条件分页查询
     */
    @Test
    public void queryMultiByPage(){
        //获取查询对象
        Query query = new SimpleQuery();

        //设置查询条件  店铺包含8并且title包含5
        Criteria criteria = new Criteria("item_seller").contains("8").and("item_title").contains("5");

        //将组装好的查询条件绑定到查询对象
        query.addCriteria(criteria);

        //设置分页条件
        query.setOffset(0);//查询起始值 默认：0开始  (page-1)*rows
        query.setRows(5);//每页记录数 默认：10条记录

        ScoredPage<TbItem> page = solrTemplate.queryForPage(query, TbItem.class);

        System.out.println("总页码:"+page.getTotalPages());
        System.out.println("总记录数:"+page.getTotalElements());

        //获取当前页结果集
        List<TbItem> content = page.getContent();
        for (TbItem item : content) {
            System.out.println(item.getId()+"   "+item.getBrand()+"  "+item.getSeller()+"   "+item.getTitle());
        }
    }

}



