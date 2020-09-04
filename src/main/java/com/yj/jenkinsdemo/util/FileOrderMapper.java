package com.yj.jenkinsdemo.util;

import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface FileOrderMapper {

	public void batchInsert(@Param("items") List<FileOrder> items);

}
