package com.github.aly8246.collectionexample.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserVo {
    private Integer id;

    private String name;

    private List<PhoneVo> phoneList = new ArrayList<>();
}