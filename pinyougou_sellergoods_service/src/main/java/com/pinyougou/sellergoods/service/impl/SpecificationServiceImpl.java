package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.abel533.entity.Example;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.entity.PageResult;
import com.pinyougou.mapper.TbSpecificationMapper;
import com.pinyougou.mapper.TbSpecificationOptionMapper;
import com.pinyougou.pojo.TbSpecification;
import com.pinyougou.pojo.TbSpecificationExample;
import com.pinyougou.pojo.TbSpecificationExample.Criteria;
import com.pinyougou.pojo.TbSpecificationOption;
import com.pinyougou.pojogroup.Specification;
import com.pinyougou.sellergoods.service.SpecificationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class SpecificationServiceImpl implements SpecificationService {

	@Autowired
	private TbSpecificationMapper specificationMapper;
	@Autowired
    private TbSpecificationOptionMapper optionMapper;
	/**
	 * 查询全部
	 */
	@Override
	public List<TbSpecification> findAll() {
		return specificationMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbSpecification> page=   (Page<TbSpecification>) specificationMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(Specification specification) {
		/*分别存储规格、规格项*/
		if(specification != null && specification.getSpecification() != null){
			specificationMapper.insert(specification.getSpecification());
            List<TbSpecificationOption> optionList = specification.getSpecificationOptionList();
            if(optionList != null && optionList.size() > 0){
                for (TbSpecificationOption option : optionList) {
                    option.setSpecId(specification.getSpecification().getId());
                    optionMapper.insert(option);
                }
            }
        }
    }

	
	/**
	 * 修改
	 */
	@Override
	public void update(Specification specification){
	    /*更新规格、规格项*/
	    if(specification != null && specification.getSpecification() != null){
	        specificationMapper.updateByPrimaryKeySelective(specification.getSpecification());
            /*先将原有的规格项删除
            * 需要有新增，新增选项没有id，是新增操作
            * 如果有删除，数据库也无法删除数据
            * */
            Long id = specification.getSpecification().getId();
            Example example = new Example(TbSpecificationOption.class);
            example.createCriteria().andEqualTo("specId",id);
            optionMapper.deleteByExample(example);
            List<TbSpecificationOption> optionList = specification.getSpecificationOptionList();
            if(optionList != null && optionList.size() > 0){
                for (TbSpecificationOption option : optionList) {
                    option.setSpecId(id);
                    optionMapper.insert(option);
                }
            }
        }
	}
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public Specification findOne(Long id){
	    /*分别查找规格、规格项*/
        TbSpecification specification = specificationMapper.selectByPrimaryKey(id);
        Example example = new Example(TbSpecificationOption.class);
        example.createCriteria().andEqualTo("specId",id);
        List<TbSpecificationOption> optionList = optionMapper.selectByExample(example);
        Specification spec = new Specification();
        spec.setSpecification(specification);
        spec.setSpecificationOptionList(optionList);
        return spec;
    }

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
	    /*删除规格、规格项*/
        if (ids != null && ids.length > 0){
            for(Long id:ids){
                specificationMapper.deleteByPrimaryKey(id);
                Example example = new Example(TbSpecificationOption.class);
                example.createCriteria().andEqualTo("specId",id);
                optionMapper.deleteByExample(example);
            }
        }
	}

		@Override
	public PageResult findPage(TbSpecification specification, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbSpecificationExample example=new TbSpecificationExample();
		Criteria criteria = example.createCriteria();
		
		if(specification!=null){			
						if(specification.getSpecName()!=null && specification.getSpecName().length()>0){
				criteria.andSpecNameLike("%"+specification.getSpecName()+"%");
			}
	
		}
		
		Page<TbSpecification> page= (Page<TbSpecification>)specificationMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}
	
}
