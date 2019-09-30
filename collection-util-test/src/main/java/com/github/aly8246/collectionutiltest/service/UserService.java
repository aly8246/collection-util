package com.github.aly8246.collectionutiltest.service;

import com.github.aly8246.collectionutil.model.Page;
import com.github.aly8246.collectionutiltest.pojo.vo.UserVo;

import java.util.List;


public interface UserService {
    List<UserVo> selectAllUserByN(String id, Page page, String name);
}
