package com.pinyougou.sellergoods.service;

import com.pinyougou.entity.PageResult;
import com.pinyougou.pojo.TbBrand;

import java.util.List;
import java.util.Map;

public interface BrandService {

    public List<TbBrand> findAll();

    public PageResult<TbBrand> findPage(int pageNum, int pageSize);

    public void insert(TbBrand tbBrand);

    public TbBrand findOne(Long id);

    public void update(TbBrand tbBrand);

    public void delete(Long[] lids);

    public PageResult<TbBrand> search(TbBrand tbBrand,int pageNum, int pageSize);

    public List<Map> selectBrandList();
}
