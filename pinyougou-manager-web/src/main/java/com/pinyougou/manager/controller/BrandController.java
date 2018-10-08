package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.entity.DMLResult;
import com.pinyougou.entity.PageResult;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.BrandService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/brand")
public class BrandController {

    @Reference
    private BrandService brandService;

    @RequestMapping("/findAll")
    public List<TbBrand> findAll(){
        return brandService.findAll();
    }

    @RequestMapping("/findPage")
    public PageResult<TbBrand> findPage(Integer page,Integer size){
        return  brandService.findPage(page, size);
    }

    @RequestMapping("/insert")
    public DMLResult insert(@RequestBody TbBrand tbBrand){
        DMLResult dmlResult;
        try {
            brandService.insert(tbBrand);
            dmlResult = new DMLResult(true,"添加成功!");
        } catch (Exception e) {
            e.printStackTrace();
            dmlResult = new DMLResult(false,"添加失败!");
            return dmlResult;
        }
        return dmlResult;
    }
}
