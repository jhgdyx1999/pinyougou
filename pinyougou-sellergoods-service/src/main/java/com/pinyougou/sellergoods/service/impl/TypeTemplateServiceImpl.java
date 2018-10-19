package com.pinyougou.sellergoods.service.impl;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.pinyougou.entity.PageResult;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbSpecificationOptionMapper;
import com.pinyougou.mapper.TbTypeTemplateMapper;
import com.pinyougou.pojo.TbSpecificationOption;
import com.pinyougou.pojo.TbSpecificationOptionExample;
import com.pinyougou.pojo.TbTypeTemplate;
import com.pinyougou.pojo.TbTypeTemplateExample;
import com.pinyougou.pojo.TbTypeTemplateExample.Criteria;
import com.pinyougou.sellergoods.service.TypeTemplateService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
@Transactional
public class TypeTemplateServiceImpl implements TypeTemplateService {

    @Resource
    private TbTypeTemplateMapper typeTemplateMapper;
    @Resource
    private TbSpecificationOptionMapper specificationOptionMapper;
    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    /**
     * 查询全部
     */
    @Override
    public List<TbTypeTemplate> findAll() {
        return typeTemplateMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbTypeTemplate> page = (Page<TbTypeTemplate>) typeTemplateMapper.selectByExample(null);

        return new PageResult<>(page.getTotal(), page.getResult());
    }

    public void saveToRedis(){
        List<TbTypeTemplate> typeTemplateList = findAll();
        for(TbTypeTemplate typeTemplate : typeTemplateList){
            //缓存品牌名称
            List<Map> brandMap = JSON.parseArray(typeTemplate.getBrandIds(), Map.class);
            redisTemplate.boundHashOps("brandList").put(typeTemplate.getId(), brandMap);
            //缓存规格
            List<Map> specOptions = selectSpecificationListWithOptions(typeTemplate.getId());
            redisTemplate.boundHashOps("specList").put(typeTemplate.getId(), specOptions);
        }
        System.out.println("缓存品牌与规格...");


    }
    /**
     * 增加
     */
    @Override
    public void add(TbTypeTemplate typeTemplate) {
        typeTemplateMapper.insert(typeTemplate);
    }


    /**
     * 修改
     */
    @Override
    public void update(TbTypeTemplate typeTemplate) {
        typeTemplateMapper.updateByPrimaryKey(typeTemplate);
    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public TbTypeTemplate findOne(Long id) {
        return typeTemplateMapper.selectByPrimaryKey(id);
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            typeTemplateMapper.deleteByPrimaryKey(id);
        }
    }


    @Override
    public PageResult findPage(TbTypeTemplate typeTemplate, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbTypeTemplateExample example = new TbTypeTemplateExample();
        Criteria criteria = example.createCriteria();

        if (typeTemplate != null) {
            if (typeTemplate.getName() != null && typeTemplate.getName().length() > 0) {
                criteria.andNameLike("%" + typeTemplate.getName() + "%");
            }
            if (typeTemplate.getSpecIds() != null && typeTemplate.getSpecIds().length() > 0) {
                criteria.andSpecIdsLike("%" + typeTemplate.getSpecIds() + "%");
            }
            if (typeTemplate.getBrandIds() != null && typeTemplate.getBrandIds().length() > 0) {
                criteria.andBrandIdsLike("%" + typeTemplate.getBrandIds() + "%");
            }
            if (typeTemplate.getCustomAttributeItems() != null && typeTemplate.getCustomAttributeItems().length() > 0) {
                criteria.andCustomAttributeItemsLike("%" + typeTemplate.getCustomAttributeItems() + "%");
            }

        }
        Page<TbTypeTemplate> page = (Page<TbTypeTemplate>) typeTemplateMapper.selectByExample(example);
        //缓存品牌与规格
        saveToRedis();
        return new PageResult<>(page.getTotal(), page.getResult());
    }

    @Override
    public List<Map> selectAllTemplates() {
        return typeTemplateMapper.selectAllTemplates();
    }

    @Override
    public List<Map> selectSpecificationListWithOptions(Long id) {
        TbTypeTemplate typeTemplate = typeTemplateMapper.selectByPrimaryKey(id);
        List<Map> list = JSON.parseArray(typeTemplate.getSpecIds(), Map.class);

        for (Map map : list) {
            TbSpecificationOptionExample specificationOptionExample = new TbSpecificationOptionExample();
            TbSpecificationOptionExample.Criteria criteria = specificationOptionExample.createCriteria();
            criteria.andSpecIdEqualTo(new Long(map.get("id").toString()));
            List<TbSpecificationOption> tbSpecificationOptions = specificationOptionMapper.selectByExample(specificationOptionExample);
            map.put("options", tbSpecificationOptions);
        }
        return list;
    }

}
