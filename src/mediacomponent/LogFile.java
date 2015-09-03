package mediacomponent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

/**
 * This class maintains a single instance of the log file for the VAMIX
 * It provides the ability to write to both the log file and log panel
 * @author Nick Molloy and Anmol Desai
 */
public class LogFile{

	private static File logFileObject = null;
	private static String SysSep = System.getProperty("file.separator");
	private static String fileName = "log";
	private static String filePath = System.getProperty("user.home") + SysSep + ".vamix" + SysSep;
	private static String FullFilePath = filePath + fileName;
	private static String emptyMessage = "No previous history";
	private static JTextArea logArea = VideoPlayer.history;


	/**
	 * @return returns File object that represents the log file on disk
	 * @throws IOException thrown when log file cannot be accessed
	 */
	public static File getLogFile() {

		if (logFileObject == null) { // Logfile hasn't been requested yet, so open it
			new File(filePath).mkdirs();

			logFileObject = new File(FullFilePath);
			// Check if log file exists on disk
			if (!logFileObject.exists()) {
				// Doesn't exist so create it
				try {
					logFileObject.createNewFile();
				} catch (IOException e) {
					JOptionPane.showMessageDialog(null, "Could create new log file: " + FullFilePath , "ERROR", JOptionPane.ERROR_MESSAGE);
				}
				// Nothing in log file so write default message to log panel
				logArea.setText(emptyMessage);
			}
		}

		return logFileObject;
	}


	/**
	 * @param logLine	String to write to both the logfile and the log panel
	 */
	public static void writeToLog(String logLine) {

		// Get time and date
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		Date date = new Date();
		String outputString = dateFormat.format(date) + "  " + logLine + System.getProperty("line.separator");

		// Check if default message is in Panel. If it is, remove it
		if (logArea.getText().equals(emptyMessage)) {
			logArea.setText("");
		}
		// Write given string to log panel
		logArea.append(outputString);

		// Write given string to log file
		try {
			FileOutputStream is = new FileOutputStream(getLogFile(), true);
			OutputStreamWriter osw = new OutputStreamWriter(is);
			Writer w = new BufferedWriter(osw);
			w.write(outputString);
			w.close();
		} catch (IOException e) {
			// Something went wrong, let user know
			JOptionPane.showMessageDialog(null, "Could not write to log file: " + filePath + "\nLog show in Panel will not be saved to disk", "ERROR", JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Deletes the file and sets the text area shown as "history" to become empty.
	 */
	public static void delete(){
		if(logFileObject != null){
			logFileObject.delete();
		}

		getLogFile();
		logArea.setText(emptyMessage);

	}
	
	/**
	 * Hides the history from the "history" text area in the main player. 
	 * Also shows the history back up if history was hidden.
	 */
	public static void hide() {

		if (logFileObject == null) {
			logFileObject = getLogFile();
		}
		
		// load the history
		
		if(logArea.getText().equals("")){
			try {
				File f = new File(FullFilePath);
				BufferedReader br = new BufferedReader(new FileReader(f));
				String line = br.readLine();
				while (line != null) {
					logArea.append(line);
					logArea.append(System.lineSeparator());
					line = br.readLine();
				}
				br.close();
			} catch (IOException e) {
				// Could not read log file, display error message
				JOptionPane.showMessageDialog(null, "Could not open log file: No log available", "ERROR", JOptionPane.ERROR_MESSAGE);
			}
		}else{
			logArea.setText("");
		}
	}



}