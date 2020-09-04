package com.yj.jenkinsdemo.util;

public interface FielAnalysisMapper {

	public FileAnalysis selectFileAnalysis(String fileType);

	public void updateFileAnalysis(FileAnalysis fileAnalysis);

}
