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
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;

import uk.co.caprica.vlcj.filter.swing.SwingFileFilterFactory;
import vamix.InvalidCheck;


@SuppressWarnings("serial")
/**
 * This class allows the user to get images from a video. A video is put in 
 * and the images making up that video are made output. This class also makes
 * the frame for this feature and it can be accessed from the menu bar.
 * @author anmol
 *
 */
public class Images extends JFrame implements ActionListener{

	private JPanel forInputVideoButton;
	private JPanel forInputVideoLabel;
	private JPanel forOutputButton;
	private JPanel forOutputLabel;
	private JPanel forImagesButton;
	
	private JButton chooseVideo;
	private JLabel showVideo;
	private JButton chooseOutput;
	private JLabel showOutput;
	private JButton makeImages;
	private File selectedFile;
	private File outputDirectory;
	
	private ImagesWorker worker;
	
	private JProgressBar progress;
	private JPanel forBar;
	
	public Images(){
		
		super("Get Images from a video file");
		
		setLayout(new GridLayout(6,1));
		
		forInputVideoButton = new JPanel(new FlowLayout());
		forInputVideoButton.setBorder(new EmptyBorder(10, 10, 10, 10));
		forInputVideoLabel = new JPanel(new BorderLayout());
		forInputVideoLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
		forOutputButton = new JPanel(new FlowLayout());
		forOutputButton.setBorder(new EmptyBorder(10, 10, 10, 10));
		forOutputLabel = new JPanel(new BorderLayout());
		forOutputLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
		forImagesButton = new JPanel(new FlowLayout());
		forImagesButton.setBorder(new EmptyBorder(10, 10, 10, 10));
		forBar = new JPanel(new FlowLayout());
		forBar.setBorder(new EmptyBorder(10, 10, 10, 10));
		
		
		chooseVideo = new JButton("Choose Video");
		chooseVideo.addActionListener(this);
		showVideo = new JLabel("Video Chosen: ");
		chooseOutput = new JButton("Choose output directory");
		chooseOutput.addActionListener(this);
		showOutput = new JLabel("Output destination: ");
		makeImages = new JButton("Get the images");
		makeImages.addActionListener(this);
		makeImages.setEnabled(false);
		progress = new JProgressBar();
		
		
		forInputVideoButton.add(chooseVideo);
		forInputVideoLabel.add(showVideo,BorderLayout.WEST);
		forOutputButton.add(chooseOutput);
		forOutputLabel.add(showOutput,BorderLayout.WEST);
		forImagesButton.add(makeImages);
		forBar.add(progress);
		
		add(forInputVideoButton);
		add(forInputVideoLabel);
		add(forOutputButton);
		add(forOutputLabel);
		add(forImagesButton);
		add(forBar);
		
	}

	@Override
	/**
	 * This method handles all the button presses.
	 */
	public void actionPerformed(ActionEvent e) {
		
		// if the choose video is pressed
		if(e.getSource() == chooseVideo){
			chooseVideoPressed();
		}
		// if the choose output is pressed
		if(e.getSource() == chooseOutput){
			chooseOutputPressed();
		}
		// if the make images button is pressed
		if(e.getSource() == makeImages){
			makeImagesPressed();
		}
	}
	
	/**
	 * This method checks that all the fields are correctly filled. If not, then the user is warned and 
	 * the process is not continued till the fields are filled correctly. A warning is 
	 * given to the user because the number of images created can be large in number. Then 
	 * the worker that gets the images is called if everything is correct.
	 */
	private void makeImagesPressed() {
		boolean carryOn = true;
		
		if((selectedFile == null)||(outputDirectory ==null)){
			JOptionPane.showMessageDialog(Images.this, "Sorry you must fill all fields before carrying on!");
			carryOn = false;
		}
		
		if(carryOn){
			JOptionPane.showMessageDialog(Images.this,"WARNING! If the video is too big, there will be a LOT if images!");
			// call the swing worker and make the images
			
			worker = new ImagesWorker();
			makeImages.setEnabled(false);
			worker.execute();
			progress.setIndeterminate(true);
		}
	}

	/**
	 * This method allows the user to choose a directory to save the output to.
	 * Only a directory can be chosen
	 */
	private void chooseOutputPressed() {

		JFileChooser outputChooser = new JFileChooser();
		outputChooser.setCurrentDirectory(new java.io.File("."));
		outputChooser.setDialogTitle("Choose a directory to output to");

		outputChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		int returnValue = outputChooser.showOpenDialog(Images.this);

		if (returnValue == JFileChooser.APPROVE_OPTION) {
			outputDirectory = outputChooser.getSelectedFile().getAbsoluteFile();
			
			showOutput.setText("Output Destination: " + outputDirectory);
		}
	}
	
	/**
	 * This method allows the user to choose a input video file to get images from. It makes
	 * sure that only video files are chosen and that they are valid. The user cannot
	 * get images from the video if the file is not valid.
	 */
	private void chooseVideoPressed() {
		

		JFileChooser fileChooser = new JFileChooser();
		
		fileChooser.setCurrentDirectory(new java.io.File("."));

		fileChooser.setDialogTitle("Choose Video File");

		fileChooser.addChoosableFileFilter(SwingFileFilterFactory.newVideoFileFilter());

		// Allows files to be chosen only. Make sure they are video files in the extract part
		// fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		
		// put filter to ensure that only video files are chosen.
		fileChooser.setFileFilter(SwingFileFilterFactory.newVideoFileFilter());
		fileChooser.setAcceptAllFileFilterUsed(false);
		int returnValue = fileChooser.showOpenDialog(Images.this);

		if (returnValue == JFileChooser.APPROVE_OPTION) {
			selectedFile = fileChooser.getSelectedFile();
			showVideo.setText("Video chosen: " + selectedFile.getName());
			InvalidCheck i = new InvalidCheck();
			boolean isValidMedia = i.invalidCheck(fileChooser.getSelectedFile().getAbsolutePath());
			// make sure that file is a valid media file.
			if (!isValidMedia) {
				JOptionPane.showMessageDialog(Images.this, "You have specified an invalid file.", "Error", JOptionPane.ERROR_MESSAGE);
				makeImages.setEnabled(false);
				return;
			}else{
				makeImages.setEnabled(true);
			}
		}
	}
	
	/**
	 * This class extends SwingWorker and runs the process that get the images from a video.
	 * It gives back lots of images. It also shows the user the result of the process via a joption dialog.
	 * 
	 * Output : images in jpeg format
	 * 
	 * @author anmol
	 *
	 */
	private class ImagesWorker extends SwingWorker<Integer,Void>{

		@Override
		protected Integer doInBackground() throws Exception {
			
			String name = "image-%1d.jpeg";
			int exitValue = 1;
			
			// start the process
			String cmd = "/usr/bin/avconv -i " + selectedFile.getAbsolutePath().replaceAll(" ", "\\\\ ") + " -r 1 " + outputDirectory.getAbsolutePath().replaceAll(" ", "\\\\ ") + File.separator +name;
			ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
			
			Process process = builder.start();
			process.waitFor();
			
			exitValue = process.exitValue();
	
			return exitValue;

		}

		@Override
		/**
		 * enables the make images button and then gives the user a message that contains
		 * the result of the process.
		 */
		protected void done() {
			makeImages.setEnabled(true);
			progress.setIndeterminate(false);
			try {
				int i = get();
				// show the user the result
				if(i == 0){
					JOptionPane.showMessageDialog(Images.this, "Images created!");
				}else{
					JOptionPane.showMessageDialog(Images.this, "Sorry! Failed to create images");
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
		
	}
}
