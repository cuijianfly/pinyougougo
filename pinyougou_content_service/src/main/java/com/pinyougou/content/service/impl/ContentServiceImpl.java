package com.pinyougou.content.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.abel533.entity.Example;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.content.service.ContentService;
import com.pinyougou.entity.PageResult;
import com.pinyougou.mapper.TbContentMapper;
import com.pinyougou.pojo.TbContent;
import com.pinyougou.pojo.TbContentExample;
import com.pinyougou.pojo.TbContentExample.Criteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Arrays;
import java.util.List;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class ContentServiceImpl implements ContentService {

	@Autowired
	private TbContentMapper contentMapper;

	@Autowired
	private RedisTemplate redisTemplate;
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
	    //新增广告需要清除缓存
		contentMapper.insert(content);
       redisTemplate.boundHashOps("content").delete(content.getCategoryId());
    }

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbContent content){
	    //执行更新操作需要删除缓存
        //修改了广告的类目id：1-->2
        //现根据参数类目id查询数据
        //TbContent tbContent = contentMapper.selectByPrimaryKey(content.getId());
        TbContent where = new TbContent();
        where.setId(content.getId());
        TbContent tbContent = contentMapper.select(where).get(0);
        if(tbContent != null && tbContent.getCategoryId().longValue() != content.getCategoryId().longValue()){
            //如果数据库查询出的类目id和传进来的参数类目ic不一致
            //将原缓存删除
            redisTemplate.boundHashOps("content").delete(tbContent.getCategoryId());
        }
        //数据库查询出的类目id和参数类目id一致
        //删除缓存
        redisTemplate.boundHashOps("content").delete(content.getCategoryId());
        int i = contentMapper.updateByPrimaryKey(content);
        System.out.println(i);
    }
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbContent findOne(Long id){

		TbContent where = new TbContent();
		where.setId(id);
		return contentMapper.select(where).get(0);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
        //删除操作需要删除redis缓存
      	 List list = Arrays.asList(ids);
        Example example = new Example(TbContent.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id", list);
        List<TbContent> contents = contentMapper.selectByExample(example);
        for (TbContent content : contents) {
            redisTemplate.boundHashOps("content").delete(content.getCategoryId());
        }
        contentMapper.deleteByExample(example);

		/*for(Long id:ids){
            TbContent tbContent = contentMapper.selectByPrimaryKey(id);
            redisTemplate.boundHashOps("content").delete(tbContent.getCategoryId());
            contentMapper.deleteByPrimaryKey(id);
		}	*/
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

	/**
	 * 根据广告类型id查询列表，在网站前台展示轮播图
	 * 由于访问量大，减轻数据库压力，使用redis缓存
	 * @param categoryId
	 * @return
	 */
	@Override
	public List<TbContent> findByCategoryId(Long categoryId) {
		//每次需要查詢时，先从redis中获取
		//如果查询不到数据，在从数据库查询
        List<TbContent> contents = (List<TbContent>) redisTemplate.boundHashOps("content").get(categoryId);
        if(contents == null || contents.size() < 1){
            System.out.println("在数据库中取数据");
            //说明缓存中没有数据，到数据库查询
            Example example = new Example(TbContent.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("categoryId",categoryId);
            //查询状态为1：正常
            criteria.andEqualTo("status","1");
            //设置排序
            example.setOrderByClause("sortOrder desc");
            contents = contentMapper.selectByExample(example);
            //将查询出的数据存到redis中
            //键为广告类目id
            redisTemplate.boundHashOps("content").put(categoryId,contents);
        }else{
            System.out.println("在缓存中取数据");
        }
        return contents;
	}

}
