/*******************************************************************************
 * CopyRight (c) 2005-2011 TAOTAOSOU Co, Ltd. All rights reserved.
 * Filename:    Object2XML.java
 * Creator:     wangqi
 * Create-Date: 2011-6-14 上午09:39:45
 *******************************************************************************/
package edu.hziee.common.lang;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * TODO
 * 
 * @author wangqi
 * @version $Id: Object2XML.java 14 2012-01-10 11:54:14Z archie $
 */
public class Object2XML {

	/**
	 * 对象输出到XML文件
	 * 
	 * @param obj
	 *            待输出的对象
	 * @param outFileName
	 *            目标XML文件的文件名
	 * @return 返回输出XML文件的路径
	 * @throws FileNotFoundException
	 */
	public static String object2XML(Object obj, String outFileName)
			throws FileNotFoundException {
		// 构造输出XML文件的字节输出流
		File outFile = new File(outFileName);
		BufferedOutputStream bos = new BufferedOutputStream(
				new FileOutputStream(outFile));

		// 构造一个XML编码器
		XMLEncoder xmlEncoder = new XMLEncoder(bos);
		// 使用XML编码器写对象
		xmlEncoder.writeObject(obj);
		// 关闭编码器
		xmlEncoder.close();

		return outFile.getAbsolutePath();
	}
	/**
	 * 把XML文件解码成对象
	 * 
	 * @param inFileName
	 *            输入的XML文件
	 * @return 返回生成的对象
	 * @throws FileNotFoundException
	 */
	public static Object xml2Object(String inFileName)
			throws FileNotFoundException {
		// 构造输入的XML文件的字节输入流
		BufferedInputStream bis = new BufferedInputStream(new FileInputStream(
				inFileName));
		// 构造一个XML解码器
		XMLDecoder xmlDecoder = new XMLDecoder(bis);
		// 使用XML解码器读对象
		Object obj = xmlDecoder.readObject();
		// 关闭解码器
		xmlDecoder.close();

		return obj;
	}
}
