package videoFeatures;

import java.io.File;
import java.util.concurrent.ExecutionException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

/**
 * This class extends swingworker and does the process of adding video filter
 * to a video file. It works in the background and is called by the VideoFilter.java 
 * It adds the video filter by taking in the parameters from the GUI made in VideoFilter.java
 * 
 * Output: mp4 with video filter
 * @author anmol
 *
 */
public class VideoFilterWorker extends SwingWorker<Integer,Void>{
	
	private JTextField field;
	private JButton start;
	private JProgressBar progress;
	private JComboBox selectFilter;
	private File selectedFile;
	private File outputDirectory;
	
	public VideoFilterWorker(JTextField field, JButton start, JProgressBar progress,JComboBox selectFilter, File selectedFile, File outputDirectory ){
		this.field = field;
		this.start = start;
		this.progress = progress;
		this.selectedFile = selectedFile;
		this.selectFilter = selectFilter;
		this.outputDirectory = outputDirectory;
	}

	@Override
	/**
	 * Has the implmentation of all the filters given.
	 */
	protected Integer doInBackground() throws Exception {

		// based on what item is selected, do the respective adding of filter
		String name = field.getText();

		if(!name.contains(".mp4")){
			name = name + ".mp4";
		}

		int exitValue = 1;
		
		// it is the flip 90 degress so this does the avconv for that.
		if(selectFilter.getSelectedIndex() == 0){
			
			String cmd = "/usr/bin/avconv -i " + "" +selectedFile.getAbsolutePath().replaceAll(" ", "\\\\ ") + " -vf " + "transpose=1 " + "-strict experimental " + outputDirectory.getAbsolutePath().replaceAll(" ", "\\\\ ") + File.separator + name;

			ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
			Process process = builder.start();
			process.waitFor();

			exitValue = process.exitValue();
		}
		
		// This rotates the video 270 degrees.
		if(selectFilter.getSelectedIndex() == 1){
			String cmd = "/usr/bin/avconv -i " + "" +selectedFile.getAbsolutePath().replaceAll(" ", "\\\\ ") + " -vf " + "transpose=0 " + "-strict experimental " + outputDirectory.getAbsolutePath().replaceAll(" ", "\\\\ ") + File.separator + name;

			ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
			Process process = builder.start();
			process.waitFor();

			exitValue = process.exitValue();
		}
		// Negates the video file and then does a vertical flip on it.
		if(selectFilter.getSelectedIndex() == 2){
			String cmd = "/usr/bin/avconv -i " + "" +selectedFile.getAbsolutePath().replaceAll(" ", "\\\\ ") + " -vf " + "negate,vflip " + "-strict experimental " + outputDirectory.getAbsolutePath().replaceAll(" ", "\\\\ ") + File.separator + name;

			ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
			Process process = builder.start();
			process.waitFor();

			exitValue = process.exitValue();
		}

		// Negates the video file
		if(selectFilter.getSelectedIndex() == 3){
			String cmd = "/usr/bin/avconv -i " + "" +selectedFile.getAbsolutePath().replaceAll(" ", "\\\\ ") + " -vf " + "negate " + "-strict experimental " + outputDirectory.getAbsolutePath().replaceAll(" ", "\\\\ ") + File.separator + name;

			ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
			Process process = builder.start();
			process.waitFor();

			exitValue = process.exitValue();
		}
		
		// Does a vertical flip on the video
		if(selectFilter.getSelectedIndex() == 4){
			String cmd = "/usr/bin/avconv -i " + "" +selectedFile.getAbsolutePath().replaceAll(" ", "\\\\ ") + " -vf " + "vflip " + "-strict experimental " + outputDirectory.getAbsolutePath().replaceAll(" ", "\\\\ ") + File.separator + name;

			ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
			Process process = builder.start();
			process.waitFor();

			exitValue = process.exitValue();
		}
		
		// Blurs the video by a factor of 2
		if(selectFilter.getSelectedIndex() == 5){
			String cmd = "/usr/bin/avconv -i " + "" +selectedFile.getAbsolutePath().replaceAll(" ", "\\\\ ") + " -vf " + "boxblur=2:1 " + "-strict experimental " + outputDirectory.getAbsolutePath().replaceAll(" ", "\\\\ ") + File.separator + name;

			ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
			Process process = builder.start();
			process.waitFor();

			exitValue = process.exitValue();
		}
		
		// blurs the video by a factor of 5
		if(selectFilter.getSelectedIndex() == 6){
			String cmd = "/usr/bin/avconv -i " + "" +selectedFile.getAbsolutePath().replaceAll(" ", "\\\\ ") + " -vf " + "boxblur=5:1 " + "-strict experimental " + outputDirectory.getAbsolutePath().replaceAll(" ", "\\\\ ") + File.separator + name;

			ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
			Process process = builder.start();
			process.waitFor();

			exitValue = process.exitValue();
		}
		
		// blurs the video by a factor of 10
		if(selectFilter.getSelectedIndex() == 7){
			String cmd = "/usr/bin/avconv -i " + "" +selectedFile.getAbsolutePath().replaceAll(" ", "\\\\ ") + " -vf " + "boxblur=10:1 " + "-strict experimental " + outputDirectory.getAbsolutePath().replaceAll(" ", "\\\\ ") + File.separator + name;

			ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
			Process process = builder.start();
			process.waitFor();

			exitValue = process.exitValue();
		}

		// sets the video file to same dimensions as 240p.
		if(selectFilter.getSelectedIndex() == 8){
			String cmd = "/usr/bin/avconv -i " + "" +selectedFile.getAbsolutePath().replaceAll(" ", "\\\\ ") + " -vf " + "scale=320:240 " + "-strict experimental " + outputDirectory.getAbsolutePath().replaceAll(" ", "\\\\ ") + File.separator + name;

			ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
			Process process = builder.start();
			process.waitFor();

			exitValue = process.exitValue();
		}
		
		// sets the video file to same dimensions as 360p.
		if(selectFilter.getSelectedIndex() == 9){
			String cmd = "/usr/bin/avconv -i " + "" +selectedFile.getAbsolutePath().replaceAll(" ", "\\\\ ") + " -vf " + "scale=480:360 " + "-strict experimental " + outputDirectory.getAbsolutePath().replaceAll(" ", "\\\\ ") + File.separator + name;

			ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
			Process process = builder.start();
			process.waitFor();

			exitValue = process.exitValue();
		}
		
		// sets the video file to same dimensions as 480p.
		if(selectFilter.getSelectedIndex() == 10){
			String cmd = "/usr/bin/avconv -i " + "" +selectedFile.getAbsolutePath().replaceAll(" ", "\\\\ ") + " -vf " + "scale=640:480 " + "-strict experimental " + outputDirectory.getAbsolutePath().replaceAll(" ", "\\\\ ") + File.separator + name;

			ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
			Process process = builder.start();
			process.waitFor();

			exitValue = process.exitValue();
		}

		// sets the video file to same dimensions as 720p.
		if(selectFilter.getSelectedIndex() == 11){
			String cmd = "/usr/bin/avconv -i " + "" +selectedFile.getAbsolutePath().replaceAll(" ", "\\\\ ") + " -vf " + "scale=1280:720 " + "-strict experimental " + outputDirectory.getAbsolutePath().replaceAll(" ", "\\\\ ") + File.separator + name;

			ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
			Process process = builder.start();
			process.waitFor();

			exitValue = process.exitValue();
		}
		
		// sets the video file to same dimensions as 1080p.
		if(selectFilter.getSelectedIndex() == 12){
			String cmd = "/usr/bin/avconv -i " + "" +selectedFile.getAbsolutePath().replaceAll(" ", "\\\\ ") + " -vf " + "scale=1920:1080 " + "-strict experimental " + outputDirectory.getAbsolutePath().replaceAll(" ", "\\\\ ") + File.separator + name;

			ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
			Process process = builder.start();
			process.waitFor();

			exitValue = process.exitValue();
		}


		return exitValue;
	}

	@Override
	/**
	 * enables the user to start the process again and givse the user
	 * the result of the last process.
	 */
	protected void done() {
		start.setEnabled(true);
		progress.setIndeterminate(false);
		try {
			int i = get();
			// give the user the results of the process
			if(i == 0){
				JOptionPane.showMessageDialog(null, "The filter was added successfully!");
			}else{
				JOptionPane.showMessageDialog(null, "The adding of filter failed!");
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}
}
