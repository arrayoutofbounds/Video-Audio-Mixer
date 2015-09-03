package textHandling;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.SwingWorker;

/**
 * This Class gets the length of the video that is passed in the input.
 * It calls the process builder and gets the duration of the media file and 
 * then returns it.
 * @author anmol
 *
 */
public class MediaLengthWorker extends SwingWorker<Integer, Void> {

	private String pathToFile;
	
	public MediaLengthWorker(String pathToFile) {
		this.pathToFile = pathToFile;
	}
	
	@Override
	protected Integer doInBackground() throws Exception {

		// Process to get length of input file
		String cmd = "/usr/bin/avconv -i " + "\""+ pathToFile + "\""+ " | grep Duration";

		ProcessBuilder timeBuilder = new ProcessBuilder("/bin/bash", "-c", cmd).redirectErrorStream(true);
		Process timeProcess = timeBuilder.start();
		InputStream out = timeProcess.getInputStream();
		BufferedReader stdout = new BufferedReader(new InputStreamReader(out));
		
		Pattern p = Pattern.compile("(.*Duration: )(\\d{2}?):(\\d{2}?):(\\d{2}?)(.*)");
		
		String line = null;
		String hoursString = null;
		String minutesString = null;
		String secondsString = null;

		while ((line = stdout.readLine()) != null ) {
			Matcher m = p.matcher(line);
			if (m.matches()) {
				hoursString = m.group(2);
				minutesString = m.group(3);
				secondsString = m.group(4);
			}
		}
		
		int totalSeconds = 0;
		
		if (hoursString != null) {
			totalSeconds = (Integer.parseInt(hoursString) * 3600) + (Integer.parseInt(minutesString) * 60) + Integer.parseInt(secondsString);
			return totalSeconds;
		} else {
			return -1;
		}
	}

}
