package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.entity.PageResult;
import com.pinyougou.mapper.*;
import com.pinyougou.pojo.*;
import com.pinyougou.pojo.TbGoodsExample.Criteria;
import com.pinyougou.pojogroup.Goods;
import com.pinyougou.sellergoods.service.GoodsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service(interfaceClass = GoodsService.class)
@Transactional
public class GoodsServiceImpl implements GoodsService {

	@Autowired
	private TbGoodsMapper goodsMapper;
	@Autowired
	private TbGoodsDescMapper descMapper;
	@Autowired
	private TbItemMapper itemMapper;
	@Autowired
	private TbBrandMapper brandMapper;
	@Autowired
	private TbSellerMapper sellerMapper;
	@Autowired
	private TbItemCatMapper itemCatMapper;
	/**
	 * 查询全部
	 */
	@Override
	public List<TbGoods> findAll() {
		return goodsMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbGoods> page=   (Page<TbGoods>) goodsMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(Goods goods) {
		goods.getGoods().setAuditStatus("0");
		goodsMapper.insert(goods.getGoods());	//插入商品表
		goods.getGoodsDesc().setGoodsId(goods.getGoods().getId());
		descMapper.insert(goods.getGoodsDesc());//插入商品扩展数据
		saveItemList(goods);//插入商品SKU列表数据
	}
	//提取方法
	private void saveItemList(Goods goods){
        if("1".equals(goods.getGoods().getIsEnableSpec())){
            for(TbItem item :goods.getItemList()){
                //标题
                String title= goods.getGoods().getGoodsName();
                Map<String,Object> specMap = JSON.parseObject(item.getSpec());
                for(String key:specMap.keySet()){
                    title+=" "+ specMap.get(key);
                }
                item.setTitle(title);
                setItemValus(goods,item);
                itemMapper.insert(item);
            }
        }else{
            TbItem item=new TbItem();
            item.setTitle(goods.getGoods().getGoodsName());//商品KPU+规格描述串作为SKU名称
            item.setPrice( goods.getGoods().getPrice() );//价格
            item.setStatus("1");//状态
            item.setIsDefault("1");//是否默认
            item.setNum(99999);//库存数量
            item.setSpec("{}");
            setItemValus(goods,item);
            itemMapper.insert(item);
        }
    }

	private void setItemValus(Goods goods,TbItem item) {
		item.setGoodsId(goods.getGoods().getId());//商品SPU编号
		item.setSellerId(goods.getGoods().getSellerId());//商家编号
		item.setCategoryid(goods.getGoods().getCategory3Id());//商品分类编号（3级）
		item.setCreateTime(new Date());//创建日期
		item.setUpdateTime(new Date());//修改日期

		//品牌名称
		TbBrand brand = brandMapper.selectByPrimaryKey(goods.getGoods().getBrandId());
		item.setBrand(brand.getName());
		//分类名称
		TbItemCat itemCat = itemCatMapper.selectByPrimaryKey(goods.getGoods().getCategory3Id());
		item.setCategory(itemCat.getName());

		//商家名称
		TbSeller seller = sellerMapper.selectByPrimaryKey(goods.getGoods().getSellerId());
		item.setSeller(seller.getNickName());

		//图片地址（取spu的第一个图片）
		List<Map> imageList = JSON.parseArray(goods.getGoodsDesc().getItemImages(), Map.class) ;
		if(imageList.size()>0){
			item.setImage ( (String)imageList.get(0).get("url"));
		}
	}

	/**
	 * 修改
	 */
	@Override
	public void update(Goods goods){
        //重新设置商品状态
        goods.getGoods().setAuditStatus("0");
        //保存商品表
        goodsMapper.updateByPrimaryKey(goods.getGoods());
        //保存商品描述表
        descMapper.updateByPrimaryKey(goods.getGoodsDesc());
        //SKU数据需要先删除，插入
        TbItem where = new TbItem();
        where.setGoodsId(goods.getGoods().getId());
        itemMapper.delete(where);
		saveItemList(goods);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public Goods findOne(Long id){
        TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
        Goods goods = new Goods();
        goods.setGoods(tbGoods);
        TbGoodsDesc goodsDesc = descMapper.selectByPrimaryKey(id);
        goods.setGoodsDesc(goodsDesc);
        TbItem where = new TbItem();
        where.setGoodsId(id);
        List<TbItem> items = itemMapper.select(where);
        goods.setItemList(items);
        return goods;
    }

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
	    if(ids != null && ids.length > 0){
            for(Long id:ids){
                /*goodsMapper.deleteByPrimaryKey(id);*/
                TbGoods goods = goodsMapper.selectByPrimaryKey(id);
                goods.setIsDelete("1");
                goodsMapper.updateByPrimaryKey(goods);
            }
        }
	}

	@Override
	public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbGoodsExample example=new TbGoodsExample();
		Criteria criteria = example.createCriteria();
		criteria.andIsDeleteIsNull();
		if(goods!=null){			
			if(goods.getSellerId()!=null && goods.getSellerId().length()>0){
				criteria.andSellerIdEqualTo(goods.getSellerId());
			/*criteria.andSellerIdLike("%"+goods.getSellerId()+"%");*/
			}
			if(goods.getGoodsName()!=null && goods.getGoodsName().length()>0){
				criteria.andGoodsNameLike("%"+goods.getGoodsName()+"%");
			}
			if(goods.getAuditStatus()!=null && goods.getAuditStatus().length()>0){
				criteria.andAuditStatusLike("%"+goods.getAuditStatus()+"%");
			}
			if(goods.getIsMarketable()!=null && goods.getIsMarketable().length()>0){
				criteria.andIsMarketableLike("%"+goods.getIsMarketable()+"%");
			}
			if(goods.getCaption()!=null && goods.getCaption().length()>0){
				criteria.andCaptionLike("%"+goods.getCaption()+"%");
			}
			if(goods.getSmallPic()!=null && goods.getSmallPic().length()>0){
				criteria.andSmallPicLike("%"+goods.getSmallPic()+"%");
			}
			if(goods.getIsEnableSpec()!=null && goods.getIsEnableSpec().length()>0){
				criteria.andIsEnableSpecLike("%"+goods.getIsEnableSpec()+"%");
			}
			if(goods.getIsDelete()!=null && goods.getIsDelete().length()>0){
				criteria.andIsDeleteLike("%"+goods.getIsDelete()+"%");
			}
	
		}
		
		Page<TbGoods> page= (Page<TbGoods>)goodsMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 根据id审核商家状态
	 * @param ids
	 * @param auditStatus
	 */
	@Override
	public void updateStatus(Long[] ids, String auditStatus) {
		if (ids != null && ids.length > 0 && StringUtils.isNotBlank(auditStatus)){
			for (Long id : ids) {
                TbGoods goods = goodsMapper.selectByPrimaryKey(id);
                goods.setAuditStatus(auditStatus);

                goodsMapper.updateByPrimaryKey(goods);
            }
		}
	}

}
