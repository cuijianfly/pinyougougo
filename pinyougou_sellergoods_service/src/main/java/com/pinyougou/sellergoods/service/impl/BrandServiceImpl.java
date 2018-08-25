package com.pinyougou.sellergoods.service.impl;

import com.github.abel533.entity.Example;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.entity.PageResult;
import com.pinyougou.mapper.BrandService;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.pojo.TbBrand;
import org.apache.commons.lang3.StringUtils;
import org.jboss.netty.util.internal.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
@com.alibaba.dubbo.config.annotation.Service
public class BrandServiceImpl implements BrandService {
    
    @Autowired
    private TbBrandMapper brandMapper;
    /**
     * 查询所有
     * @return
     */
    public List<TbBrand> findAll() {
        return brandMapper.selectByExample(null);
    }

    /**
     * 分页查询
     * @param pageNum 当前页
     * @param pageSize 当前页显示的条数
     * @return
     */
    public PageResult findPage(int pageNum, int pageSize, TbBrand brand) {
        //开启分页
        PageHelper.startPage(pageNum,pageSize);
        Example example = new Example(TbBrand.class);
        Example.Criteria criteria = example.createCriteria();
        if (brand != null && StringUtils.isNotBlank(brand.getName())){
            criteria.andLike("name","%"+brand.getName()+"%");
        }
        if (brand != null && StringUtils.isNotBlank(brand.getFirstChar())) {
            criteria.andEqualTo("firstChar",brand.getFirstChar().toUpperCase());
        }
        //Page<TbBrand> page = (Page<TbBrand>)brandMapper.select(null);
        Page<TbBrand> page = (Page<TbBrand>)brandMapper.selectByExample(example);
        return new PageResult(page.getTotal(),page.getResult());
    }

    /**
     * 新增
     */
    public void add(TbBrand brand) {
        brandMapper.insert(brand);
    }

    /**
     * 根据id查询商标
     * @param id
     * @return
     */
    public TbBrand queryById(Long id) {
        return brandMapper.selectByPrimaryKey(id);
    }

    /**
     * 删除
     * @param Ids
     */
    @Override
    public void delete(Long[] Ids) {
        for (Long id : Ids) {
            brandMapper.deleteByPrimaryKey(id);
        }

        /*List<Long> list = Arrays.asList(Ids);

        brandMapper.deleteByExample();*/

    }

    /**
     * 根据id更新商标
     * @param tbBrand
     */
    public void updateBrand(TbBrand tbBrand) {
        brandMapper.updateByPrimaryKeySelective(tbBrand);
    }

}
