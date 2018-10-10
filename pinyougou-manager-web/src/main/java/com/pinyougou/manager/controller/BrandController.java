package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.entity.Result;
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
    public Result insert(@RequestBody TbBrand tbBrand){
        Result result;
        try {
            brandService.insert(tbBrand);
            result = new Result(true,"添加成功!");
        } catch (Exception e) {
            e.printStackTrace();
            result = new Result(false,"添加失败!");
            return result;
        }
        return result;
    }

    @RequestMapping("/findOne")
    public TbBrand findOne(Long id){
        return brandService.findOne(id);
    }

    @RequestMapping("/update")
    public Result update(@RequestBody TbBrand tbBrand){
        Result result;
        try {
            brandService.update(tbBrand);
            result = new Result(true,"修改成功!");
        } catch (Exception e) {
            e.printStackTrace();
            result = new Result(false,"修改失败!");
            return result;
        }
        return result;
    }

    @RequestMapping("/del")
    public Result delete(Long[] ids){
        Result result;
        try {
            brandService.delete(ids);
            result = new Result(true,"删除成功!");
        } catch (Exception e) {
            e.printStackTrace();
            result = new Result(false,"删除失败!");
            return result;
        }
        return result;
    }

    @RequestMapping("/search")
    public PageResult<TbBrand> search(@RequestBody TbBrand tbBrand,Integer page,Integer size){
        return brandService.search(tbBrand,page,size);
    }
}
