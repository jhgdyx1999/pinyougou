package com.pinyougou.sellergoods.service.impl;
import java.util.List;
import java.util.Map;

import com.pinyougou.compositeEntity.SpecificationAndSpecificationOption;
import com.pinyougou.entity.PageResult;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbSpecificationMapper;
import com.pinyougou.mapper.TbSpecificationOptionMapper;
import com.pinyougou.pojo.TbSpecification;
import com.pinyougou.pojo.TbSpecificationExample;
import com.pinyougou.pojo.TbSpecificationExample.Criteria;
import com.pinyougou.pojo.TbSpecificationOption;
import com.pinyougou.pojo.TbSpecificationOptionExample;
import com.pinyougou.sellergoods.service.SpecificationService;

import javax.annotation.Resource;


/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class SpecificationServiceImpl implements SpecificationService {

	@Resource
	private TbSpecificationMapper specificationMapper;
	
	@Resource
	private TbSpecificationOptionMapper specificationOptionMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbSpecification> findAll() {
		return specificationMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbSpecification> page=   (Page<TbSpecification>) specificationMapper.selectByExample(null);
		return new PageResult<>(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(SpecificationAndSpecificationOption specificationAndSpecificationOption) {
        TbSpecification specification = specificationAndSpecificationOption.getSpecification();
        List<TbSpecificationOption> specificationOptionList = specificationAndSpecificationOption.getSpecificationOptionList();
        specificationMapper.insert(specification);
        for (TbSpecificationOption option: specificationOptionList) {
            option.setSpecId(specification.getId());
            specificationOptionMapper.insert(option);
        }

    }

	/**
	 * 修改
	 */
	@Override
	public void update(SpecificationAndSpecificationOption specificationAndSpecificationOption){
        Long id = specificationAndSpecificationOption.getSpecification().getId();
        //更新规格
        specificationMapper.updateByPrimaryKey(specificationAndSpecificationOption.getSpecification());
        //跟新规格选项
        List<TbSpecificationOption> specificationOptionList = specificationAndSpecificationOption.getSpecificationOptionList();
        //删除所有相应的规格选项
        TbSpecificationOptionExample tbSpecificationOptionExample = new TbSpecificationOptionExample();
        TbSpecificationOptionExample.Criteria criteria = tbSpecificationOptionExample.createCriteria();
        criteria.andSpecIdEqualTo(id);
        specificationOptionMapper.deleteByExample(tbSpecificationOptionExample);
        //重新插入规格选项
        for (TbSpecificationOption tbSpecificationOption:specificationOptionList) {
                tbSpecificationOption.setSpecId(id);
                specificationOptionMapper.insert(tbSpecificationOption);
        }
    }
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public SpecificationAndSpecificationOption findOne(Long id){
        TbSpecification tbSpecification = specificationMapper.selectByPrimaryKey(id);
        TbSpecificationOptionExample tbSpecificationOptionExample = new TbSpecificationOptionExample();
        TbSpecificationOptionExample.Criteria criteria = tbSpecificationOptionExample.createCriteria();
        criteria.andSpecIdEqualTo(id);
        List<TbSpecificationOption> specificationOptionList = specificationOptionMapper.selectByExample(tbSpecificationOptionExample);
        SpecificationAndSpecificationOption specificationAndSpecificationOption = new SpecificationAndSpecificationOption();
        specificationAndSpecificationOption.setSpecification(tbSpecification);
        specificationAndSpecificationOption.setSpecificationOptionList(specificationOptionList);
        return specificationAndSpecificationOption;
    }

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
        for(Long id : ids){
            TbSpecificationOptionExample tbSpecificationOptionExample = new TbSpecificationOptionExample();
            TbSpecificationOptionExample.Criteria criteria = tbSpecificationOptionExample.createCriteria();
            criteria.andSpecIdEqualTo(id);
            specificationOptionMapper.deleteByExample(tbSpecificationOptionExample);
            specificationMapper.deleteByPrimaryKey(id);
        }
	}
	
		@Override
	public PageResult findPage(TbSpecification specification, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbSpecificationExample example=new TbSpecificationExample();
		Criteria criteria = example.createCriteria();
		
		if(specification!=null){			
						if(specification.getSpecName()!=null && specification.getSpecName().length()>0){
				criteria.andSpecNameLike("%"+specification.getSpecName()+"%");
			}
	
		}
		
		Page<TbSpecification> page= (Page<TbSpecification>)specificationMapper.selectByExample(example);		
		return new PageResult<>(page.getTotal(), page.getResult());
	}

		@Override
		public List<Map> selectSpecList() {
			return  specificationMapper.selectSpecList();
		}
	
}
