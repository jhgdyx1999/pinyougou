package com.pinyougou.portal.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.content.service.ContentService;
import com.pinyougou.pojo.TbContent;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author:Jack
 * @description:
 * @Date:created in 2018/10/17,15:06
 */
@RestController
@RequestMapping("/content")
public class ContentController {

    @Reference
    private ContentService contentService;

    @RequestMapping("/selectContentListByCategoryId")
    public List<TbContent> selectContentListByCategoryId(Long categoryId) {
        return contentService.selectContentListByCategoryId(categoryId);
    }
}
