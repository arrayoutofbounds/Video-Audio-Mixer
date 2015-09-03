package mediacomponent;

import java.io.File;

/**
 * This class allows the internal of the program to add played files to the history.
 * @author anmol
 *
 */
public class AddToFile {
	
	public AddToFile(){
		
	}
	
	
	public void add(){
		LogFile.writeToLog(VideoPlayer.filePath.substring(VideoPlayer.filePath.lastIndexOf(File.separator)+1));
	}

}
