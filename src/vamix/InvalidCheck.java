package vamix;
import java.io.IOException;

import javax.swing.JOptionPane;

/**
 * This class has a method body that checks the file
 * entered is a media file. It calls to bash and checks the validity 
 * based on the type.
 * @author anmol
 *
 */
public class InvalidCheck {

	/**
	 * This method inputs a string that has the name and path of the 
	 * new file and it passes it to bash and checks if it is valid media file.
	 * It returns a boolean that is true if file entered is a valid media file and false
	 * if otherwise.
	 * @param newFile
	 * @return
	 */
	public boolean invalidCheck(String newFile){
		String command = "file " + "-ib " + "\"" + newFile + "\"" + " | grep \"video\\|audio\"";
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

