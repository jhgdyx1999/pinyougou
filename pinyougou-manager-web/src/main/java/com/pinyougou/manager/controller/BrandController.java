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

    @RequestMapping("/findOne")
    public TbBrand findOne(Long id){
        return brandService.findOne(id);
    }

    @RequestMapping("/update")
    public DMLResult update(@RequestBody TbBrand tbBrand){
        DMLResult dmlResult;
        try {
            brandService.update(tbBrand);
            dmlResult = new DMLResult(true,"修改成功!");
        } catch (Exception e) {
            e.printStackTrace();
            dmlResult = new DMLResult(false,"修改失败!");
            return dmlResult;
        }
        return dmlResult;
    }

    @RequestMapping("/del")
    public DMLResult delete(Long[] ids){
        DMLResult dmlResult;
        try {
            brandService.delete(ids);
            dmlResult = new DMLResult(true,"删除成功!");
        } catch (Exception e) {
            e.printStackTrace();
            dmlResult = new DMLResult(false,"删除失败!");
            return dmlResult;
        }
        return dmlResult;
    }

    @RequestMapping("/search")
    public PageResult<TbBrand> search(@RequestBody TbBrand tbBrand,Integer page,Integer size){
        return brandService.search(tbBrand,page,size);
    }
}
