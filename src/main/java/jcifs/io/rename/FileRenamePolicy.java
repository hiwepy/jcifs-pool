package jcifs.io.rename;

import java.io.File;

public interface FileRenamePolicy {
	
	public abstract String rename(String filename);
	
	public abstract File rename(File file);
	
}



