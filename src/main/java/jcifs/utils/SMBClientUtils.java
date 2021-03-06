package jcifs.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.ServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jcifs.smb1.SmbFile1;
import jcifs.smb1.filter.Smb1FileFilter;
import jcifs.smb1.smb1.SmbFile;

public class SMBClientUtils {

protected static Logger LOG = LoggerFactory.getLogger(SMBClientUtils.class);
	
	/**
	 * 追加文件至【文件共享服务器】
	 * @param smbClient		： SMBClient对象
	 * @param filepath		：相对SMBClient路径的文件路径
	 * @param localFile		：文件
	 * @return
	 * @throws IOException
	 */
	public static boolean appendFile(SmbFile1 sharedFile, File localFile) throws IOException{
		InputStream input = null;
		try {
			//异常检查
			Smb1Assert.assertAppend(localFile,sharedFile);
			// 包装文件输入流  
			input = SmbFile1StreamUtils.toBufferedInputStream(localFile, sharedFile.getBufferSize());
			//断点上传输入流
			return SMBClientUtils.appendStream(sharedFile, input, Math.max(0, localFile.length() - sharedFile.getContentLength()));
		} finally {
        	//关闭输入通道
        	IOUtils.closeQuietly(input);
        }
	}
	
	/**
	 * 追加文件至【文件共享服务器】
	 * @param smbClient		： SMBClient对象
	 * @param filepath		：相对SMBClient路径的文件路径
	 * @param localFile		：文件
	 * @return
	 * @throws IOException
	 */
	public static boolean appendFile(SmbFile1 smbClient,String filepath, File localFile) throws IOException{
		InputStream input = null;
		try {
			//当前文件
			SmbFile1 sharedFile = smbClient.get(filepath);
			//异常检查
			Smb1Assert.assertAppend(localFile, sharedFile);
			// 包装文件输入流  
			input = SmbFile1StreamUtils.toBufferedInputStream(localFile, smbClient.getBufferSize());
			//断点上传输入流
			return SMBClientUtils.appendStream(sharedFile, input, Math.max(0, localFile.length() - sharedFile.getContentLength()));
		} finally {
        	//关闭输入通道
        	IOUtils.closeQuietly(input);
        }
	}
	
	/**
	 * 
	 * 追加文件至【文件共享服务器】
	 * @param smbClient		： SMBClient对象
	 * @param filepath		：相对SMBClient路径的文件路径
	 * @param localFile		：文件
	 * @param skipOffset	：跳过已经存在的长度
	 * @return
	 * @throws IOException
	 */
	public static boolean appendFile(SmbFile1 smbClient,String filepath, File localFile,long skipOffset) throws IOException{
		InputStream input = null;
		try {
			//当前文件
			SmbFile1 sharedFile = smbClient.get(filepath);
			//异常检查
			Smb1Assert.assertAppend(localFile, sharedFile, skipOffset);
			// 包装文件输入流  
			input = SmbFile1StreamUtils.toBufferedInputStream(localFile, smbClient.getBufferSize());
			//断点上传输入流
			return SMBClientUtils.appendStream(sharedFile, input, skipOffset);
		} finally {
        	//关闭输入通道
        	IOUtils.closeQuietly(input);
        }
	}
	
	/**
	 * 
	 * 追加输入流至【文件共享服务器】
	 * @param sharedFile		： SMBClient对象
	 * @param input			：输入流
	 * @param skipOffset	：跳过已经存在的长度
	 * @return
	 * @throws IOException
	 */
	public static boolean appendStream(SmbFile1 sharedFile,InputStream input) throws IOException{
		try {
			//断点上传输入流
			return SMBClientUtils.appendStream(sharedFile, input, sharedFile.getContentLength());
		} finally {
        	//关闭输入通道
        	IOUtils.closeQuietly(input);
        }
	}
	
	/**
	 * 
	 * 追加输入流至【文件共享服务器】
	 * @param sharedFile		： SMBClient对象
	 * @param input			：输入流
	 * @param skipOffset	：跳过已经存在的长度
	 * @return
	 * @throws IOException
	 */
	public static boolean appendStream(SmbFile1 sharedFile,InputStream input,long skipOffset) throws IOException{
		try {
			//异常检查
			Smb1Assert.assertFile(sharedFile);
			//初始进度监听
			SMBCopyListenerUtils.initCopyListener(sharedFile, sharedFile.getName());
			// 追加文件内容
			long totalRead = SmbFile1StreamUtils.copyLarge(input, sharedFile, skipOffset);
			//异常检查
			return Smb1Assert.assertRead(totalRead,sharedFile.getURL().getPath());
		} finally {
        	//关闭输入通道
        	IOUtils.closeQuietly(input);
        }
	}
	
	/**
	 * 
	 * 追加输入流至【文件共享服务器】
	 * @param smbClient		： SMBClient对象
	 * @param filepath		：相对SMBClient路径的文件路径
	 * @param input			：输入流
	 * @param skipOffset	：跳过已经存在的长度
	 * @return
	 * @throws IOException
	 */
	public static boolean appendStream(SmbFile1 smbClient,String filepath,InputStream input,long skipOffset) throws IOException{
		try {
			//当前文件
			SmbFile1 sharedFile = smbClient.get(filepath);
			//异常检查
			Smb1Assert.assertFile(sharedFile);
			//初始进度监听
			SMBCopyListenerUtils.initCopyListener(sharedFile, sharedFile.getName());
			// 追加文件内容
			long totalRead = SmbFile1StreamUtils.copyLarge(input, sharedFile , skipOffset);
			//异常检查
			return Smb1Assert.assertRead(totalRead,filepath);
		} finally {
        	//关闭输入通道
        	IOUtils.closeQuietly(input);
        }
	}
	
	/**
	 * 
	 * 切换目录至指定目录，如果指定目录不存在创建目录，并返回结果标志
	 * @param smbClient
	 * @param targetDir
	 * @return
	 * @throws IOException
	 */
	public static SmbFile1 changeExistsDir(SmbFile1 smbClient,String targetDir) throws IOException{
		if(targetDir == null){
			return smbClient;
		}
		//当前目录
		SmbFile1 currentDir = new SmbFile1(smbClient,targetDir);
		if(!currentDir.exists()){
			currentDir.mkdirs();
		}
		return currentDir;
	}
	
	public static String[] listNames(SmbFile1 smbClient,String sharedDir) throws IOException{
		//当前目录
		SmbFile1 currentDir = new SmbFile1(smbClient,SMBPathUtils.getSharedDir(sharedDir));
		//异常检查
		Smb1Assert.assertDir(currentDir);
		//返回文件名数组
		return currentDir.list();
	}
	
	public static List<SmbFile1> listFiles(SmbFile1 sharedDir) throws IOException{
		return listFiles(sharedDir, false);
	}
	
	public static List<SmbFile1> listFiles(SmbFile1 sharedDir,boolean recursion) throws IOException{
		//异常检查
		Smb1Assert.assertDir(sharedDir);
		//创建文件类型的文件集合
		List<SmbFile1> fileList = new ArrayList<SmbFile1>();
		//列出当前工作目录的文件信息
		SmbFile[] files = sharedDir.listFiles();
		//循环共享文件
		for(SmbFile sharedFile : files){
			if (sharedFile.isDirectory() ) {
				if( recursion){
					fileList.addAll(SMBClientUtils.listFiles(sharedDir, sharedFile.getName()));
				}else {
					fileList.add(sharedDir.wrap(sharedFile));
				}
			} else{
				fileList.add(sharedDir.wrap(sharedFile));
			}
		}
		return fileList;
	}
	
	public static List<SmbFile1> listFiles(SmbFile1 smbClient,String sharedDir) throws IOException{
		return listFiles(smbClient, sharedDir, false);
	}
	
	public static List<SmbFile1> listFiles(SmbFile1 smbClient,String sharedDir,boolean recursion) throws IOException{
		//当前目录
		SmbFile1 currentDir = new SmbFile1(smbClient,SMBPathUtils.getSharedDir(sharedDir));
		//异常检查
		Smb1Assert.assertDir(currentDir);
		//创建文件类型的文件集合
		List<SmbFile1> fileList = new ArrayList<SmbFile1>();
		//列出当前工作目录的文件信息
		SmbFile[] files = currentDir.listFiles();
		//循环共享文件
		for(SmbFile sharedFile : files){
			if (sharedFile.isDirectory() ) {
				if(recursion){
					fileList.addAll(SMBClientUtils.listFiles(currentDir, sharedFile.getName()));
				}else{
					fileList.add(smbClient.wrap(sharedFile));
				}
			} else{
				fileList.add(smbClient.wrap(sharedFile));
			}
		}
		return fileList;
	}
	
	public static List<SmbFile1> listFiles(SmbFile1 smbClient, String sharedDir, String[] extensions, boolean recursion) throws IOException {
		//当前目录
		SmbFile1 currentDir = new SmbFile1(smbClient,SMBPathUtils.getSharedDir(sharedDir));
		//异常检查
		Smb1Assert.assertDir(currentDir);
		//创建文件类型的文件集合
		List<SmbFile1> fileList = new ArrayList<SmbFile1>();
		//列出当前工作目录的文件信息
		Collection<SmbFile> files = Smb1FileUtils.listFiles(currentDir.listFiles(), extensions);
		if(files != null && files.size() > 0){
			//循环共享文件
			for(SmbFile sharedFile : files){
				if (sharedFile.isDirectory() ) {
					if(recursion){
						fileList.addAll( smbClient.wrapAll(Smb1FileUtils.listFiles( sharedFile, extensions, recursion)) );
					}else{
						fileList.add( smbClient.wrap(sharedFile));
					}
				} else{
					fileList.add(smbClient.wrap(sharedFile));
				}
			}
		}
		return fileList;
	}
	
	public static List<SmbFile1> listFiles(SmbFile1 smbClient, String sharedDir, Smb1FileFilter filter, boolean recursion) throws IOException {
		//当前目录
		SmbFile1 currentDir = new SmbFile1(smbClient,SMBPathUtils.getSharedDir(sharedDir));
		//异常检查
		Smb1Assert.assertDir(currentDir);
		//创建文件类型的文件集合
		List<SmbFile1> fileList = new ArrayList<SmbFile1>();
		//列出当前工作目录的文件信息
		Collection<SmbFile> files = Smb1FileUtils.listFiles(currentDir.listFiles(), filter);
		if(files != null && files.size() > 0){
			//循环共享文件
			for(SmbFile sharedFile : files){
				if (sharedFile.isDirectory() ) {
					if(recursion){
						fileList.addAll( smbClient.wrapAll(Smb1FileUtils.listFiles( sharedFile, filter, recursion)) );
					}else{
						fileList.add( smbClient.wrap(sharedFile));
					}
				} else{
					fileList.add(smbClient.wrap(sharedFile));
				}
			}
		}
		return fileList;
	}
	
	/**
	 * 
	 * 循环创建目录，并且创建完目录后，设置工作目录为当前创建的目录下
	 * @param smbClient
	 * @param targetDir
	 * @return
	 * @throws IOException
	 */
	public static boolean makeDir(SmbFile1 smbClient,String targetDir) throws IOException{
		SmbFile sharedDir = new SmbFile(smbClient,targetDir);
		//验证是否有该文件夹，有就转到，没有创建后转到该目录下
		if (sharedDir.exists()) {
			return true;
		} 
		sharedDir.mkdirs();
		return true;
	}
	
	public static InputStream getInputStream(SmbFile1 sharedFile,long skipOffset) throws IOException{
		//异常检查
		Smb1Assert.assertGet(sharedFile,skipOffset);
		//设置接收数据流的起始位置
		sharedFile.setRestartOffset(skipOffset);
		//获得InputStream
		return SmbFile1StreamUtils.toBufferedInputStream(sharedFile.getInputStream(), sharedFile.getBufferSize());
	}
	
	public static InputStream getInputStream(SmbFile1 smbClient,String filepath) throws IOException{
		//共享文件
		SmbFile1 sharedFile = smbClient.get(filepath);
		//异常检查
		Smb1Assert.assertFile(sharedFile);
		//获得InputStream
		return SmbFile1StreamUtils.toBufferedInputStream(sharedFile.getInputStream(), smbClient.getBufferSize());
	}
	
	public static InputStream getInputStream(SmbFile1 smbClient,String filepath,long skipOffset) throws IOException{
		//共享文件
		SmbFile1 sharedFile = smbClient.get(filepath);
		//异常检查
		Smb1Assert.assertGet(sharedFile,skipOffset);
		//设置接收数据流的起始位置
		sharedFile.setRestartOffset(skipOffset);
		//获得InputStream
		return SmbFile1StreamUtils.toBufferedInputStream(sharedFile.getInputStream(), smbClient.getBufferSize());
	}
	
	public static boolean remove(SmbFile1 sharedDir,String filepath) throws IOException {
		//当前文件
		SmbFile sharedFile = new SmbFile(sharedDir,filepath);
		//删除【共享文件】服务器上的一个指定文件
		if(sharedFile.exists()){
			sharedFile.delete();
		}
		return true;
	}
	
	public static boolean remove(SmbFile1 sharedDir, String[] fileNames) throws IOException {
		//异常检查
		Smb1Assert.assertDir(sharedDir);
		//循环要删除的文件列表
		for(String fileName:fileNames){
			//删除【共享文件】服务器上的一个指定文件
			SMBClientUtils.remove(sharedDir, fileName);
		}
        return true;
	}
	
	public static boolean rename(SmbFile1 smbClient,String filepath,String filename) throws Exception{
		//共享文件
		SmbFile1 sharedFile = smbClient.get(filepath);
		//异常检查
		Smb1Assert.assertFile(sharedFile);
		//新的路径
		String destpath = FilenameUtils.getFullPath(sharedFile.getURL().getPath())  + FilenameUtils.getBaseName(filename) + "." + StringUtils.defaultIfBlank(FilenameUtils.getExtension(filename), FilenameUtils.getExtension(filepath));
		//移动或重命名
		sharedFile.renameTo(new SmbFile(smbClient,destpath.substring(smbClient.getURL().getPath().length())));
		return true;
	}
	
	public static boolean renameTo(SmbFile1 smbClient,String filepath,String destpath) throws Exception{
		//共享文件
		SmbFile1 sharedFile = smbClient.get(filepath);
		//异常检查
		Smb1Assert.assertFile(sharedFile);
		//移动或重命名
		sharedFile.renameTo(new SmbFile(smbClient,destpath));
		return true;
	}
	
	// delete all subDirectory and files.
	public static boolean removeDir(SmbFile1 smbClient,String sharedDir) throws IOException {
		//当前目录
		SmbFile1 currentDir = new SmbFile1(smbClient,SMBPathUtils.getSharedDir(sharedDir));
		//异常检查
		Smb1Assert.assertDir(currentDir);
		try {
			currentDir.delete();
		} catch (Exception e) {
			SmbFile[] sharedFileArr = currentDir.listFiles();
			for (SmbFile sharedFile : sharedFileArr) {
				if (sharedFile.isDirectory()) {
					LOG.info("Delete subDir ["+ sharedFile.getURL().getPath() +"]");				
					SMBClientUtils.removeDir(smbClient, sharedDir + "/" + sharedFile.getName());
				} else{
					sharedFile.delete();
				}
			}
		}
		return true;
	}
	
	public static void retrieveToDir(SmbFile1 sharedDir,File localDir) throws Exception{
		//异常检查
		Smb1Assert.assertDir(sharedDir);
		//遍历当前目录下的文件
		List<SmbFile1> fileList = SMBClientUtils.listFiles(sharedDir);
		//循环下载文件
		for(SmbFile sharedFile :fileList){
			if(sharedFile.isDirectory()){
				File newDir = new File(localDir ,sharedFile.getName());
				if (!newDir.exists()) {
					newDir.mkdirs();
				}
			}else{
				//写SmbFile到指定文件路径
				SMBClientUtils.retrieveToFile(sharedDir.wrap(sharedFile), new File(localDir, sharedFile.getName()));
			}
		}
	}
	
	@SuppressWarnings("resource")
	public static void retrieveToFile(SmbFile1 sharedFile,File localFile) throws IOException{
		//异常检查
		Smb1Assert.assertGet(sharedFile, localFile);
		InputStream input = null;
		FileChannel outChannel = null;
		try {
			if(!localFile.exists()){
				File dir = localFile.getParentFile();
				if(!dir.exists()){
					dir.mkdirs();
				}
				localFile.setReadable(true);
				localFile.setWritable(true);
				localFile.createNewFile();
			}
			//先按照“rw”访问模式打开localFile文件，如果这个文件还不存在，RandomAccessFile的构造方法会创建该文件
			//其中的“rws”参数中，rw代表读写方式，s代表同步方式，也就是锁。这种方式打开的文件，就是独占方式。
			//RandomAccessFile不支持只写模式，因为把参数设为“w”是非法的        
			outChannel = new RandomAccessFile(localFile,"rws").getChannel();
			//初始进度监听
			SMBCopyListenerUtils.initCopyListener(sharedFile, localFile.getName());
			//获得InputStream
			input = SMBClientUtils.getInputStream(sharedFile,outChannel.size());
			//将InputStream写到FileChannel
			SMBChannelUtils.copyLarge(input, outChannel ,sharedFile);
		} finally {
        	//关闭输入流
        	IOUtils.closeQuietly(input);
        	//关闭输出通道
        	IOUtils.closeQuietly(outChannel);
        	//恢复起始位
        	sharedFile.setRestartOffset(0); 
        }
	}
	
	public static void retrieveToStream(SmbFile1 sharedFile,OutputStream output) throws IOException {
		try {
			//异常检查
			Smb1Assert.assertFile(sharedFile);
			//初始进度监听
			SMBCopyListenerUtils.initCopyListener(sharedFile, sharedFile.getName());
			// 追加文件内容
			long totalRead = SmbFile1StreamUtils.copyLarge(sharedFile, output);
			//异常检查
			Smb1Assert.assertRead(totalRead,sharedFile.getURL().getPath());
        } finally {
        	//关闭输出流
        	IOUtils.closeQuietly(output);
        	//恢复起始位
        	sharedFile.setRestartOffset(0); 
        }
	}
	
	public static void retrieveToResponse(SmbFile1 sharedFile, ServletResponse response) throws IOException {
		try {
			//异常检查
			Smb1Assert.assertFile(sharedFile);
			//初始进度监听
			SMBCopyListenerUtils.initCopyListener(sharedFile, sharedFile.getName());
			// 追加文件内容
			long totalRead = SmbFile1StreamUtils.copyLarge(sharedFile, response.getOutputStream());
			//异常检查
			Smb1Assert.assertRead(totalRead,sharedFile.getURL().getPath());
        } finally {
        	//恢复起始位
        	sharedFile.setRestartOffset(0); 
        }
	}
	
	/**
	 * 
	 * 上传文件至【文件共享服务器】
	 * @param localFile		：本地文件
	 * @param sharedFile	：共享文件
	 * @return
	 * @throws IOException
	 */
	public static boolean storeFile(File localFile,SmbFile1 sharedFile) throws IOException{
		//异常检查
		Smb1Assert.assertFile(localFile);
		InputStream input = null;
		try {
			//如果共享文件存在，则先删除
			if(sharedFile.exists()){
				sharedFile.delete();
			}
			//初始进度监听
			SMBCopyListenerUtils.initCopyListener(sharedFile, localFile.getName());
			// 包装文件输入流  
			input = SmbFile1StreamUtils.toBufferedInputStream(localFile, sharedFile.getBufferSize());
			// 拷贝文件内容
			long totalRead = SmbFile1StreamUtils.copyLarge(input, sharedFile);
			//异常检查
			return Smb1Assert.assertRead(totalRead,sharedFile.getURL().getPath());
        } finally {
        	//关闭输入通道
        	IOUtils.closeQuietly(input);
        }
	}
	
	/**
	 * 
	 * 上传文件至【文件共享服务器】
	 * @param localFile		：本地文件
	 * @param sharedFile	：共享文件
	 * @param delIfExists	：如果文件存在是否删除
	 * @return
	 * @throws IOException
	 */
	public static boolean storeFile(File localFile,SmbFile1 sharedFile,boolean delIfExists) throws IOException {
		//异常检查
		Smb1Assert.assertFile(localFile);
		//同名文件存在，要求删除
		if(delIfExists){
			//上传完整文件
			return SMBClientUtils.storeFile(localFile,sharedFile);
		}else{
			if(sharedFile.exists()){
				//断点上传文件
				return SMBClientUtils.appendFile(sharedFile,localFile);
			}
			//上传完整文件
			return SMBClientUtils.storeFile(localFile,sharedFile);
		}
	}
	
	
	/**
	 * 
	 * 上传文件至【文件共享服务器】
	 * @param smbClient		： SMBClient对象
	 * @param filepath		：共享文件路径
	 * @param localFile		：本地文件
	 * @param delIfExists	：如果文件存在是否删除
	 * @return
	 * @throws IOException
	 */
	public static boolean storeFile(File localFile,SmbFile1 smbClient,String filepath,boolean delIfExists) throws IOException {
		//共享文件
		SmbFile1 sharedFile = smbClient.get(filepath);
		//上传文件
		return SMBClientUtils.storeFile(localFile, sharedFile, delIfExists);
	}
	
	/**
	 * 
	 * 采用NOI上传文件至【文件共享服务器】
	 * @param inChannel		：文件通道
	 * @param sharedFile	：共享文件
	 * @return
	 * @throws IOException
	 */
	public static boolean storeFileChannel(FileChannel inChannel,SmbFile1 sharedFile) throws IOException {
		OutputStream output = null;
		try {
			//如果共享文件存在，则先删除
			if(sharedFile.exists()){
				sharedFile.delete();
			}
			//初始进度监听
			SMBCopyListenerUtils.initCopyListener(sharedFile, sharedFile.getName());
			//获得OutputStream
			output = SmbFile1StreamUtils.toBufferedOutputStream(sharedFile.getOutputStream(), sharedFile.getBufferSize());
			//从FileChannel中读取数据写出到OutputStream
			return SMBChannelUtils.copyLarge(inChannel, output, sharedFile);
        } finally {
        	//关闭输入通道
        	IOUtils.closeQuietly(inChannel);
        	//关闭输出流
        	IOUtils.closeQuietly(output);
        }
	}
	
	/**
	 * 
	 *  采用NOI上传文件至【文件共享服务器】
	 * @param inChannel		：文件通道
	 * @param sharedFile	：共享文件
	 * @param delIfExists	：如果文件存在是否删除
	 * @return
	 * @throws IOException
	 */
	public static boolean storeFileChannel(FileChannel inChannel,SmbFile1 sharedFile,boolean delIfExists) throws IOException {
		OutputStream output = null;
		try {
			if(delIfExists){
				return SMBClientUtils.storeFileChannel(inChannel, sharedFile);
			}
			if(sharedFile.exists()){
				//跳过指定的长度,实现断点续传
				SmbFile1StreamUtils.skip(inChannel, sharedFile.getContentLength());
			}
			//初始进度监听
			SMBCopyListenerUtils.initCopyListener(sharedFile, sharedFile.getName());
			//获得OutputStream
			output = SmbFile1StreamUtils.toBufferedOutputStream(sharedFile.getOutputStream(), sharedFile.getBufferSize());
			//从FileChannel中读取数据写出到OutputStream
			return SMBChannelUtils.copyLarge(inChannel, output, sharedFile);
        } finally {
        	//关闭输入通道
        	IOUtils.closeQuietly(inChannel);
        	//关闭输出流
        	IOUtils.closeQuietly(output);
        }
	}
	
	/**
	 * 
	 * 采用NOI上传文件至【文件共享服务器】
	 * @param localFile		：本地文件
	 * @param sharedFile	：共享文件
	 * @return
	 * @throws IOException
	 */
	@SuppressWarnings("resource")
	public static boolean storeFileChannel(File localFile,SmbFile1 sharedFile) throws IOException {
		//异常检查
		Smb1Assert.assertFile(localFile);
		FileChannel  inChannel = null;
		try {
			//先按照“rw”访问模式打开localFile文件，如果这个文件还不存在，RandomAccessFile的构造方法会创建该文件
			//其中的“rws”参数中，rw代表读写方式，s代表同步方式，也就是锁。这种方式打开的文件，就是独占方式。
			//RandomAccessFile不支持只写模式，因为把参数设为“w”是非法的        
			inChannel = new RandomAccessFile(localFile,"rws").getChannel();
			//初始进度监听
			SMBCopyListenerUtils.initCopyListener(sharedFile, localFile.getName());
			//从FileChannel中读取数据写出到OutputStream
			return SMBClientUtils.storeFileChannel(inChannel, sharedFile);
        } finally {
        	//关闭输入通道
        	IOUtils.closeQuietly(inChannel);
        }
	}
	
	/**
	 * 
	 * 采用NOI上传文件至【文件共享服务器】
	 * @param localFile		：本地文件
	 * @param sharedFile	：共享文件
	 * @param delIfExists	：如果文件存在是否删除
	 * @return
	 * @throws IOException
	 */
	@SuppressWarnings("resource")
	public static boolean storeFileChannel(File localFile,SmbFile1 sharedFile,boolean delIfExists) throws IOException {
		//异常检查
		Smb1Assert.assertFile(localFile);
		FileChannel  inChannel = null;
		try {
			//先按照“rw”访问模式打开localFile文件，如果这个文件还不存在，RandomAccessFile的构造方法会创建该文件
			//其中的“rws”参数中，rw代表读写方式，s代表同步方式，也就是锁。这种方式打开的文件，就是独占方式。
			//RandomAccessFile不支持只写模式，因为把参数设为“w”是非法的        
			inChannel = new RandomAccessFile(localFile,"rws").getChannel();
			//初始进度监听
			SMBCopyListenerUtils.initCopyListener(sharedFile, localFile.getName());
			//从FileChannel中读取数据写出到OutputStream
			return SMBClientUtils.storeFileChannel(inChannel, sharedFile, delIfExists);
        } finally {
        	//关闭输入通道
        	IOUtils.closeQuietly(inChannel);
        }
	}
	
	/**
	 * 
	 * 采用NOI上传文件至【文件共享服务器】
	 * @param localFile		：本地文件
	 * @param smbClient		： SMBClient对象
	 * @param filepath		：共享文件路径
	 * @return
	 * @throws IOException
	 */
	public static boolean storeFileChannel(File localFile,SmbFile1 smbClient,String filepath) throws IOException {
		//共享文件
		SmbFile1 sharedFile = smbClient.get(filepath);
		//从FileChannel中读取数据写出到OutputStream
		return SMBClientUtils.storeFileChannel(localFile, sharedFile);
	}
	
	/**
	 * 
	 * 采用NOI上传文件至【文件共享服务器】
	 * @param localFile		：本地文件
	 * @param smbClient		： SMBClient对象
	 * @param filepath		：共享文件路径
	 * @param delIfExists	：如果文件存在是否删除
	 * @return
	 * @throws IOException
	 */
	public static boolean storeFileChannel(File localFile,SmbFile1 smbClient,String filepath,boolean delIfExists) throws IOException {
		//共享文件
		SmbFile1 sharedFile = smbClient.get(filepath);
		//从FileChannel中读取数据写出到OutputStream
		return SMBClientUtils.storeFileChannel(localFile, sharedFile, delIfExists);
	}
	
	public static boolean storeStream(InputStream input,SmbFile1 sharedFile) throws IOException{
		try {
			//清除原文件
			if(sharedFile.exists()){
				sharedFile.delete();
				sharedFile.createNewFile();
			}
			//初始进度监听
			SMBCopyListenerUtils.initCopyListener(sharedFile, sharedFile.getName());
			//拷贝文件内容
			long totalRead = SmbFile1StreamUtils.copyLarge(input, sharedFile);
			//异常检查
			return Smb1Assert.assertRead(totalRead,sharedFile.getURL().getPath());
		} finally {
        	//关闭输入通道
        	IOUtils.closeQuietly(input);
        }
	}
	
	/**
	 * 
	 * 上传输入流至【文件共享服务器】
	 * @param smbClient
	 * @param filepath
	 * @param input
	 * @return
	 * @throws IOException
	 */
	public static boolean storeStream(InputStream input,SmbFile1 smbClient,String filepath) throws IOException{
		try {
			//共享文件
			SmbFile1 sharedFile = smbClient.get(filepath);
			//从InputStream中读取数据写出共享服务器
			return SMBClientUtils.storeStream(input, sharedFile);
        } finally {
        	//关闭输入通道
        	IOUtils.closeQuietly(input);
        }
	}

	/**
	 * 
	 * 上传输入流至【文件共享服务器】
	 * @param input			：输入流
	 * @param smbClient		： SMBClient对象
	 * @param filepath		：文件路径
	 * @param delIfExists	：如果文件存在是否删除
	 * @return
	 * @throws IOException
	 */
	public static boolean storeStream(InputStream input,SmbFile1 smbClient,String filepath, boolean delIfExists) throws IOException {
		try {
			if(delIfExists){
				return SMBClientUtils.storeStream(input, smbClient ,filepath);
			}
			//共享文件
			SmbFile1 sharedFile = smbClient.get(filepath);
			if(sharedFile.exists()){
				//追加文件内容
				return SMBClientUtils.appendStream(sharedFile, input);
			}
			//初始进度监听
			SMBCopyListenerUtils.initCopyListener(sharedFile, sharedFile.getName());
			//拷贝文件内容
			long totalRead = SmbFile1StreamUtils.copyLarge(input, sharedFile);
			//异常检查
			return Smb1Assert.assertRead(totalRead,sharedFile.getURL().getPath());
			
        } finally {
        	//关闭输入流
        	IOUtils.closeQuietly(input);
        }
	}
	
}
