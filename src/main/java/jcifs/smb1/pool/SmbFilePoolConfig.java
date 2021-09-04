package jcifs.smb1.pool;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import jcifs.smb1.SmbFile1;
/**
 * 基于apache-pool2的对象池初始化对象
 * @author 		： <a href="https://github.com/hiwepy">hiwepy</a>
 */
public class SmbFilePoolConfig extends GenericObjectPoolConfig<SmbFile1> {
	
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
