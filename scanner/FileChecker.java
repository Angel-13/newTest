package scanner;

import java.io.File;

public class FileChecker {

	public boolean fileExists(String path){
		return new File(path).isFile();
	}
		
}
