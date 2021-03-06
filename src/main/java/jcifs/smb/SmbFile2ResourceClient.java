package jcifs.smb;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.List;

import javax.servlet.ServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;

import jcifs.smb1.filter.Smb1FileFilter;
import jcifs.utils.Smb2FileUtils;
 
/**
 * 基于ThreadLocal多线程对象复用的SMBClient共享文件资源服务客户端实现
 * @author 		： <a href="https://github.com/hiwepy">hiwepy</a>
 */
public class SmbFile2ResourceClient implements ISMBClient{
	
	private ThreadLocal<SmbFile2> clientThreadLocal = new ThreadLocal<SmbFile2>();  
	private SmbFile2Builder clientBuilder;
	
	public SmbFile2ResourceClient(){
	}
	
	public SmbFile2ResourceClient(SmbFile2Builder builder){
		 this.clientBuilder = builder;
	}
	
	@Override
	public boolean makeDir(String targetDir) throws Exception{
		//获得一个SMBClient对象
		SmbFile2 smbClient = getSMBClient();
		try {
			//当前目录
			SmbFile currentDir = new SmbFile(smbClient,targetDir);
			if(!currentDir.exists()){
				currentDir.mkdirs();
			}
			return true;
		} finally {
			//释放对象  
			releaseClient(smbClient);
        }
	}
	
	public void downloadToDir(String sharedDir,String localDir) throws Exception{
		this.downloadToDir(sharedDir, new File(localDir));
	}
	
	public void downloadToDir(String sharedDir,File localDir) throws Exception{
		//获得一个SMBClient对象
		SmbFile2 smbClient = getSMBClient();
		try {
			//当前共享目录
			SmbFile2 currentDir = new SmbFile2(smbClient, sharedDir.endsWith("/") ? sharedDir : sharedDir + "/");
			//复制共享目录到指定的本地目录
			Smb2FileUtils.retrieveToDir(currentDir, localDir);
        } finally {
			//释放对象  
			releaseClient(smbClient);
        }
	}
	
	@Override
	public void downloadToFile(String filepath, File localFile) throws Exception {
		//获得一个SMBClient对象
		SmbFile2 smbClient = getSMBClient();
		try {
			//源文件
			SmbFile2 smbFile = new SmbFile2( smbClient, filepath);
			//下载共享文件至输出流
			Smb2FileUtils.retrieveToFile(smbFile, localFile);
        } finally {
        	//释放对象  
			releaseClient(smbClient);
        }
	}
	
	@Override
	public void downloadToFile(String filepath, String localFile) throws Exception {
		this.downloadToFile(filepath, new File(localFile));
	}
	
	@Override
	public void downloadToFile(String sharedDir,String fileName, String localFile) throws Exception{
		//解析共享文件路径
		String filepath = FilenameUtils.getFullPath(sharedDir + "/" + fileName) + fileName;
		//存储本地文件到【文件共享服务器】
		this.downloadToFile(filepath, localFile);
	}
	
	@Override
	public void downloadToFile(String sharedDir,String fileName, File localFile) throws Exception{
		//解析共享文件路径
		String filepath = FilenameUtils.getFullPath(sharedDir + "/" + fileName) + fileName;
		//存储本地文件到【文件共享服务器】
		this.downloadToFile(filepath, localFile);
	}

	@Override
	public void downloadToStream(String filepath,OutputStream output) throws Exception {
		//获得一个SMBClient对象
		SmbFile2 smbClient = getSMBClient();
		try {
			//源文件
			SmbFile2 smbFile = new SmbFile2( smbClient, filepath);
			//下载共享文件至输出流
			Smb2FileUtils.retrieveToStream(smbFile, output);
        } finally {
        	//释放对象  
			releaseClient(smbClient);
        }
	}
	
	public void downloadToStream(String sharedDir,String fileName,OutputStream output) throws Exception{
		//解析共享文件路径
		String filepath = FilenameUtils.getFullPath(sharedDir + "/" + fileName) + fileName;
		//存储本地文件到【文件共享服务器】
		this.downloadToStream(filepath, output);
	}
	
	@Override
	public void downloadToResponse(String filepath,ServletResponse response) throws Exception {
		//获得一个SMBClient对象
		SmbFile2 smbClient = getSMBClient();
		try {
			//源文件
			SmbFile2 smbFile = new SmbFile2( smbClient, filepath);
			//下载共享文件至输出流
			Smb2FileUtils.retrieveToResponse(smbFile, response);
        } finally {
        	//释放对象  
			releaseClient(smbClient);
        }
	}
	
	public void downloadToResponse(String sharedDir,String fileName,ServletResponse response) throws Exception{
		//解析共享文件路径
		String filepath = FilenameUtils.getFullPath(sharedDir + "/" + fileName) + fileName;
		//存储本地文件到【文件共享服务器】
		this.downloadToResponse(filepath, response);
	}

	@Override
	public SmbFile getFile(String filepath) throws Exception {
		//获得一个SMBClient对象
		SmbFile2 smbClient = getSMBClient();
		try {
			//源文件
			return new SmbFile2( smbClient, filepath);
        } finally {
        	//释放对象  
			releaseClient(smbClient);
        }
	}
	
	@Override
	public SmbFile getFile(String sharedDir,String fileName) throws Exception {
		//获得一个SMBClient对象
		SmbFile2 smbClient = getSMBClient();
		try { 
			//源文件
			return new SmbFile2( smbClient, sharedDir + "/" + fileName);
		} finally {
			//释放对象  
			releaseClient(smbClient);
        }
	}
	
	@Override
	public InputStream getInputStream(String filepath) throws Exception{
		//获得一个SMBClient对象
		SmbFile2 smbClient = getSMBClient();
		try { 
			return Smb2FileUtils.getInputStream(smbClient, filepath);
		} finally {
			//释放对象  
			releaseClient(smbClient);
        }
	}
	
	@Override
	public String[] listNames(String sharedDir) throws Exception {
		//获得一个SMBClient对象
		SmbFile2 smbClient = getSMBClient();
		try { 
	        return Smb2FileUtils.listNames(smbClient, sharedDir);
		} finally {
			//释放对象  
			releaseClient(smbClient);
        }
	}

	@Override
	public SmbFile2[] listFiles(String sharedDir) throws Exception {
		//获得一个SMBClient对象
		SmbFile2 smbClient = getSMBClient();
		try { 
			//列出当前工作目录的文件信息
			List<SmbFile2> list = Smb2FileUtils.listFiles(smbClient, sharedDir);
	        return list.toArray(new SmbFile2[list.size()] );
		} finally {
			//释放对象  
			releaseClient(smbClient);
        }
	}
	
	@Override
	public SmbFile2[] listFiles(String sharedDir,boolean recursion) throws Exception {
		//获得一个SMBClient对象
		SmbFile2 smbClient = getSMBClient();
		try { 
			//列出当前工作目录的文件信息
			List<SmbFile2> list = Smb2FileUtils.listFiles(smbClient, sharedDir, recursion);
	        return list.toArray(new SmbFile2[list.size()] );
		} finally {
			//释放对象  
			releaseClient(smbClient);
        }
	}

	@Override
	public SmbFile2[] listFiles(String sharedDir, String[] extensions) throws Exception{
		//获得一个SMBClient对象
		SmbFile2 smbClient = getSMBClient();
		try { 
			//列出当前工作目录的文件信息
			List<SmbFile2> list = Smb2FileUtils.listFiles(smbClient, sharedDir, extensions, false);
	        return list.toArray(new SmbFile2[list.size()] );
		} finally {
			//释放对象  
			releaseClient(smbClient);
        }
	}
	
	@Override
	public SmbFile2[] listFiles(String sharedDir, String[] extensions,boolean recursion) throws Exception{
		//获得一个SMBClient对象
		SmbFile2 smbClient = getSMBClient();
		try { 
			//列出当前工作目录的文件信息
			List<SmbFile2> list = Smb2FileUtils.listFiles(smbClient, sharedDir, extensions , recursion);
	        return list.toArray(new SmbFile2[list.size()] );
		} finally {
			//释放对象  
			releaseClient(smbClient);
        }
	}
	
	@Override
	public SmbFile2[] listFiles(String sharedDir, Smb1FileFilter filter) throws Exception{
		//获得一个SMBClient对象
		SmbFile2 smbClient = getSMBClient();
		try { 
			//列出当前工作目录的文件信息
			List<SmbFile2> list = Smb2FileUtils.listFiles(smbClient, sharedDir, filter , false );
	        return list.toArray(new SmbFile2[list.size()] );
		} finally {
			//释放对象  
			releaseClient(smbClient);
        }
	}
		
	@Override
	public SmbFile2[] listFiles(String sharedDir, Smb1FileFilter filter,boolean recursion) throws Exception{
		//获得一个SMBClient对象
		SmbFile2 smbClient = getSMBClient();
		try { 
			//列出当前工作目录的文件信息
			List<SmbFile2> list = Smb2FileUtils.listFiles(smbClient, sharedDir, filter , recursion);
	        return list.toArray(new SmbFile2[list.size()] );
		} finally {
			//释放对象  
			releaseClient(smbClient);
        }
	}
	
	@Override
	public boolean remove(String filepath) throws Exception {
		//获得一个SMBClient对象
		SmbFile2 smbClient = getSMBClient();
		try { 
			return Smb2FileUtils.remove(smbClient, filepath);
		} finally {
			//释放对象  
			releaseClient(smbClient);
        }
	}
	
	@Override
	public boolean remove(String[] filepaths) throws Exception{
		//获得一个SMBClient对象
		SmbFile2 smbClient = getSMBClient();
		try { 
	        return Smb2FileUtils.remove(smbClient, filepaths);
		} finally {
			//释放对象  
			releaseClient(smbClient);
        }
	}
	
	@Override
	public boolean remove(String sharedDir, String fileName) throws Exception {
		//获得一个SMBClient对象
		SmbFile2 smbClient = getSMBClient();
		try { 
			//当前共享目录
			SmbFile2 currentDir = new SmbFile2(smbClient,sharedDir);
			//删除【共享文件】服务器上的一个指定文件
			return Smb2FileUtils.remove(currentDir, fileName);
		} finally {
			//释放对象  
			releaseClient(smbClient);
        }
	}
	
	@Override
	public boolean remove(String sharedDir, String[] fileNames) throws Exception {
		//获得一个SMBClient对象
		SmbFile2 smbClient = getSMBClient();
		try { 
			//当前共享目录
			SmbFile2 currentDir = new SmbFile2(smbClient,sharedDir);
			//删除【共享文件】服务器上的多个指定文件
			return Smb2FileUtils.remove(currentDir, fileNames);
		} finally {
			//释放对象  
			releaseClient(smbClient);
        }
	}
	
	@Override
	public boolean removeDir(String sharedDir) throws Exception {
		//获得一个SMBClient对象
		SmbFile2 smbClient = getSMBClient();
		try { 
			return Smb2FileUtils.removeDir(smbClient, sharedDir);
		} finally {
			//释放对象  
			releaseClient(smbClient);
        }
	}

	public boolean rename(String filepath,String fileName) throws Exception{
		//获得一个SMBClient对象
		SmbFile2 smbClient = getSMBClient();
		try { 
			return Smb2FileUtils.rename(smbClient, filepath, fileName);
		} finally {
			//释放对象  
			releaseClient(smbClient);
        }
	}
	
	public boolean renameTo(String filepath,String destpath) throws Exception{
		//获得一个SMBClient对象
		SmbFile2 smbClient = getSMBClient();
		try { 
			return Smb2FileUtils.renameTo(smbClient, filepath, destpath);
		} finally {
			//释放对象  
			releaseClient(smbClient);
        }
	}
	
	@Override
	public boolean upload(byte[] bytes,String destpath) throws Exception{
		InputStream input = null;
		try {
			input = new ByteArrayInputStream(bytes);
			return this.upload(ByteArrayOutputStream.toBufferedInputStream(input),destpath);
        } finally {
        	//关闭输入流
        	IOUtils.closeQuietly(input);
        }
	}
	
	@Override
	public boolean upload(byte[] bytes,String sharedDir,String fileName) throws Exception{
		//解析共享文件路径
		String destpath = FilenameUtils.getFullPath(sharedDir + "/" + fileName) + fileName;
		//存储本地文件到【文件共享服务器】
		return this.upload(bytes, destpath);
	}
	
	@Override
	public boolean upload(File localFile,String destpath) throws Exception {
		//获得一个SMBClient对象
		SmbFile2 smbClient = getSMBClient();
		try {
			//目标文件
			SmbFile2 sharedFile = new SmbFile2(smbClient,destpath);
			//存储本地文件到【文件共享服务器】
			return Smb2FileUtils.storeFile(localFile , sharedFile);
        } finally {
        	//释放对象  
			releaseClient(smbClient);
        }
	}
	
	@Override
	public boolean upload(File localFile,String sharedDir,String fileName) throws Exception {
		//获得一个SMBClient对象
		SmbFile2 smbClient = getSMBClient();
		try {
			//解析共享文件路径
			String destpath = FilenameUtils.getFullPath(sharedDir + "/" + fileName) + fileName;
			//存储本地文件到【文件共享服务器】
			return Smb2FileUtils.storeFile(localFile, smbClient, destpath, true);
        } finally {
        	//释放对象  
			releaseClient(smbClient);
        }
	}
	
	@Override
	public boolean upload(InputStream input,String destpath) throws Exception {
		//获得一个SMBClient对象
		SmbFile2 smbClient = getSMBClient();
		try {
			return Smb2FileUtils.storeStream(input ,smbClient, destpath);
        } finally {
        	//释放对象  
			releaseClient(smbClient);
        }
	}
	
	@Override
	public boolean upload(InputStream input,String sharedDir,String fileName) throws Exception {
		//解析共享文件路径
		String destpath = FilenameUtils.getFullPath(sharedDir + "/" + fileName) + fileName;
		//存储本地文件到【文件共享服务器】
		return this.upload(input, destpath);
	}
	
	@Override
	public boolean upload(String filepath,String destpath) throws Exception {
		return this.upload(new File(filepath),destpath);
	}

	@Override
	public boolean upload(String filepath,String sharedDir,String fileName) throws Exception {
		return this.upload(new File(filepath),sharedDir, fileName);
	}

	@Override
	public boolean upload(StringBuilder fileContent,String destpath) throws Exception {
		StringReader reader = null;
		try {
			reader = new StringReader(fileContent.toString());
			return this.upload(IOUtils.toByteArray(reader, Charset.defaultCharset()),destpath);
        } finally {
        	//关闭输入流
        	IOUtils.closeQuietly(reader);
        }
	}
	
	@Override
	public boolean upload(StringBuilder fileContent,String sharedDir, String fileName) throws Exception {
		//解析共享文件路径
		String destpath = FilenameUtils.getFullPath(sharedDir + "/" + fileName) + fileName;
		//存储本地文件到【文件共享服务器】
		return this.upload(fileContent, destpath);
	}
	
	@Override
	public boolean uploadByChannel(File localFile,String destpath) throws Exception{
		//获得一个SMBClient对象
		SmbFile2 smbClient = getSMBClient();
		try {
			return Smb2FileUtils.storeFileChannel(localFile ,smbClient, destpath);
        } finally {
        	//释放对象  
			releaseClient(smbClient);
        }
	}
	
	@Override
	public boolean uploadByChannel(File localFile,String sharedDir,String fileName) throws Exception{
		//解析共享文件路径
		String destpath = FilenameUtils.getFullPath(sharedDir + "/" + fileName) + fileName;
		//存储本地文件到【文件共享服务器】
		return this.uploadByChannel(localFile, destpath);
	}
	
	@Override
	public SmbFile2 getSMBClient() throws Exception {
		if (clientBuilder == null) {
			throw new IllegalArgumentException("clientBuilder is null.");
		}
		if (clientThreadLocal.get() != null && !clientThreadLocal.get().getDoInput() && !clientThreadLocal.get().getDoOutput()) {  
            return clientThreadLocal.get();  
        } else {
        	//构造一个SMBClient实例  
        	SmbFile2 smbClient = getClientBuilder().build();
        	//尝试连接 ;SmbFile的connect()方法可以尝试连接远程文件夹，如果账号或密码错误，将抛出连接异常
        	smbClient.connect(); 
    		clientThreadLocal.set(smbClient);
    		return smbClient;
        }
	}
 
	@Override
	public void releaseClient(SmbFile2 smbClient) throws Exception{
		//断开连接  
		//SMBConnectUtils.releaseConnect(smbClient);
	}

	public void setClientBuilder(SmbFile2Builder clientBuilder) {
		this.clientBuilder = clientBuilder;
	}

	public SmbFile2Builder getClientBuilder() {
		return clientBuilder;
	}
	
	
}
