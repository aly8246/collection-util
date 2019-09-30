package com.github.aly8246.collectionexample.service;

import com.github.aly8246.collectionutil.model.Page;
import com.github.aly8246.collectionexample.dao.UserMapper;
import com.github.aly8246.collectionexample.pojo.vo.UserVo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;

    @Override
    public List<UserVo> selectAllUserByN(String id, Page page, String name) {
        return userMapper.selectAllUserByN(id, page, name);
    }
}
