package com.pinyougou.user.controller;

import java.util.List;

import com.pinyougou.common.util.PhoneFormatCheckUtils;
import com.pinyougou.entity.PageResult;
import com.pinyougou.entity.Result;
import com.pinyougou.user.service.UserService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbUser;

/**
 * controller
 *
 * @author Administrator
 */
@SuppressWarnings("Duplicates")
@RestController
@RequestMapping("/user")
public class UserController {

    @Reference
    private UserService userService;

    /**
     * 返回全部列表
     *
     * @return
     */
    @RequestMapping("/findAll")
    public List<TbUser> findAll() {
        return userService.findAll();
    }


    /**
     * 返回全部列表
     *
     * @return
     */
    @RequestMapping("/findPage")
    public PageResult findPage(int page, int rows) {
        return userService.findPage(page, rows);
    }

    /**
     * 增加
     *
     * @param user
     * @return
     */
    @RequestMapping("/add")
    public Result add(@RequestBody TbUser user,String smsCode) {
        try {
            //校验手机验证码
            boolean isValidated = userService.checkSmsCode(user.getPhone(), smsCode);
            if (!isValidated){
                return new Result(false, "验证码错误!");
            }
            //校验通过
            userService.add(user);
            return new Result(true, "注册成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "注册失败");
        }
    }

    /**
     * 修改
     *
     * @param user
     * @return
     */
    @RequestMapping("/update")
    public Result update(@RequestBody TbUser user) {
        try {
            userService.update(user);
            return new Result(true, "修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "修改失败");
        }
    }

    /**
     * 获取实体
     *
     * @param id
     * @return
     */
    @RequestMapping("/findOne")
    public TbUser findOne(Long id) {
        return userService.findOne(id);
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @RequestMapping("/delete")
    public Result delete(Long[] ids) {
        try {
            userService.delete(ids);
            return new Result(true, "删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "删除失败");
        }
    }

    /**
     * 查询+分页
     *
     * @param
     * @param page
     * @param rows
     * @return
     */
    @RequestMapping("/search")
    public PageResult search(@RequestBody TbUser user, int page, int rows) {
        return userService.findPage(user, page, rows);
    }

    /**
     * 生成验证码,发送短信
     * @param phone
     * @return
     */
    @RequestMapping("/createSmsCode")
    public Result createSmsCode(String phone){
        try {
            if (!PhoneFormatCheckUtils.isPhoneLegal(phone)){
                return new Result(false, "手机号码不正确!");
            }
            String smsCode = userService.createSmsCode(phone);
            System.out.println("验证码:"+smsCode);
            return new Result(true, "验证码发送成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(true, "验证码发送失败");
        }
    }



}
