package com.baidu.disconfig;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baidu.disconf.client.DisconfMgr;
import com.baidu.disconf.client.addons.properties.DisconfModul;
import com.baidu.disconf.client.store.aspect.DisconfAspectJ;

/**
 * 读取模块配置文件
 * 
 * @author lxs
 *
 */
public class ReadModuleConfigFile {
	 protected static final Logger LOGGER = LoggerFactory.getLogger(DisconfAspectJ.class);
	public static Properties getConfProperties(String fileName)  {
	  	Properties pp = new Properties();
		InputStream inputStream = DisconfMgr.getInstance().getInputStream(fileName);
		try {
			pp.load(inputStream);
			LOGGER.info(String.format("***************************load sccuess %s properties****************************", fileName)); 
	 	}catch (Exception e) {
	 		LOGGER.error("", e); 
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				LOGGER.error("", e); 
			}
		}
		return pp;
	}
}
