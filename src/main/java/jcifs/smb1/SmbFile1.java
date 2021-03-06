package jcifs.smb1;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jcifs.io.CopyStreamProcessListener;
import jcifs.smb1.smb1.NtlmPasswordAuthentication;
import jcifs.smb1.smb1.SmbFile;
import jcifs.smb1.smb1.SmbFileInputStream;

public class SmbFile1 extends SmbFile {
	
	/** 默认缓存大小： 8M */
	public static final int DEFAULT_BUFFER_SIZE = 8 * 1024 * 1024;
	/** 默认FileChannel缓存大小： 2M */
	public static final int DEFAULT_CHANNEL_SIZE = 2 * 1024 * 1024;
	
	protected static Logger LOG = LoggerFactory.getLogger(SmbFile1.class);
	/** 启用或禁用数据流方式上传/下载时是否在缓冲发送/接收完成自动刷新缓存区；大文件上传下载时比较有用;默认false */
	protected boolean autoFlush = false;
	/** 数据流方式上传/下载时缓存区达到可自动刷新的最小阀值；仅当 autoflush 为true 时才有效；默认与默认缓存区大小相同即 8M */
	protected int autoFlushBlockSize = DEFAULT_BUFFER_SIZE;
	/** 内部缓冲区大小;默认 8M */
	protected int bufferSize = DEFAULT_BUFFER_SIZE;
	/** 文件通道读取缓冲区大小;默认 2M */
	protected int channelReadBufferSize = DEFAULT_CHANNEL_SIZE;
	/** 文件通道写出缓冲区大小;默认 2M */
	protected int channelWriteBufferSize = DEFAULT_CHANNEL_SIZE;
	/** 进行存储时/检索操作时数据处理进度监听对象 */
	protected CopyStreamProcessListener copyStreamProcessListener;
	/** 是否使用Log4j记录命令信息,默认打印出命令，如果开启日志则关闭打印;默认 false */
	protected boolean logDebug = false;
	/** 读或写的起始位置 */
	protected long restartOffset = 0;

	public SmbFile1(String url) throws MalformedURLException {
		super(url);
	}
	
	public SmbFile1(String url,SmbFile1Config config) throws MalformedURLException {
		super(url);
		//拷贝初始参数
		copy(this,config);
	}

	public SmbFile1(URL url) {
		super(url);
	}

	public SmbFile1( String url, NtlmPasswordAuthentication auth ) throws MalformedURLException {
		super(url, auth);
	}
	
	public SmbFile1(String url, NtlmPasswordAuthentication auth ,SmbFile1Config config) throws MalformedURLException {
		super(url, auth);
		//拷贝初始参数
		copy(this,config);
	}
	
	public SmbFile1(SmbFile1 context, String name) throws MalformedURLException, UnknownHostException {
		super(context, name);
		//拷贝初始参数
		copy(context,this);
	}
	 
	public SmbFile1 wrap(SmbFile smbFile) {
		SmbFile1 newClient = new SmbFile1(smbFile.getURL());
		//拷贝初始参数
		copy(this,newClient);
		return newClient;
	}
	
	public Collection<SmbFile1> wrapAll(Collection<SmbFile> smbFiles) {
		//创建文件类型的文件集合
		Collection<SmbFile1> fileList = new ArrayList<SmbFile1>();
		for (SmbFile smbFile : smbFiles) {
			SmbFile1 newClient = new SmbFile1(smbFile.getURL());
			//拷贝初始参数
			copy(this,newClient);
			fileList.add(newClient);
		}
		return fileList;
	}
	
	public SmbFile1 get(String name) throws MalformedURLException, UnknownHostException {
		return new SmbFile1(this, name);
	}
	
	public void copy(SmbFile1 dest,SmbFile1Config config){
		//启用或禁用用户交互（例如弹出一个验证对话框）的上下文中对此 URL 进行检查
		dest.setAllowUserInteraction(config.isAllowUserInteraction());
		//设置一个指定的超时值（以毫秒为单位），该值将在打开到此 URLConnection 引用的资源的通信链接时使用
		dest.setConnectTimeout(config.getConnectTimeout());
		//数据读取超时时间，以毫秒为单位
		dest.setReadTimeout(config.getReadTimeout());
		//启用或禁用在条件允许情况下允许协议使用缓存
		dest.setDefaultUseCaches(config.isUsecaches());
		dest.setUseCaches(config.isUsecaches());
		
		dest.setAutoFlush(config.isAutoFlush());
		dest.setAutoFlushBlockSize(config.getAutoFlushBlockSize());
		dest.setBufferSize(config.getBufferSize());
		dest.setChannelReadBufferSize(config.getChannelReadBufferSize());
		dest.setChannelWriteBufferSize(config.getChannelWriteBufferSize());
		dest.setLogDebug(config.isLogDebug());
		
		try {
			//进行存储时/检索操作时数据处理进度监听对象
			if(StringUtils.isNotEmpty(config.getCopyStreamProcessListenerName())){
				@SuppressWarnings("unchecked")
				Class<CopyStreamProcessListener> listenerClazz = (Class<CopyStreamProcessListener>) Class.forName(config.getCopyStreamProcessListenerName());
				dest.setCopyStreamProcessListener((CopyStreamProcessListener) ConstructorUtils.invokeConstructor(listenerClazz));
			}else{
				dest.setCopyStreamProcessListener(config.getCopyStreamProcessListener());
			}
		} catch (Exception e) {
			LOG.error(ExceptionUtils.getStackTrace(e));
		}
	}

	public void copy(SmbFile1 src,SmbFile1 dest){
		// 启用或禁用用户交互（例如弹出一个验证对话框）的上下文中对此 URL 进行检查
		dest.setAllowUserInteraction(src.getAllowUserInteraction());
		// 设置一个指定的超时值（以毫秒为单位），该值将在打开到此 URLConnection 引用的资源的通信链接时使用
		dest.setConnectTimeout(src.getConnectTimeout());
		// 数据读取超时时间，以毫秒为单位
		dest.setReadTimeout(src.getReadTimeout());
		// 启用或禁用在条件允许情况下允许协议使用缓存
		dest.setDefaultUseCaches(src.getUseCaches());
		dest.setUseCaches(src.getUseCaches());
		dest.setAutoFlush(src.isAutoFlush());
		dest.setAutoFlushBlockSize(src.getAutoFlushBlockSize());
		dest.setBufferSize(src.getBufferSize());
		dest.setChannelReadBufferSize(src.getChannelReadBufferSize());
		dest.setChannelWriteBufferSize(src.getChannelWriteBufferSize());
		dest.setCopyStreamProcessListener(src.getCopyStreamProcessListener());
		dest.setLogDebug(src.isLogDebug());
	}

	public boolean isAutoFlush() {
		return autoFlush;
	}

	public void setAutoFlush(boolean autoFlush) {
		this.autoFlush = autoFlush;
	}

	public int getAutoFlushBlockSize() {
		return autoFlushBlockSize;
	}

	public void setAutoFlushBlockSize(int autoFlushBlockSize) {
		this.autoFlushBlockSize = autoFlushBlockSize;
	}

	public int getBufferSize() {
		return bufferSize;
	}

	public void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
	}

	public int getChannelReadBufferSize() {
		return channelReadBufferSize;
	}

	public void setChannelReadBufferSize(int channelReadBufferSize) {
		this.channelReadBufferSize = channelReadBufferSize;
	}

	public int getChannelWriteBufferSize() {
		return channelWriteBufferSize;
	}

	public void setChannelWriteBufferSize(int channelWriteBufferSize) {
		this.channelWriteBufferSize = channelWriteBufferSize;
	}

	public CopyStreamProcessListener getCopyStreamProcessListener() {
		return copyStreamProcessListener;
	}

	public void setCopyStreamProcessListener(
			CopyStreamProcessListener copyStreamProcessListener) {
		this.copyStreamProcessListener = copyStreamProcessListener;
	}

	public boolean isLogDebug() {
		return logDebug;
	}

	public void setLogDebug(boolean logDebug) {
		this.logDebug = logDebug;
	}

	

	public long getRestartOffset() {
		return restartOffset;
	}

	public void setRestartOffset(long restartOffset) {
		this.restartOffset = Math.max(0, restartOffset);
	}

	/**
	 * This URLConnection method just returns a new <tt>SmbFileInputStream</tt> created with this file.
	 * @throws IOException thrown by <tt>SmbFileInputStream</tt> constructor
	 */
	@SuppressWarnings("resource")
	public InputStream getInputStream() throws IOException {
		SmbFileInputStream input = new SmbFileInputStream(this);
		long at = getRestartOffset();
		while (at > 0) {
			long amt = input.skip(at);
			if (amt == -1) {
				throw new EOFException(" restartOffset [" + restartOffset + "] larger than the length [" + this.getContentLength() + "] of shared File : unexpected EOF");  
			}
			at -= amt;
		}
		return input;
	}

}
