package com.pinyougou.content.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.content.service.ContentService;
import com.pinyougou.mapper.TbContentMapper;
import com.pinyougou.pojo.TbContent;
import com.pinyougou.pojo.TbContentExample;
import com.pinyougou.pojo.TbContentExample.Criteria;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
@Transactional
public class ContentServiceImpl implements ContentService {

	@Autowired
	private TbContentMapper contentMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbContent> findAll() {
		return contentMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbContent> page=   (Page<TbContent>) contentMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbContent content) {
		contentMapper.insert(content);
		//清空redis中的当前分类对应的广告列表
		Long categoryId = content.getCategoryId();
		redisTemplate.boundHashOps("pinyougou_content").delete(categoryId);
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbContent content){
		//清空广告列表数据
		TbContent tbContent = contentMapper.selectByPrimaryKey(content.getId());
		Long categoryId = tbContent.getCategoryId();
		redisTemplate.boundHashOps("pinyougou_content").delete(categoryId);
		contentMapper.updateByPrimaryKey(content);

		if (tbContent.getCategoryId().longValue()!= content.getCategoryId().longValue()) {
			//页面修改了广告分类id
			redisTemplate.boundHashOps("pinyougou_content").delete(content.getCategoryId());
		}
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbContent findOne(Long id){
		return contentMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			//清空redis中的当前分类对应的广告列表
			TbContent content = contentMapper.selectByPrimaryKey(id);
			Long categoryId = content.getCategoryId();
			redisTemplate.boundHashOps("pinyougou_content").delete(categoryId);
			contentMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbContent content, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbContentExample example=new TbContentExample();
		Criteria criteria = example.createCriteria();
		
		if(content!=null){			
						if(content.getTitle()!=null && content.getTitle().length()>0){
				criteria.andTitleLike("%"+content.getTitle()+"%");
			}
			if(content.getUrl()!=null && content.getUrl().length()>0){
				criteria.andUrlLike("%"+content.getUrl()+"%");
			}
			if(content.getPic()!=null && content.getPic().length()>0){
				criteria.andPicLike("%"+content.getPic()+"%");
			}
			if(content.getStatus()!=null && content.getStatus().length()>0){
				criteria.andStatusLike("%"+content.getStatus()+"%");
			}
	
		}
		
		Page<TbContent> page= (Page<TbContent>)contentMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Autowired
	private RedisTemplate redisTemplate;

	@Override
	public List<TbContent> findByCategoryId(Long categoryId) {
		//1、尝试从redis缓存中获取广告列表
		List<TbContent> contentList = (List<TbContent>) redisTemplate.boundHashOps("pinyougou_content").get(categoryId);

		//2、redis中没有广告列表，从数据库中查询广告列表
		if(contentList==null){
			System.out.println("from mysql ..............");
			//从数据库中查询广告列表
			TbContentExample example = new TbContentExample();
			example.createCriteria().andCategoryIdEqualTo(categoryId).andStatusEqualTo("1");
			//设置排序条件
			example.setOrderByClause("sort_order");
			contentList = contentMapper.selectByExample(example);
			//3、将数据库中的查询结果存入redis
			redisTemplate.boundHashOps("pinyougou_content").put(categoryId,contentList);
		}else {
			System.out.println("from redis ..............");
		}


		return contentList;
	}

}
