package tools;

import javax.swing.JFrame;

/**
 * This is the class that makes the download frame. It calls
 * on the class that consists of the download logic.
 * @author anmol
 *
 */
public class DownloadFrame extends JFrame {
	
	Download download;
	
	public DownloadFrame(){
		download = new Download();
		add(download);
		
		
	}

}
