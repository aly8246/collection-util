package com.github.aly8246.collectionutiltest.pojo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Phone {
    private Integer id;

    private String name;

    private Integer userId;
}