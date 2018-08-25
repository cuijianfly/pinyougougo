package com.pinyougou.entity;

import com.pinyougou.pojo.TbBrand;

import java.io.Serializable;
import java.util.List;

/**
 * 分页结果封装对象
 */
public class PageResult<T> implements Serializable {
    //总记录数
    private Long total;
    //当前页要显示的数据
    private List<TbBrand> rows;

    public PageResult() {
    }

    public PageResult(Long total, List<TbBrand> rows) {
        this.total = total;
        this.rows = rows;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public List<TbBrand> getRows() {
        return rows;
    }

    public void setRows(List<TbBrand> rows) {
        this.rows = rows;
    }

    @Override
    public String toString() {
        return "PageResult{" +
                "total=" + total +
                ", rows=" + rows +
                '}';
    }
}
