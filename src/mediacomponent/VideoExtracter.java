package mediacomponent;

import java.io.File;
import java.util.concurrent.ExecutionException;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

/**
 * This class extends swingoworker an does the background tasks that allow the user to get the part of a video file by 
 * just DOUBLE CLICKING ON THE SCREEN the media is being played on.
 * 
 * @author anmol
 *
 */
public class VideoExtracter extends SwingWorker<Integer,Void>{

	String startTime;
	String lengthTime;
	String name;

	public VideoExtracter(String startTime, String lengthTime) {
		this.startTime = startTime;
		this.lengthTime = lengthTime;
	}

	@Override
	protected Integer doInBackground() throws Exception {

		// time gives a unique name...i mean whats the probabilty that the time is exact ...down to the milliseconds! lol
		name =  "" + VideoPlayer.mediaPlayer.getTime() + ".mp4";
		int exitValue = 1;

		String cmd = "/usr/bin/avconv -i " + VideoPlayer.filePath + " -ss " + startTime + " -t " + lengthTime + " -c:a copy -c:v copy "  + System.getProperty("user.home") + File.separator + name; 
		ProcessBuilder builder = new ProcessBuilder("/bin/bash","-c",cmd);

		Process process = builder.start();
		process.waitFor();
		exitValue = process.exitValue();
		return exitValue;
	}

	@Override
	protected void done() {
		try {
			int i = get();
			// show the success of the process
			if(i == 0){
				JOptionPane.showMessageDialog(null, "Video was extracted successfully to your home folder."
						+ " Name of the video is " + name );
				// go to the current directory folder and find the file and then move it to the home folder

			}else{
				JOptionPane.showMessageDialog(null, "Video Extraction failed!");
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}

	}

}