package jcifs.smb1;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletResponse;

import jcifs.smb1.filter.Smb1FileFilter;
import jcifs.smb1.smb1.SmbFile;

/**
 * SMBClient 客户端接口
 * @author 		： <a href="https://github.com/hiwepy">hiwepy</a>
 */
public interface ISMBClient {
	
	public boolean makeDir(String targetDir) throws Exception;
	
	public void downloadToDir(String sharedDir,String localDir) throws Exception;
	
	public void downloadToDir(String sharedDir,File localDir) throws Exception;
	
	public void downloadToFile(String filepath, String localFile) throws Exception;
	
	public void downloadToFile(String filepath, File localFile) throws Exception;
	
	public void downloadToFile(String sharedDir,String fileName, String localFile) throws Exception;
	
	public void downloadToFile(String sharedDir,String fileName, File localFile) throws Exception;
	
	public void downloadToStream(String filepath,OutputStream output) throws Exception;
	
	public void downloadToStream(String sharedDir,String fileName,OutputStream output) throws Exception;
	
	public void downloadToResponse(String filepath,ServletResponse response) throws Exception;
	
	public void downloadToResponse(String sharedDir,String fileName,ServletResponse response) throws Exception;
	
	public boolean remove(String filepath) throws Exception;
	
	public boolean remove(String[] filepaths) throws Exception;
	
	public boolean remove(String sharedDir,String fileName) throws Exception;
	
	public boolean remove(String sharedDir,String[] ftpFiles) throws Exception;
	
	public boolean removeDir(String sharedDir) throws Exception;
	
	public boolean rename(String filepath,String fileName) throws Exception;
	
	public boolean renameTo(String filepath,String destpath) throws Exception;
	
	public String[] listNames(String sharedDir) throws Exception;
	
	public SmbFile[] listFiles(String sharedDir) throws Exception;
	
	public SmbFile[] listFiles(String sharedDir,boolean recursion) throws Exception;
	
	public SmbFile[] listFiles(String sharedDir, String[] extensions) throws Exception;
	
	public SmbFile[] listFiles(String sharedDir, String[] extensions,boolean recursion) throws Exception;
	
	public SmbFile[] listFiles(String sharedDir, Smb1FileFilter filter) throws Exception;
		
	public SmbFile[] listFiles(String sharedDir, Smb1FileFilter filter,boolean recursion) throws Exception;
	
	public SmbFile getFile(String ftpFilePath) throws Exception;
	
	public SmbFile getFile(String sharedDir,String fileName) throws Exception;
	
	public InputStream getInputStream(String filepath) throws Exception;
	
	public boolean upload(byte[] bytes,String destpath) throws Exception;
	
	public boolean upload(byte[] bytes,String sharedDir,String fileName) throws Exception;
	
	public boolean upload(File localFile,String destpath) throws Exception;
	
	public boolean upload(File localFile,String sharedDir,String fileName) throws Exception;
	
	public boolean upload(InputStream input,String destpath) throws Exception;
	
	public boolean upload(InputStream input,String sharedDir,String fileName) throws Exception;
	
	public boolean upload(String filepath,String destpath) throws Exception;
	
	public boolean upload(String filepath,String sharedDir,String fileName) throws Exception;
	
	public boolean upload(StringBuilder fileContent,String destpath) throws Exception;
	
	public boolean upload(StringBuilder fileContent,String sharedDir,String fileName) throws Exception;
	
	public boolean uploadByChannel(File localFile,String destpath) throws Exception;
	
	public boolean uploadByChannel(File localFile,String sharedDir,String fileName) throws Exception;
	
	public SmbFile1 getSMBClient() throws Exception;
	
	public void releaseClient(SmbFile1 smbClient) throws Exception;
	
}
