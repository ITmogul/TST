package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.HighlightEntry;
import org.springframework.data.solr.core.query.result.HighlightPage;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class SearchServiceImpl implements SearchService {

    @Autowired
    private SolrTemplate solrTemplate;

    @Override
    public Map<String, Object> search(Map searchMap) {
        //构建高亮查询对象
        HighlightQuery query = new SimpleHighlightQuery();
        //1、关键字搜索
        //获取页面提交的搜索关键字
        String keywords = (String) searchMap.get("keywords");
        keywords = keywords.replace(" ","");
        Criteria criteria = null;
        if(StringUtils.isNotEmpty(keywords)){
            //输入的搜索条件   is:会对搜索的内容分词  contains不会对搜索内容分词，将搜索内容当做一个完整的词条搜索
            criteria = new Criteria("item_keywords").is(keywords);
            //criteria = new Criteria("item_keywords").contains(keywords);
        }else{
            //未输入的搜索条件，查询所有 *:*
            criteria = new Criteria().expression("*:*");
        }

        query.addCriteria(criteria);

        //过滤条件查询
        //2、获取分类条件
        String category = (String) searchMap.get("category");
        if(StringUtils.isNotEmpty(category)){
            //组装过滤条件
            Criteria categoryCriteria = new Criteria("item_category").is(category);
            FilterQuery filterQuery = new SimpleFilterQuery(categoryCriteria);
            query.addFilterQuery(filterQuery);
        }

        //3、获取品牌条件
        String brand = (String) searchMap.get("brand");
        if(StringUtils.isNotEmpty(brand)){
            //组装过滤条件
            Criteria brandCriteria = new Criteria("item_brand").is(brand);
            FilterQuery filterQuery = new SimpleFilterQuery(brandCriteria);
            query.addFilterQuery(filterQuery);
        }

        //4、获取规格条件
        Map<String,String> specMap = (Map<String, String>) searchMap.get("spec");
        for(String key : specMap.keySet()){
            //组装过滤条件
            Criteria specCriteria = new Criteria("item_spec_"+key).is(specMap.get(key));
            FilterQuery filterQuery = new SimpleFilterQuery(specCriteria);
            query.addFilterQuery(filterQuery);
        }

        //5、获取价格区间筛选条件
        String price = (String) searchMap.get("price");
        if(StringUtils.isNotEmpty(price)){
            //组装过滤条件
            // 0-1000  1000-2000  2000-*    价格临界值  0 *
            String[] prices = price.split("-");

            if(!"0".equals(prices[0])){
                Criteria priceCriteria = new Criteria("item_price").greaterThanEqual(prices[0]);
                FilterQuery filterQuery = new SimpleFilterQuery(priceCriteria);
                query.addFilterQuery(filterQuery);
            }

            if(!"*".equals(prices[1])){
                Criteria priceCriteria = new Criteria("item_price").lessThanEqual(prices[1]);
                FilterQuery filterQuery = new SimpleFilterQuery(priceCriteria);
                query.addFilterQuery(filterQuery);
            }
        }

        //6、排序查询
        String sortField = (String) searchMap.get("sortField");
        String sort = (String) searchMap.get("sort");
        if (StringUtils.isNotEmpty(sortField)) {
            //判断排序方式 升序还是降序
            if ("ASC".equals(sort)) {//升序
                query.addSort(new Sort(Sort.Direction.ASC,"item_"+sortField));
            }else{//降序
                query.addSort(new Sort(Sort.Direction.DESC,"item_"+sortField));
            }

        }

        //获取当前页
        Integer pageNo = (Integer) searchMap.get("pageNo");
        int rows= 60;
        //7、分页条件查询
        query.setOffset((pageNo-1)*rows);//分页索引起始值 默认：0  （pageNo-1)*rows
        query.setRows(rows);//每页展示记录数


        //高亮处理
        //创建高亮对象
        HighlightOptions highlightOptions = new HighlightOptions();
        //设置高亮字段
        highlightOptions.addField("item_title");
        //设置高亮前缀和后缀
        highlightOptions.setSimplePrefix("<font color='red'>");
        highlightOptions.setSimplePostfix("</font>");
        query.setHighlightOptions(highlightOptions);

        HighlightPage<TbItem> page = solrTemplate.queryForHighlightPage(query, TbItem.class);


        //获取当前页列表
        List<TbItem> itemList = page.getContent();
        //处理高亮结果
        for (TbItem item : itemList) {
            //获取高亮结果集
            List<HighlightEntry.Highlight> highlights = page.getHighlights(item);
            if(highlights!=null && highlights.size()>0){
                //获取高亮对象
                HighlightEntry.Highlight highlight = highlights.get(0);
                //获取高亮内容
                List<String> snipplets = highlight.getSnipplets();
                if(snipplets!=null && snipplets.size()>0){
                    String title = snipplets.get(0);
                    item.setTitle(title);
                }
            }

        }


        //构建返回结果
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("rows",itemList);
        resultMap.put("pageNo",pageNo);//当前页
        resultMap.put("totalPages",page.getTotalPages());//总页数

        return resultMap;
    }
}
