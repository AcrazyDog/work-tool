/**
 * Copyright (c) 2006-2015 Kingdee Ltd. All Rights Reserved. 
 *  
 * This code is the confidential and proprietary information of   
 * Kingdee. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Kingdee,http://www.Kingdee.com.
 *  
 */
package com.kingdee.work;

import java.io.IOException;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.junit.Test;

import com.kingdee.work.tool.BuildEnvironment;

import net.lingala.zip4j.exception.ZipException;

/**
 * <p>
 * 
 * </p>
 * 
 * @author Administrator
 * @date 2018年9月20日 下午1:49:06
 * @version
 */
public class BuildEnvironmentTest {

	@Test
	public void pollFiles() throws ClientProtocolException, IOException {
		BuildEnvironment buildEnvironment = new BuildEnvironment("http://172.18.5.42:88/biz-dev/apppackage/", "F:\\ierp\\biz-dev\\lib", "C:\\Users\\Administrator\\Desktop\\jar\\");
		buildEnvironment.pollFiles();
	}

	@Test
	public void downloadFiles() throws ClientProtocolException, IOException {
		BuildEnvironment buildEnvironment = new BuildEnvironment("http://172.18.5.42:88/biz-dev/apppackage/", "F:\\ierp\\biz-dev\\lib", "C:\\Users\\Administrator\\Desktop\\jar\\");
		List<String> pollFiles = buildEnvironment.pollFiles();
		buildEnvironment.downloadFiles(pollFiles);
	}

	@Test
	public void buildEnvironment() throws ClientProtocolException, IOException, ZipException {
		BuildEnvironment buildEnvironment = new BuildEnvironment("http://172.18.5.42:88/biz-main/apppackage/", "F:\\ierp\\biz-main\\lib", "C:\\Users\\Administrator\\Desktop\\jar\\");
		List<String> pollFiles = buildEnvironment.pollFiles();
		buildEnvironment.downloadFiles(pollFiles);
		buildEnvironment.unzipFiles();
		buildEnvironment.moveFile();
	}
}
