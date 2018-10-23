/**
 * Copyright (c) 2006-2015 Kingdee Ltd. All Rights Reserved. 
 *  
 * This code is the confidential and proprietary information of   
 * Kingdee. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Kingdee,http://www.Kingdee.com.
 *  
 */
package com.kingdee.work.tool;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

/**
 * <p>
 * 
 * </p>
 * 
 * @author Administrator
 * @date 2018年9月20日 下午1:43:56
 * @version
 */
public class BuildEnvironment {

	private String url;

	/**
	 * 解压jar到的目录
	 */
	private String destPath;

	/**
	 * 下载下来的临时目录
	 */
	private String downloadPath;

	public BuildEnvironment(String url, String destPath, String downloadPath) {
		this.url = url;
		this.destPath = destPath;
		this.downloadPath = downloadPath;
	}

	public List<String> pollFiles() throws ClientProtocolException, IOException {
		HttpClient httpClient = HttpClientBuilder.create().build();
		HttpGet httpGet = new HttpGet(url);

		HttpResponse httpResponse = httpClient.execute(httpGet);
		InputStream content = httpResponse.getEntity().getContent();

		List<String> readLines = IOUtils.readLines(content);

		List<String> fileNames = new ArrayList<String>();
		for (String readLine : readLines) {
			if (readLine != null && readLine.startsWith("<a")) {
				String fileName = readLine.substring(readLine.indexOf("=\"") + 2, readLine.indexOf("\">"));
				fileNames.add(fileName);
			}
		}

		return fileNames;
	}

	public void downloadFiles(List<String> fileNames) throws ClientProtocolException, IOException {
		HttpClient httpClient = HttpClientBuilder.create().build();

		for (String fileName : fileNames) {
			HttpGet httpGet = new HttpGet(url + "/" + fileName);
			HttpResponse httpResponse = httpClient.execute(httpGet);
			InputStream content = httpResponse.getEntity().getContent();

			File file = new File(downloadPath + fileName);

			if (!file.exists()) {
				file.createNewFile();
			}
			IOUtils.write(IOUtils.toByteArray(content), new FileOutputStream(file));
		}

	}

	public void unzipFiles() throws ZipException {

		File rootFile = new File(downloadPath);
		File[] listFiles = rootFile.listFiles();

		for (File file : listFiles) {
			ZipFile zipFile = new ZipFile(file);
			try {
				zipFile.extractAll(destPath);
			} catch (Exception e) {
				System.out.println("解压文件失败：" + zipFile.getFile().getPath());
			}
		}
	}

	/**
	 * 
	 * <p>
	 * 将文件夹中的文件移动到外面
	 * </p>
	 * 
	 * @author Administrator
	 * @date 2018年9月20日 下午2:30:09
	 * @version
	 */
	public void moveFile() {
		File rootFile = new File(destPath);
		File[] listFiles = rootFile.listFiles();

		// 先删除所有的jar
		for (File file : listFiles) {
			if (!file.isDirectory()) {
				file.delete();
			}
		}

		for (File file : listFiles) {
			this.moveSubFile(file);
		}

	}

	private void moveSubFile(File file) {

		File[] listFile = file.listFiles();

		if (listFile == null) {
			System.out.println("文件：" + file.getName() + "下不存在文件");
		} else {
			for (File subFile : listFile) {

				if (!subFile.isFile()) {
					moveSubFile(subFile);
				} else {
					if (subFile.renameTo(new File(destPath + File.separator + subFile.getName()))) {
						System.out.println("文件移动成功");
					} else {
						System.out.println("文件移动失败，目标移动文件" + destPath + File.separator + subFile.getName());
					}
				}
			}
		}
	}

}
