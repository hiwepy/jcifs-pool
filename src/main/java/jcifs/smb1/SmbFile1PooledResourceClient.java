package jcifs.smb1;


import jcifs.smb1.pool.SmbFilePool;
 
/**
 * 基于 Apache Pool2的SMBClient共享文件资源服务客户端实现
 * @author 		： <a href="https://github.com/hiwepy">hiwepy</a>
 */
public class SmbFile1PooledResourceClient extends SmbFile1ResourceClient{
	
	private SmbFilePool clientPool = null;
	private SmbFile1Config clientConfig = null;
	
	public SmbFile1PooledResourceClient(SmbFilePool clientPool, SmbFile1Config clientConfig){
		 this.clientPool = clientPool;
		 this.clientConfig = clientConfig;
	} 
	
	public SmbFile1PooledResourceClient(){
		 
	}
	 
	@Override
	public SmbFile1 getSMBClient() throws Exception {
		//从对象池获取SMBClient对象
		return clientPool.borrowObject();
	}
 
	@Override
	public void releaseClient(SmbFile1 smbClient) throws Exception{
		
		try {
			//释放SMBClient到对象池
			if(smbClient !=null){
				clientPool.returnObject(smbClient);
			}
		} catch (Throwable e) {
			 
		}
		
	}
	
	public SmbFile1Config getClientConfig() {
		return clientConfig;
	}
	
}
