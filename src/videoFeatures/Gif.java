package videoFeatures;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.concurrent.ExecutionException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;

import uk.co.caprica.vlcj.filter.swing.SwingFileFilterFactory;
import vamix.InvalidCheck;

/**
 * This class has the GUI and the logic to make a GIF file.
 * It takes in a video file and then makes a gif file.
 * 
 * WARNING : TOO large files can cause the program to crash (i.e do not put 10 minute videos to make a gif)
 *
 * Output: GIF file	
 * @author anmol
 *
 */
@SuppressWarnings("serial")
public class Gif extends JFrame implements ActionListener {

	private JPanel forInputVideoButton;
	private JPanel forInputVideoLabel;
	private JPanel forOutputButton;
	private JPanel forOutputLabel;
	private JPanel forOutputName;
	private JPanel forGifButton;


	private JButton chooseVideo;
	private JLabel showVideo;
	private JButton chooseOutput;
	private JLabel showOutput;
	private JTextField chooseName;
	private JButton makeGif;
	private File selectedFile;
	private File outputDirectory;


	private GifWorker worker;
	private File toOverride;

	private JLabel labelForOutputName;

	private JProgressBar progress;
	private JPanel forBar;

	public Gif(){
		super("Making a GIF image");
		setLayout(new GridLayout(7,1));

		// initalise and declare all the components
		
		forInputVideoButton = new JPanel(new FlowLayout());
		forInputVideoButton.setBorder(new EmptyBorder(10, 10, 10, 10));
		forInputVideoLabel = new JPanel(new BorderLayout());
		forInputVideoLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
		forOutputButton = new JPanel(new FlowLayout());
		forOutputButton.setBorder(new EmptyBorder(10, 10, 10, 10));
		forOutputLabel = new JPanel(new BorderLayout());
		forOutputLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
		forOutputName = new JPanel(new FlowLayout());
		forOutputName.setBorder(new EmptyBorder(10, 10, 10, 10));
		forGifButton = new JPanel(new FlowLayout());
		forGifButton.setBorder(new EmptyBorder(10, 10, 10, 10));
		forBar = new JPanel(new FlowLayout());
		forBar.setBorder(new EmptyBorder(10, 10, 10, 10));


		chooseVideo = new JButton("Choose Video");
		chooseVideo.addActionListener(this);
		showVideo = new JLabel("Video Chosen: ");
		chooseOutput = new JButton("Choose output directory");
		chooseOutput.addActionListener(this);
		showOutput = new JLabel("Output destination: ");
		chooseName = new JTextField(5000);
		chooseName.setText("");
		chooseName.setColumns(40);
		makeGif = new JButton("Make the GIF");
		makeGif.setEnabled(false);
		makeGif.addActionListener(this);
		labelForOutputName = new JLabel("Choose Output name:");
		progress = new JProgressBar();

		forInputVideoButton.add(chooseVideo);
		forInputVideoLabel.add(showVideo,BorderLayout.WEST);
		forOutputButton.add(chooseOutput);
		forOutputLabel.add(showOutput,BorderLayout.WEST);
		forOutputName.add(labelForOutputName);
		forOutputName.add(chooseName);
		forGifButton.add(makeGif);
		forBar.add(progress);

		add(forInputVideoButton);
		add(forInputVideoLabel);
		add(forOutputButton);
		add(forOutputLabel);
		add(forOutputName);
		add(forGifButton);
		add(forBar);
	}


	@Override
	/**
	 * methods for all the button presses
	 */
	public void actionPerformed(ActionEvent e) {

		if(e.getSource() == chooseVideo){
			chooseVideoPressed();
		}

		if(e.getSource() == chooseOutput){
			chooseOutputPressed();
		}

		if(e.getSource() == makeGif){
			makeGifPressed();
		}
	}
	
	/**
	 * if the make gif button is pressed this method checks that all the
	 * fields are correct and allows the user to override or rename their file if there
	 * is a clash. A warning is also given to the user for large files.
	 * 
	 * Output : gif file
	 */
	private void makeGifPressed() {
		// check that that everything has been filled
		boolean carryOn = true;

		if((chooseName.getText().equals(""))||(selectedFile == null)||(outputDirectory ==null)){
			JOptionPane.showMessageDialog(Gif.this, "Sorry you must fill all fields before carrying on!");
			carryOn = false;
		}

		if(carryOn){
			JOptionPane.showMessageDialog(Gif.this,"WARNING! If the video is too small or big, the making of the gif fails!");
			// call the swing worker and make the gif
			boolean override = false;

			File propFile = new File(outputDirectory,chooseName.getText() + ".gif");
			if(propFile.exists()){
				toOverride = propFile;
				// ask the user if they want to overrride or not. If not then they must change the name of their file
				String[] options = {"Yes,Override!","No! Do not override!"};
				int code = JOptionPane.showOptionDialog(Gif.this, 
						"This file already exists! Would you like to override it?", 
						"Option Dialog Box", 0, JOptionPane.QUESTION_MESSAGE, 
						null, options, "Yes,Override!");
				if (code == 0) {
					// Allow override
					override = true;
				} else if(code == 1) {
					override = false;
				}

				if(override){
					toOverride.delete();
					worker = new GifWorker();
					worker.execute();
					makeGif.setEnabled(false);
					progress.setIndeterminate(true);
				}else{
					JOptionPane.showMessageDialog(Gif.this, "Please choose another name to carry on making a GIF!");
				}
			}else{

				worker = new GifWorker();
				makeGif.setEnabled(false);
				worker.execute();
				progress.setIndeterminate(true);
			}
			//worker = new GifWorker();
			//worker.execute();
		}
	}

	/**
	 * This method allows the user to choose an output directory.
	 */
	private void chooseOutputPressed() {
		JFileChooser outputChooser = new JFileChooser();
		outputChooser.setCurrentDirectory(new java.io.File("."));
		outputChooser.setDialogTitle("Choose a directory to output to");

		outputChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		int returnValue = outputChooser.showOpenDialog(Gif.this);

		if (returnValue == JFileChooser.APPROVE_OPTION) {
			outputDirectory = outputChooser.getSelectedFile().getAbsoluteFile();
			
			showOutput.setText("Output Destination: " + outputDirectory);
		}
	}

	/**
	 * This method allows the user to choose an input video to make into a gif.
	 * Only valid media files are processed. ANy invalid media files are not processed
	 * and the user is warned about them
	 */
	private void chooseVideoPressed() {
		JFileChooser fileChooser = new JFileChooser();

		fileChooser.setCurrentDirectory(new java.io.File("."));

		fileChooser.setDialogTitle("Choose Video File");

		fileChooser.addChoosableFileFilter(SwingFileFilterFactory.newVideoFileFilter());

		// Allows files to be chosen only. Make sure they are video files in the extract part
		// fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

		fileChooser.setFileFilter(SwingFileFilterFactory.newVideoFileFilter());
		fileChooser.setAcceptAllFileFilterUsed(false);
		int returnValue = fileChooser.showOpenDialog(Gif.this);

		if (returnValue == JFileChooser.APPROVE_OPTION) {
			selectedFile = fileChooser.getSelectedFile();
			showVideo.setText("Video chosen: " + selectedFile.getName());
			InvalidCheck i = new InvalidCheck();
			boolean isValidMedia = i.invalidCheck(fileChooser.getSelectedFile().getAbsolutePath());

			if (!isValidMedia) {
				JOptionPane.showMessageDialog(Gif.this, "You have specified an invalid file.", "Error", JOptionPane.ERROR_MESSAGE);
				makeGif.setEnabled(false);
				return;
			}else{
				makeGif.setEnabled(true);
			}

		}
	}

	/**
	 * This class makes the gif. It extends swingoworker and makes the gif in the background.
	 * The user is notified of the result of the process.
	 * This method currently does the gif making on rgb24.
	 * @author anmol
	 *
	 */
	private class GifWorker extends SwingWorker<Integer, Void>{

		@Override
		protected Integer doInBackground() throws Exception {
			String name = chooseName.getText();

			if(!name.contains(".gif")){
				name = name + ".gif";
			}
			int exitValue = 1;
			String cmd = "/usr/bin/avconv -i " + "" +selectedFile.getAbsolutePath().replaceAll(" ", "\\\\ ") + " -pix_fmt rgb24 " + outputDirectory.getAbsolutePath().replaceAll(" ", "\\\\ ") + File.separator + name;

			ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
			Process process = builder.start();
			process.waitFor();
			exitValue = process.exitValue();
			return exitValue;
		}

		@Override
		protected void done() {
			makeGif.setEnabled(true);
			progress.setIndeterminate(false);
			try {
				int i = get();
				if(i==0){
					JOptionPane.showMessageDialog(Gif.this, "The GIF has successfully been made!");
				}else{
					JOptionPane.showMessageDialog(Gif.this, "The making of the GIF was unsuccessful!");
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}

		}

	}



}
