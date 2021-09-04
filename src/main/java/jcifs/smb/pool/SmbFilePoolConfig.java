package jcifs.smb.pool;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import jcifs.smb.SmbFile2;

/**
 * 基于apache-pool2的对象池初始化对象
 */
public class SmbFilePoolConfig extends GenericObjectPoolConfig<SmbFile2> {
	
	/**
	 * If the SMBClient Pool should be enabled or not
	 */
	private boolean enabled = false;

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	
}
