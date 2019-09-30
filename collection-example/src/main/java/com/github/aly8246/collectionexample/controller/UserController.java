package com.github.aly8246.collectionexample.controller;

import com.github.aly8246.collectionutil.model.Page;
import com.github.aly8246.collectionutil.result.PageResult;
import com.github.aly8246.collectionutil.util.CollectionUtil;
import com.github.aly8246.collectionutil.util.IPage;
import com.github.aly8246.collectionexample.dao.UserMapper;
import com.github.aly8246.collectionexample.pojo.vo.UserVo;
import com.github.aly8246.collectionexample.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("user/")
@RequiredArgsConstructor
public class UserController {
    private final UserMapper userMapper;
    private final UserService userService;

    @GetMapping("collectionUtil")
    public Object test(Page page) {
        userService.selectAllUserByN("1", page, "2");
        PageResult pageResult = CollectionUtil.get();

        //可以多次分页
        userMapper.selectAllUserByN("1", new Page(2, 2), "2");
        IPage iPage = CollectionUtil.packMPResult();
        System.out.println(iPage);
        return iPage;
    }


    @GetMapping("mybatisplus")
    public Object test3() {
        com.baomidou.mybatisplus.core.metadata.IPage<UserVo> userVoIPage = userMapper.selectAllUserMP(new com.baomidou.mybatisplus.extension.plugins.pagination.Page(2, 2, 5), "1", "1");
        return userVoIPage;
    }

    @GetMapping("right")
    public Object test2() {
        return userMapper.selectUserById("1");
    }
}
