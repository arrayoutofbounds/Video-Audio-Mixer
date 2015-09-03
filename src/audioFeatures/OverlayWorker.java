package audioFeatures;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

import textHandling.MediaLengthWorker;

/**
 * This class has the swingworker that will allow the user to  overlay one audio on another.
 * It takes in a video file and then allows the user to select a audio file to overlay on that file.
 * It is called by the ReplaceAudio class.
 * 
 * OUTPUT: mp4
 * @author anmol
 *
 */
public class OverlayWorker extends SwingWorker<Integer, Integer>{
	//declare the fieds
	private JButton replace;
	private File selectedFile = null;
	private File selectedFile2 = null;
	private File outputDirectory = null;
	private JButton overlay;
	private JProgressBar progressBar;
	private JTextField outputName;
	
	// initalise the fields
	public OverlayWorker(JButton replace, File selectedFile, File selectedFile2, File outputDirectory,JButton overlay, JProgressBar progressBar,JTextField outputName){
		
		this.replace = replace;
		this.selectedFile = selectedFile;
		this.selectedFile2  = selectedFile2;
		this.outputDirectory = outputDirectory;
		this.overlay = overlay;
		this.progressBar = progressBar;
		this.outputName = outputName;
	
	}
	
	
	@Override
	protected Integer doInBackground() throws Exception {

		String name = outputName.getText();
		
		if(!name.contains(".mp4")){
			name = name + ".mp4";
		}
		
		// Get length of the video and audio and find which is the largest
		
					MediaLengthWorker LengthWorker = new MediaLengthWorker(selectedFile.getAbsolutePath());
					LengthWorker.execute();
					int length1 = LengthWorker.get();
					
					LengthWorker = new MediaLengthWorker(selectedFile2.getAbsolutePath());
					LengthWorker.execute();
					int length2 = LengthWorker.get();
					boolean haveLengths = true;
					if (length1 == -1 || length2 == -1) {
						// Couldn't get length for at least one of the files
						// We will use an indeterminate progress bar
						haveLengths = false;
						progressBar.setStringPainted(false);
						progressBar.setIndeterminate(true);
					}
					int maxLength = 0;
					if (length1 >= length2) {
						maxLength = length1;
					} else if (length2 >= length1) {
						maxLength = length2;
					}
		
		
		int exitValue = 1;
		
		// do the command for overlaying the audio
		
		String cmd = "/usr/bin/avconv -i " + selectedFile.getAbsolutePath().replaceAll(" ", "\\\\ ") + " -i " + selectedFile2.getAbsolutePath().replaceAll(" ", "\\\\ ") + " -filter_complex amix=inputs=2 -strict experimental " + outputDirectory.getAbsolutePath().replaceAll(" ", "\\\\ ") + File.separator + name;
		
		// start the process
		ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd).redirectErrorStream(true);
		Process process = builder.start();
		
		InputStream out = process.getInputStream();
		BufferedReader stdout = new BufferedReader(new InputStreamReader(out));
		stdout = new BufferedReader(new InputStreamReader(out));
		
		String line = null;
		Pattern p = Pattern.compile("(.*time=)(\\d+\\.\\d+)(.*)");

		while ((line = stdout.readLine()) != null) {
			Matcher m = p.matcher(line);
			if (m.matches()) {
				if (haveLengths) {
					publish((int)(Float.parseFloat(m.group(2)) / maxLength * 100));
				}
			}
		}
		
		process.waitFor();
		
		exitValue = process.exitValue();

		return exitValue;
	}

	@Override
	protected void done() {
		replace.setEnabled(true);
		overlay.setEnabled(true);
		try {
			int i = get();
			
			// show the user the result
			
			if(i == 0){
				progressBar.setValue(100);
				JOptionPane.showMessageDialog(null,"The overlaying was successful!");
			}else{
				JOptionPane.showMessageDialog(null,"Sorry! The overlaying was unsuccessful!");
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		
	}
	
	@Override
	/**
	 * Update the progress bar as needed
	 */
	protected void process(List<Integer> list) {
		for (Integer i : list) {
			progressBar.setValue(i);
		}
	}


}
