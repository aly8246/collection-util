package com.github.aly8246.collectionexample.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.aly8246.collectionutil.model.Page;
import com.github.aly8246.collectionexample.pojo.model.User;
import com.github.aly8246.collectionexample.pojo.vo.UserVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserMapper extends BaseMapper<User> {
    List<UserVo> selectAllUserByN(@Param("id") String id, @Param("page") Page page, @Param("name") String name);

    IPage<UserVo> selectAllUserMP(@Param("page") com.baomidou.mybatisplus.extension.plugins.pagination.Page page, @Param("id") String id, @Param("name") String name);

    List<UserVo> selectAllUser(@Param("id") String id, @Param("name") String name);

    List<UserVo> selectUserById(@Param("userId") String userId);
}