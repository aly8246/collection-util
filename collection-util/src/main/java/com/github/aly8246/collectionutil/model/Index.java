package com.github.aly8246.collectionutil.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Index {
private Integer index;
private Integer total;
private Integer start;

public Index() {
}

public Index(Integer index, Integer total) {
	this.index = index;
	this.total = total;
}

private Integer end;
}
