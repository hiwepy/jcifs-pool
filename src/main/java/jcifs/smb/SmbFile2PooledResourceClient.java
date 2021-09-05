package jcifs.smb;

import jcifs.smb.pool.SmbFilePool;

/**
 * 基于 Apache Pool2的SMBClient共享文件资源服务客户端实现
 * @author 		： <a href="https://github.com/hiwepy">hiwepy</a>
 */
public class SmbFile2PooledResourceClient extends SmbFile2ResourceClient{
	
	private SmbFilePool clientPool = null;
	private SmbFile2Config clientConfig = null;
	
	public SmbFile2PooledResourceClient(SmbFilePool clientPool, SmbFile2Config clientConfig){
		 this.clientPool = clientPool;
		 this.clientConfig = clientConfig;
	} 
	
	public SmbFile2PooledResourceClient(){
		 
	}
	 
	@Override
	public SmbFile2 getSMBClient() throws Exception {
		//从对象池获取SMBClient对象
		return clientPool.borrowObject();
	}
 
	@Override
	public void releaseClient(SmbFile2 smbClient) throws Exception{
		
		try {
			//释放SMBClient到对象池
			if(smbClient !=null){
				clientPool.returnObject(smbClient);
			}
		} catch (Throwable e) {
			 
		}
		
	}
	
	public SmbFile2Config getClientConfig() {
		return clientConfig;
	}
	
}
