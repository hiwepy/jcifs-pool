package jcifs.utils;

import jcifs.io.CopyStreamProcessListener;
import jcifs.smb1.SmbFile1;

public class SMBCopyListenerUtils {

	public static void initCopyListener(SmbFile1 sharedFile,String filename){
		//进度监听
		CopyStreamProcessListener listener = sharedFile.getCopyStreamProcessListener();
		//判断监听存在
		if(listener != null){
	    	listener.setFileName(filename);
	    }
	}
	
}
