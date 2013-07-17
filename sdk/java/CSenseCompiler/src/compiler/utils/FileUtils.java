package compiler.utils;

import java.io.File;

public class FileUtils {
    public static boolean delete(File dir) {
	if(! dir.exists() || !dir.isDirectory()) 
	    return false;

	String[] files = dir.list();
	for(int i = 0, len = files.length; i < len; i++)    {
	    File f = new File(dir, files[i]);
	    if(f.isDirectory()) {
		delete(f);
	    }else   {
		f.delete();
	    }
	}
	return dir.delete();
    }
}
