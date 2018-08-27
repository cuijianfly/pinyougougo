package com.pinyougou.manager.controller;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.entity.PageResult;
import com.pinyougou.entity.Result;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.BrandService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 *商家后台管理系统
 */
@RestController
@RequestMapping("/brand")
public class BrandController {

    @Reference
    private BrandService brandService;

    @RequestMapping("/findAll")
    public List<TbBrand> findAll(){
        List<TbBrand> brands = brandService.findAll();
        return brands;
    }

    /**
     * 分页查询
     * @param page 当前页
     * @param size 当前页显示的条数
     * @return
     */
    @RequestMapping("/findPage")
    public PageResult<TbBrand> findPage(int page,int size,@RequestBody TbBrand brand){
        return brandService.findPage(page, size, brand);
    }
    @RequestMapping("/add")
    public Result add(@RequestBody TbBrand brand){
        try {
            brandService.add(brand);
            return new Result(true,"新增成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"新增失败");
        }
    }

    /**
     * 根据id查询商标
     * @param id
     * @return
     */
    @RequestMapping("/queryById")
    public TbBrand queryById(Long id){
        return brandService.queryById(id);
    }

    /**
     * 根据id更新商标
     * @param
     * @return
     */
    @RequestMapping("/update")
    public Result updateBrand(@RequestBody TbBrand brand){
        try {
            brandService.updateBrand(brand);
            return new Result(true,"更新成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"更新失败");
        }
    }

    /**
     * 删除
     * @param Ids
     * @return
     */
    @RequestMapping("/delete")
    public Result delete(Long[] Ids){
        try {
            brandService.delete(Ids);
            return new Result(true,"成 功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"失 败");
        }
    }
}
