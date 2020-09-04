package com.yj.jenkinsdemo.controller;

import com.yj.jenkinsdemo.util.FielAnalysisMapper;
import com.yj.jenkinsdemo.util.FileAnalysis;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.InputStream;

/**
 * 批次插入大文件数据
 * 
 * @author hui.zhao.cfs
 *
 */
public class BatchInsertMain {

	public static void main(String[] args) throws Exception {
		String resource = "mybatis-config2.xml";
		InputStream inputStream = Resources.getResourceAsStream(resource);
		SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);

		FileAnalysis fileAnalysis = new FileAnalysis();
		SqlSession session = sqlSessionFactory.openSession();
		try {
			FielAnalysisMapper mapper = session.getMapper(FielAnalysisMapper.class);
			fileAnalysis = mapper.selectFileAnalysis("01");
			session.commit();
		} finally {
			session.close();
		}

		FileAnalysisInsert fileAnalysisInsert = new FileAnalysisInsert(sqlSessionFactory);
		fileAnalysisInsert.batchInsertOrder(fileAnalysis);

	}
}
