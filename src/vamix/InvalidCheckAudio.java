package vamix;
import java.io.IOException;

import javax.swing.JOptionPane;

/**
 * This file is similar to the InvalidCheck file. But
 * this file specifically checks that the media file is a audio file.
 * This is used when the user enters a audio file.
 * @author anmol
 *
 */
public class InvalidCheckAudio {


	/**
	 * /**
	 * This method inputs a string that has the name and path of the 
	 * new file and it passes it to bash and checks if it is valid audio file.
	 * It returns a boolean that is true if file entered is a valid audio file and false
	 * if otherwise.
	 * 
	 * @param newFile
	 * @return
	 */
	public boolean invalidCheckAudio(String newFile){
		String command = "file " + "-ib " + "\"" + newFile + "\"" + " | grep \"audio\"";
		ProcessBuilder builder = new ProcessBuilder("bash", "-c", command);
		boolean isValidMedia = false;

		try {
			Process process = builder.start();
			process.waitFor();
			if (process.exitValue() == 0) {
				isValidMedia = true;
			}
			return isValidMedia;

		} catch (IOException | InterruptedException e1) {
			// Couldn't determine file type. Warn user
			JOptionPane.showMessageDialog(null, "Unable to determine file type. Cannot load file.", "Error", JOptionPane.ERROR_MESSAGE);
			return false;
		}
	}
}

