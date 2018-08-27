package com.pinyougou.sellergoods.service;

import com.pinyougou.entity.PageResult;
import com.pinyougou.pojo.TbBrand;

import java.util.List;

public interface BrandService {

    public List<TbBrand> findAll();

    public PageResult findPage(int pageNum,int pageSize, TbBrand brand);

    public void add(TbBrand brand);

    public TbBrand queryById(Long id);

    public void delete(Long[] Ids);

    public void updateBrand(TbBrand brand);
}
