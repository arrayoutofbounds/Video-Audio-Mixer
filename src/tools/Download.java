package tools;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

/**
 * This class has the download function. It is a seperate frame.
 * It creates the GUI for the download frame.
 * 
 * Output : format of what the URL gives.
 * @author anmol
 *
 */
@SuppressWarnings("serial")
public class Download extends JPanel implements ActionListener {
	
	
	// initialise the variables
	private JLabel forUrl;
	private JTextField Url;
	private JButton choosePlaceToSaveToButton;
	private JLabel showChosenDirectoryLabel;
	private JButton downloadButton;
	private JButton cancelButton;
	private JProgressBar progressBar;
	private File chosenDirectory;
	private ButtonHandler worker;
	private File toOverride;
	private String basename;
	private boolean override;
	private boolean resume;


	public Download() {

		setLayout(new GridBagLayout());

		// Create all the components and create a new gridLayout for each component,
		// adding the label that shows user where to enter the URL. 
		forUrl = new JLabel("Please enter URL:");
		GridBagConstraints gb = new GridBagConstraints();
		gb.gridx = 0;
		gb.gridy = 0;
		gb.insets = new Insets(20,10,0,10);
		gb.gridheight = 1;
		gb.gridwidth = 1;
		gb.weightx = 0;
		gb.weighty = 1;
		this.add(forUrl,gb);

		// Adding the textfield that shows the user the textfield that takes in the URL
		Url = new JTextField(5000);
		GridBagConstraints gb1 = new GridBagConstraints();
		gb1.gridx = 1;
		gb1.gridy = 0;
		gb1.fill = GridBagConstraints.HORIZONTAL;
		gb1.insets = new Insets(15,10,0,10);
		gb1.gridheight = 1;
		gb1.gridwidth = 4;
		gb1.weighty = 1;
		this.add(Url,gb1);

		// Adding the JButton that will allow the user to select a destination
		choosePlaceToSaveToButton = new JButton("Choose Destination of downloaded file");
		GridBagConstraints gb2 = new GridBagConstraints();
		gb2.gridx = 1;
		gb2.gridy = 1;
		gb2.anchor = GridBagConstraints.CENTER;
		gb2.insets = new Insets(10,10,10,10);
		gb2.gridheight = 1;
		gb2.gridwidth = 4;
		gb2.weighty =1;
		this.add(choosePlaceToSaveToButton, gb2);
		
		// adding the label that shows the directory
		showChosenDirectoryLabel = new JLabel("Saved To: ");
		GridBagConstraints gb3 = new GridBagConstraints();
		gb3.gridx = 0;
		gb3.gridy = 2;
		gb3.insets = new Insets(0,0,0,80);
		gb3.fill = GridBagConstraints.HORIZONTAL;
		gb3.gridwidth = 5;
		gb3.gridheight = 1;
		gb3.weightx = 0;
		gb3.weighty = 1;
		this.add(showChosenDirectoryLabel,gb3);

		// Get the download button and cancel button 
		downloadButton = new JButton("Download");
		downloadButton.setPreferredSize(new Dimension(100,20));
		GridBagConstraints gb4  = new GridBagConstraints();
		gb4.gridx = 1;
		gb4.gridy = 3;
		gb4.anchor  = GridBagConstraints.EAST;
		gb4.gridheight = 1;
		gb4.gridwidth = 2;
		gb4.weightx = 0.4;
		gb4.weighty = 1;
		this.add(downloadButton, gb4);
		
		// adding the cancel button
		cancelButton = new JButton("Cancel");
		cancelButton.setEnabled(false);
		cancelButton.setPreferredSize(new Dimension(100,20));
		GridBagConstraints gb5  = new GridBagConstraints();
		gb5.gridx =3;
		gb5.gridy = 3;
		gb5.gridheight = 1;
		gb5.insets = new Insets(0,0,0,20);
		gb5.anchor  = GridBagConstraints.WEST;
		gb5.gridwidth = 2;
		gb5.weightx = 0.65;
		gb5.weighty = 1;
		this.add(cancelButton,gb5);

		// Make the progress bar
		progressBar = new JProgressBar();
		progressBar.setStringPainted(true);
		GridBagConstraints gb6  = new GridBagConstraints();
		gb6.gridx = 1;
		gb6.gridy = 4;
		gb6.gridheight = 1;
		gb6.fill = GridBagConstraints.HORIZONTAL;
		gb6.insets = new Insets(0,100,0,200);
		gb6.gridwidth = 4;
		gb6.weightx = 1;
		gb6.weighty = 1;
		this.add(progressBar,gb6);

		// Add action listeners to all the buttons
		choosePlaceToSaveToButton.addActionListener(this);
		downloadButton.addActionListener(this);
		cancelButton.addActionListener(this);

	}
	
	/**
	 * This class does the download process calls in the background
	 * and ensures that the user knows what the result of the process is
	 * and also makes sure that the GUI is not disturbed.
	 * @author anmol
	 *
	 */
	private class ButtonHandler extends SwingWorker<Integer,Integer> {

		/**
		 * This method return the exit value in the end. It updates the
		 * progess bar by returning numbers in the middle by calling publish()
		 */
		@Override
		protected Integer doInBackground() throws Exception {
		// Ask if the file is open source or not

			// If the user says the file IS open source then allow download 
			int exitValue = 1;
			InputStream stdout;
			BufferedReader stdoutBuffered;
			
			// do the wget depending on whether the user wants to override or resume
			try {
				ProcessBuilder builder = null;

				if (override) {
					toOverride.delete();
					builder = new ProcessBuilder("/usr/bin/wget","-P" + chosenDirectory.getAbsolutePath().replaceAll(" ", "\\\\ "),Url.getText());
				} else {
					builder = new ProcessBuilder("/usr/bin/wget","-c","-P" + chosenDirectory.getAbsolutePath().replaceAll(" ", "\\\\ "),Url.getText());
				}
				
				// print the error stream because it has the numbers needed for the percentage for the progress bar
				builder.redirectErrorStream(true);
				Process process = builder.start();
				stdout = process.getInputStream();
				stdoutBuffered = new BufferedReader(new InputStreamReader(stdout));
				
				// read the error lines so that the progress bar can be updated
				String line2= null;
				while ((line2 = stdoutBuffered.readLine()) != null) {

					if (isCancelled()) {
						process.destroy();
					} else {
						Matcher m =Pattern.compile("(\\d+)%").matcher(line2);
						if (m.find()) {
							// send the numbers to the process method
							publish(Integer.parseInt(m.group(1)));
						}
					}
				}
				process.waitFor();
				// get the exit value
				exitValue = process.exitValue();
				//Close the buffered reader
				stdoutBuffered.close();
				
			} catch (IOException io) {
				// DO NOT THROW EXCEPTION OR STACK TRACE HERE!
				// Check for IOexception
				//JOptionPane.showMessageDialog(Download.this,"io exception");
				// the bufferedreader throws an exception i think
			} catch(Exception e4) {
				//any other exception...just show me the stack trace
				e4.printStackTrace();
			}
			
			
			// return the exit value
			return exitValue;

		}

		@Override
		/**
		 * This method runs on the ED thread and thus it updates
		 * the progress bar
		 */
		protected void process(List<Integer> chunks) {
			for (int i : chunks) {
				progressBar.setValue(i);
			}
		}

		@Override
		/**
		 * This method gets the result of the download function and tells the
		 * user in a nice GUI way what happened.
		 */
		protected void done() {
			downloadButton.setEnabled(true);
			try {
				int value = get();
				
				// tell the user what the result is and its different meanings.
				if(value == 0){
					JOptionPane.showMessageDialog(Download.this,"Download Complete!");
					downloadButton.setEnabled(true);
					cancelButton.setEnabled(false);
				} else if(value == 1) {
					downloadButton.setEnabled(true);
					cancelButton.setEnabled(false);
					JOptionPane.showMessageDialog(Download.this, "Download failed due to Generic error code!");
				} else if(value == 2) {
					downloadButton.setEnabled(true);
					cancelButton.setEnabled(false);
					JOptionPane.showMessageDialog(Download.this, "Donwload failed due to parse error!");
				} else if(value == 3) {
					downloadButton.setEnabled(true);
					cancelButton.setEnabled(false);
					JOptionPane.showMessageDialog(Download.this, "Download failed due to File IO error!");
				} else if(value ==4) {
					downloadButton.setEnabled(true);
					cancelButton.setEnabled(false);
					JOptionPane.showMessageDialog(Download.this, "Download failed due to Network failure!");
				} else if(value == 5) {
					downloadButton.setEnabled(true);
					cancelButton.setEnabled(false);
					JOptionPane.showMessageDialog(Download.this, "Download failed due to SSL verification failure");
				} else if(value ==6) {
					downloadButton.setEnabled(true);
					cancelButton.setEnabled(false);
					JOptionPane.showMessageDialog(Download.this, "Download failed due to Username/password authentication failure!");
				} else if(value == 7) {
					downloadButton.setEnabled(true);
					cancelButton.setEnabled(false);
					JOptionPane.showMessageDialog(Download.this, "Download failed due to Protocol errors!");
				} else {
					downloadButton.setEnabled(true);
					cancelButton.setEnabled(false);
					JOptionPane.showMessageDialog(Download.this, "Download failed due to Server issuing an error response!");
				}
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			} catch (CancellationException c) {
				JOptionPane.showMessageDialog(Download.this,"The download was cancelled!");
			}
		}
	}

	@Override
	/**
	 * 
	 * This method is for when either the choose place to save, download or cancel button is pressed.
	 * 
	 */
	public void actionPerformed(ActionEvent e) {

		// If the button to select the directory to save to is chosen
		if (e.getSource() == choosePlaceToSaveToButton) {
			choosePlaceToSavePressed();
		}

		// if the user presses the download button then the program has to check if that chosen File exists and 
		// if its does then it needs to ask whether to override it or resume download. Then depending on that input from 
		// the user it downloads its by resuming or overriding
		// as the user if it is a open source file...if the user says "yes" then continue with the download, else stop it

		// use the process calls to bash to get the basename and see if a file exists with that name
		// if it does then dialog box the override or resume.
		// if it does not then just carry on with the download as usual

		if (e.getSource() == downloadButton) {
			downloadPressed();
		}
		
		if (e.getSource() == cancelButton) {
			downloadButton.setEnabled(true);
			cancelButton.setEnabled(false);
			worker.cancel(true);
		}
	}
	
	/**
	 * This method is for when the download button is pressed. It gets the name
	 * It gets the base name and this allows it to check if the file aready exits somewhere.
	 */
	private void downloadPressed() {

		String text = Url.getText();

		// We have the url.. now we need the basename
		String cmd = "basename " + text;
		try {

			ProcessBuilder builder = new ProcessBuilder("/bin/bash","-c", cmd);
			Process process = builder.start();

			// Wait for the process to finish otherwise you will get improper exit values.
			process.waitFor();

			InputStream stdout = process.getInputStream();
			BufferedReader stdoutBuffered = new BufferedReader(new InputStreamReader(stdout));

			int exitValue = process.exitValue();

			if(exitValue == 0){
				basename = stdoutBuffered.readLine();
			}

		} catch(Exception exception) {
			JOptionPane.showMessageDialog(Download.this,"Error 404. URL not found!");
		}

		// ensure that no fields are empty before the processs is started.
		if ((text.equals("") || text.equals(null)) && (chosenDirectory == null)) {
			JOptionPane.showMessageDialog(Download.this,"Please give URL of mp3 and choose a destination to save the file");
		} else if(text.equals("") || text.equals(null)) {
			JOptionPane.showMessageDialog(Download.this,"Please fill out the URL of the file for download.");
		} else if(chosenDirectory == null) {
			JOptionPane.showMessageDialog(Download.this,"Please choose a destination to save the file.");
		} else {

			int answer = JOptionPane.showConfirmDialog(Download.this,"Is the file open source?");

			if(answer == JOptionPane.YES_OPTION){

				// If everything is fine then proceed to download the file. 
				File propFile  = new File(chosenDirectory,basename);
				toOverride = propFile;
				
				if (propFile.exists()) {

					String[] options = {"Override","Resume"};
					int code = JOptionPane.showOptionDialog(Download.this, 
							"This file already exists! Would you like to override it or resume downloading it?", 
							"Option Dialog Box", 0, JOptionPane.QUESTION_MESSAGE, 
							null, options, "Override");
					if (code == 0) {
						// Allow override
						override = true;
						resume =false;
					} else if(code == 1) {
						//Allow resume
						resume = true;
						override = false;
					}
				}

				worker = new ButtonHandler();
				cancelButton.setEnabled(true);
				downloadButton.setEnabled(false);
				worker.execute();

			} else if(answer == JOptionPane.NO_OPTION) {
				JOptionPane.showMessageDialog(Download.this, "Sorry, you cannot download a file that is not open source!");
			} else {
				JOptionPane.showMessageDialog(Download.this, "P.s you have to choose a open source file to continue download");
			}
		}
	}

	/**
	 * This method allows the user to choose a place to save the downloaded file to.
	 */
	private void choosePlaceToSavePressed() {

		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(new java.io.File("."));
		chooser.setDialogTitle("Choose Directory");
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int response = chooser.showOpenDialog(Download.this);

		if (response == JFileChooser.APPROVE_OPTION) {

			// Chose the directory to save to
			chosenDirectory = chooser.getSelectedFile().getAbsoluteFile();

			// Show the directory chosen
			showChosenDirectoryLabel.setText("Saved To: " + chosenDirectory.getAbsolutePath());
		}
	}
	
	
}
