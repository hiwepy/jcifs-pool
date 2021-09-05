package jcifs.smb.pool;

import org.apache.commons.pool2.impl.GenericObjectPool;

import jcifs.smb.SmbFile;

/**
 * SMBClient连接池
 * @author 		： <a href="https://github.com/hiwepy">hiwepy</a>
 */
public class SmbFilePool extends GenericObjectPool<SmbFile> {

	/**
	 * 初始化连接池，需要注入一个工厂来提供SMBClient实例和连接池初始化对象
	 * @param factory
	 * @param config
	 */
	public SmbFilePool(SmbFilePooledFactory factory, SmbFilePoolConfig config){
		super(factory,config);
	}

}