package com.github.aly8246.collectionutil.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel
public class Page {

@ApiModelProperty(value = "第几页,默认第一页", name = "page", example = "1", required = false)
private int page;

@ApiModelProperty(value = "每页的大小,默认一页10条数据", name = "pageSize", example = "10", required = false)
private int pageSize;
}
