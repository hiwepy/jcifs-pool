package jcifs.smb.pool;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import jcifs.smb.SmbFile;

/**
 * 基于apache-pool2的对象池初始化对象
 */
public class SmbFilePoolConfig extends GenericObjectPoolConfig<SmbFile> {
	
}
