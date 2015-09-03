package audioFeatures;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import uk.co.caprica.vlcj.filter.swing.SwingFileFilterFactory;
import vamix.InvalidCheck;


@SuppressWarnings("serial")
/**
 * This class has the frame for replacing and overlaying audio.
 * It does not do the actual tasks, but calls on Overlayworker and ReplaceWorker to do the background tasks.
 * But, it creates the frame needed and allows the user to select the video and audio file.
 * 
 * @author anmol
 *
 */
public class ReplaceAudio extends JFrame implements ActionListener {
	
	// decalre the variables
	private JPanel forInputButtons;
	private JPanel forInputValues1;
	private JPanel forInputValues2;
	private JPanel forProgressbar;
	private JPanel forChoosingDirectory;
	private JPanel forDisplayingOutputDirectory;
	private JPanel forOutputName;
	private JPanel forReplaceButton;

	private JButton inputVideo;
	private JButton inputAudio;

	private JLabel showInputVideo;
	private JLabel showInputAudio;

	private JButton chooseDirectory;

	private JLabel showOutputDirectory;
	private JButton replace;
	private File selectedFile = null;
	private File selectedFile2 = null;
	private File outputDirectory = null;
	private JButton overlay;
	private JProgressBar progressBar;


	private JLabel output;
	private JTextField outputName;

	private ReplaceWorker worker;
	private OverlayWorker worker2;
	private File toOverride;

	// initalise the variables
	public ReplaceAudio(){

		super("Replace/Overlay audio of a video");

		setLayout(new GridLayout(8,1));

		forInputButtons = new JPanel(new FlowLayout());
		forInputButtons.setBorder(new EmptyBorder(10, 10, 10, 10));

		forInputValues1 = new JPanel(new BorderLayout());
		forInputValues1.setBorder(new EmptyBorder(10, 10, 10, 10));
		forInputValues2 = new JPanel(new BorderLayout());
		forInputValues2.setBorder(new EmptyBorder(10, 10, 10, 10));

		forOutputName = new JPanel(new FlowLayout());

		forProgressbar = new JPanel(new FlowLayout());
		forProgressbar.setBorder(new EmptyBorder(10, 10, 10, 10));

		forChoosingDirectory = new JPanel(new FlowLayout());
		forChoosingDirectory.setBorder(new EmptyBorder(10, 10, 10, 10));

		forDisplayingOutputDirectory = new JPanel(new BorderLayout());
		forDisplayingOutputDirectory.setBorder(new EmptyBorder(10, 10, 10, 10));

		forReplaceButton = new JPanel(new FlowLayout());
		forReplaceButton.setBorder(new EmptyBorder(10, 10, 10, 10));

		inputVideo = new JButton("Choose input Video");
		inputVideo.addActionListener(this);
		inputAudio = new JButton("Choose input Audio");
		inputAudio.addActionListener(this);

		showInputVideo = new JLabel("Input Video: ");
		showInputAudio = new JLabel("Input Audio: ");

		chooseDirectory = new JButton("Choose Output Destination");
		chooseDirectory.addActionListener(this);

		showOutputDirectory = new JLabel("Output Destination: ");

		replace = new JButton("Replace Audio");
		overlay = new JButton("Overlay Audio");
		replace.addActionListener(this);
		overlay.addActionListener(this);
		replace.setEnabled(false);
		overlay.setEnabled(false);

		output = new JLabel("Enter name for output file: ");
		outputName = new JTextField(5000);
		outputName.setText("");
		outputName.setColumns(40);

		forInputButtons.add(inputVideo);
		forInputButtons.add(inputAudio);

		forInputValues1.add(showInputVideo,BorderLayout.WEST);
		forInputValues2.add(showInputAudio,BorderLayout.WEST);

		forChoosingDirectory.add(chooseDirectory);

		forDisplayingOutputDirectory.add(showOutputDirectory,BorderLayout.WEST);

		forReplaceButton.add(replace);
		forReplaceButton.add(overlay);

		forOutputName.add(output);
		forOutputName.add(outputName);

		progressBar = new JProgressBar();
		progressBar.setStringPainted(true);
		forProgressbar.add(progressBar);

		add(forInputButtons);
		add(forInputValues1);
		add(forInputValues2);
		add(forChoosingDirectory);
		add(forDisplayingOutputDirectory);
		add(forOutputName);
		add(forReplaceButton);
		add(forProgressbar);

	}


	@Override
	/**
	 * This method ensures that the correct input files are chosen.
	 * If not, then user cannot do anything for replacing or overlaying till they enter a 
	 * valid file.
	 */
	public void actionPerformed(ActionEvent e) {


		// open jfilechooser for video

		/**
		 * This method is called when the user wants to enter a video file to manipulate.
		 * It will allow the user to choose a video FILE ONLY!!!
		 */
		if(e.getSource() == inputVideo){
			JFileChooser fileChooser = new JFileChooser();

			fileChooser.setCurrentDirectory(new java.io.File("."));

			fileChooser.setDialogTitle("Choose Video File");

			fileChooser.addChoosableFileFilter(SwingFileFilterFactory.newVideoFileFilter());

			// Allows files to be chosen only. Make sure they are video files in the extract part
		
			fileChooser.setFileFilter(SwingFileFilterFactory.newVideoFileFilter());
			fileChooser.setAcceptAllFileFilterUsed(false);
			int returnValue = fileChooser.showOpenDialog(ReplaceAudio.this);

			if (returnValue == JFileChooser.APPROVE_OPTION) {
				selectedFile = fileChooser.getSelectedFile();
				showInputVideo.setText("Input Video: " + selectedFile.getName());

				InvalidCheck i = new InvalidCheck();
				boolean isValidMedia = i.invalidCheck(fileChooser.getSelectedFile().getAbsolutePath());
				
				// check that the file is a valid media file and allow the process to carry on only if that is true.
				if (!isValidMedia) {
					JOptionPane.showMessageDialog(ReplaceAudio.this, "You have specified an invalid file.", "Error", JOptionPane.ERROR_MESSAGE);
					replace.setEnabled(false);
					overlay.setEnabled(false);
					return;
				}else{
					replace.setEnabled(true);
					overlay.setEnabled(true);
				}
			}

		}

		/**
		 * This method is called when the user wants to enter the audio file that they will
		 * be using to replace/overlay on the video files. ONLY AUDIO FILES CAN GET CHOSEN!!!
		 */
		if(e.getSource() == inputAudio){
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setCurrentDirectory(new java.io.File("."));

			fileChooser.setDialogTitle("Choose Audio File");

			fileChooser.addChoosableFileFilter(SwingFileFilterFactory.newAudioFileFilter());

			// Allows files to be chosen only. Make sure they are video files in the extract part

			fileChooser.setFileFilter(SwingFileFilterFactory.newAudioFileFilter());
			fileChooser.setAcceptAllFileFilterUsed(false);
			int returnValue = fileChooser.showOpenDialog(ReplaceAudio.this);

			if (returnValue == JFileChooser.APPROVE_OPTION) {
				selectedFile2 = fileChooser.getSelectedFile();
				showInputAudio.setText("Input Audio: " + selectedFile2.getName());

				InvalidCheck i = new InvalidCheck();
				boolean isValidMedia = i.invalidCheck(fileChooser.getSelectedFile().getAbsolutePath());

				if (!isValidMedia) {
					JOptionPane.showMessageDialog(ReplaceAudio.this, "You have specified an invalid file.", "Error", JOptionPane.ERROR_MESSAGE);
					replace.setEnabled(false);
					overlay.setEnabled(false);
					return;
				}else{
					replace.setEnabled(true);
					overlay.setEnabled(true);
				}
			}
		}

		/**
		 * This method is called when the user wants to choose the output directory, Only 
		 * directories can be chosen.
		 */
		if(e.getSource() == chooseDirectory){

			JFileChooser outputChooser = new JFileChooser();
			outputChooser.setCurrentDirectory(new java.io.File("."));
			outputChooser.setDialogTitle("Choose a directory to output to");

			outputChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

			int returnValue = outputChooser.showOpenDialog(ReplaceAudio.this);
			
			// show the user the directory chosen
			if (returnValue == JFileChooser.APPROVE_OPTION) {
				outputDirectory = outputChooser.getSelectedFile().getAbsoluteFile();
			
				showOutputDirectory.setText("Output Directory: " + outputDirectory);
			}
		}
		
		/**
		 * This method is called when the user presses the replace button.
		 * This will call the ReplaceWorker and do the manipulation there. It also checks
		 * to see if there is a file like the one wanted and allows the user to override or rename
		 * current file. Checks are also done to ensure that all fields are filled before actually
		 * doing the task. All files are output types MP4.
		 */
		if(e.getSource() == replace) {
			//do the whole swingworker replacement thing
			boolean carryOn = true;
			// do checks that everything has been entered
			if((selectedFile2 == null)||((selectedFile == null)||(outputDirectory == null)||(outputName.getText().equals("")))){
				JOptionPane.showMessageDialog(ReplaceAudio.this,"Some fields are empty! Please fill all the fields to procced!");
				carryOn = false;
			}

			if(carryOn){
				String a = outputName.getText();
				boolean override = false;
				// set the suffix to mp4 if not there
				if(!a.contains(".mp4")){
					a = a + ".mp4";
				}
				File propFile = new File(outputDirectory,a);

				if(propFile.exists()){
					toOverride = propFile;
					String[] options = {"Yes,Override!","No! Do not override!"};
					int code = JOptionPane.showOptionDialog(ReplaceAudio.this, 
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
						worker = new ReplaceWorker(replace,selectedFile,selectedFile2,outputDirectory, overlay,  progressBar,outputName);
						replace.setEnabled(false);
						overlay.setEnabled(false);
						worker.execute();
					}else{
						JOptionPane.showMessageDialog(ReplaceAudio.this, "Please choose another name to carry on extracting!");
					}
				}else{
					worker = new ReplaceWorker(replace,selectedFile,selectedFile2,outputDirectory, overlay,  progressBar,outputName);
					replace.setEnabled(false);
					overlay.setEnabled(false);
					worker.execute();
				}

			}
		}

		/**
		 * This method is called when the user presses the overlay button.
		 * This will call the OverlayWorker and do the manipulation there. It also checks
		 * to see if there is a file like the one wanted and allows the user to override or rename
		 * current file. Checks are also done to ensure that all fields are filled before actually
		 * doing the task. All files are output types MP4.
		 */
		if(e.getSource() == overlay){
			boolean carryOn = true;
			// do checks that everything has be entered
			if((selectedFile2 == null)||((selectedFile == null)||(outputDirectory == null)||(outputName.getText().equals("")))){
				JOptionPane.showMessageDialog(ReplaceAudio.this,"Some fields are empty! Please fill all the fields to procced!");
				carryOn = false;
			}

			if(carryOn){

				String a = outputName.getText();
				boolean override = false;
				if(!a.contains(".mp4")){
					a = a + ".mp4";
				}
				File propFile = new File(outputDirectory,a);

				if(propFile.exists()){
					toOverride = propFile;
					String[] options = {"Yes,Override!","No! Do not override!"};
					int code = JOptionPane.showOptionDialog(ReplaceAudio.this, 
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
						worker2 = new OverlayWorker(replace,selectedFile,selectedFile2,outputDirectory, overlay,  progressBar,outputName);
						replace.setEnabled(false);
						overlay.setEnabled(false);
						worker2.execute();
					}else{
						JOptionPane.showMessageDialog(ReplaceAudio.this, "Please choose another name to carry on extracting!");
					}
				}else{
					worker2 = new OverlayWorker(replace,selectedFile,selectedFile2,outputDirectory, overlay,  progressBar,outputName);
					replace.setEnabled(false);
					overlay.setEnabled(false);
					replace.setEnabled(false);
					overlay.setEnabled(false);
					worker2.execute();
				}

				//worker2 = new OverlayWorker();
				//worker2.execute();
			}
		}
	}
}

