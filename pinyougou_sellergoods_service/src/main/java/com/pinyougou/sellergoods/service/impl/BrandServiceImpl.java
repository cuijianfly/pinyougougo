package com.pinyougou.sellergoods.service.impl;

import com.pinyougou.mapper.BrandService;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.pojo.TbBrand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
