package textHandling;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Vector;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.border.Border;

import uk.co.caprica.vlcj.filter.swing.SwingFileFilterFactory;


/**
 * This class has the edit text frame. It makes the text and puts it onto the video.
 * It is a seperate frame that shows up in the menu bar.
 * 
 * input : any valid media file
 * OUTPUt : mp4 file
 * @author anmol
 *
 */
public class EditTextFrame extends JFrame implements ActionListener {
	
	private JPanel topPanel;
	private JPanel bottomPanel;
	private JButton selectInputFileButton;
	private JButton selectOutputDirectoryButton;
	private JLabel outputFileLabel;
	private JLabel selectedFileLabel;
	private JTextField outputFilenameField;
	private File selectedFile;
	private File outputDirectory;
	private JButton startButton;
	private TextEditPanel leftEditPane;
	private TextEditPanel rightEditPane;
	private JProgressBar progressBar;
	private JMenuBar menuBar;
	private JMenu fileMenu;
	private JMenuItem saveMenuItem;
	private JMenuItem openMenuItem;
	
	private Vector<String> fontPaths = new Vector<String>();
	private Vector<String> fontNames = new Vector<String>();
	private ArrayList<Font> fontFiles = new ArrayList<Font>();

	
	public EditTextFrame() {
		super("Add text to start and end of a video");
		
		FontDetectorWorker fontWorker = new FontDetectorWorker(fontPaths, fontNames, fontFiles);
		fontWorker.execute();
		
		this.setLayout(new BorderLayout(2, 2));
		Border blackline = BorderFactory.createLineBorder(Color.black);
		
		selectInputFileButton = new JButton("Select input file");
		selectInputFileButton.addActionListener(this);
		selectedFileLabel = new JLabel();
		topPanel = new JPanel(new BorderLayout(10, 10));
		
		JPanel subTopPanel1 = new JPanel(new BorderLayout(2, 2));
		menuBar = new JMenuBar();
		subTopPanel1.add(menuBar);
        
        fileMenu = new JMenu("File");
        fileMenu.setMnemonic('f');
        menuBar.add(fileMenu);
        
        openMenuItem = new JMenuItem("Open");
        openMenuItem.setMnemonic('o');
        fileMenu.add(openMenuItem);
        openMenuItem.addActionListener(this);
        
        saveMenuItem = new JMenuItem("Save");
        saveMenuItem.setMnemonic('s');
        fileMenu.add(saveMenuItem);
        saveMenuItem.addActionListener(this);
        
		JPanel subTopPanel2 = new JPanel(new BorderLayout());
		
		subTopPanel2.add(selectInputFileButton, BorderLayout.NORTH);
		subTopPanel2.add(new JLabel("Selected File: "), BorderLayout.WEST);
		subTopPanel2.add(selectedFileLabel, BorderLayout.CENTER);
		subTopPanel2.setBorder(blackline);
		topPanel.add(subTopPanel1, BorderLayout.NORTH);
		topPanel.add(subTopPanel2, BorderLayout.SOUTH);
		this.add(topPanel, BorderLayout.NORTH);
		
		leftEditPane = new TextEditPanel("Text to show at start", fontNames, fontFiles);	
		this.add(leftEditPane, BorderLayout.WEST);
		rightEditPane = new TextEditPanel("Text to show at end", fontNames, fontFiles);
		this.add(rightEditPane, BorderLayout.CENTER);
		
		selectOutputDirectoryButton = new JButton("Select output directory");
		selectOutputDirectoryButton.addActionListener(this);
		outputFileLabel = new JLabel("Output file: ");
		outputFilenameField = new JTextField();
		startButton = new JButton("Start");
		startButton.addActionListener(this);
		
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(startButton, BorderLayout.NORTH);
		
		progressBar = new JProgressBar();
		panel.add(progressBar, BorderLayout.SOUTH);
		bottomPanel = new JPanel(new BorderLayout());
		bottomPanel.add(selectOutputDirectoryButton, BorderLayout.NORTH);
		bottomPanel.add(outputFileLabel, BorderLayout.WEST);
		bottomPanel.add(outputFilenameField, BorderLayout.CENTER);
		//bottomPanel.add(startButton, BorderLayout.SOUTH);
		bottomPanel.add(panel, BorderLayout.SOUTH);
		
		bottomPanel.setBorder(blackline);
		this.add(bottomPanel, BorderLayout.SOUTH);
		
	}
	
	/**
	 * This method saves the project settings made in the text.
	 * It can be loaded again for future use.
	 */
	private void saveProject() {
		String[] startSettings = leftEditPane.getSettingsArray();
		String[] endSettings = rightEditPane.getSettingsArray();
		
		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(new java.io.File("."));
		chooser.setDialogTitle("Select file to save project");
		int response = chooser.showSaveDialog(this);

		if (response == JFileChooser.APPROVE_OPTION) {
			File chosenFile = chooser.getSelectedFile().getAbsoluteFile();
			// TODO check if file already exists
			if (chosenFile.exists()) {
				// Warn user
			}
			
			BufferedWriter writer = null;
			try {
				writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(chosenFile)));
				// write settings to file
				
				if (selectedFile != null) {
					writer.write(selectedFile.getAbsolutePath());
				} else {
					writer.write("[?]");
				}
				writer.newLine();
				for (String line : startSettings) {
					writer.write(line);
					writer.newLine();
				}
				writer.write("%");
				writer.newLine();
				for (String line : endSettings) {
					writer.write(line);
					writer.newLine();
				}
				writer.write("&");
				writer.newLine();
				if (outputDirectory != null) {
					writer.write(outputDirectory.getAbsolutePath());
				} else {
					writer.write("[?]");
				}
				writer.newLine();
				if (!outputFilenameField.getText().isEmpty()) {
					writer.write(outputFileLabel.getText());
				} else {
					writer.write("[?]");
				}
				writer.newLine();
				writer.write("&;");
				writer.newLine();

				
			} catch (IOException e) {
				// Warn user saving failed
			} finally {
				try {
					writer.close();
				} catch (IOException e) {
					// Something went wrong
				}
			}
		}
		
	}
	
	/**
	 * This method saves the project settings made in the text.
	 * It can be loaded again for future use.
	 */
	private void loadProject() {
		
		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(new java.io.File("."));
		chooser.setDialogTitle("Select project file to load");
		int response = chooser.showOpenDialog(this);

		File chosenFile = null;
		if (response == JFileChooser.APPROVE_OPTION) {
			chosenFile = chooser.getSelectedFile().getAbsoluteFile();
		} else {
			// something went wrong
			return;
		}
		
		ArrayList<String> start = new ArrayList<String>();
		ArrayList<String> end = new ArrayList<String>();
		ArrayList<String> output = new ArrayList<String>();
		ArrayList<String> selected = start;
		
		// Read file
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(chosenFile));
			String line = null;
			String inputFileString = reader.readLine();
			if (!inputFileString.equals("[?]")) {
				File inputFile = new File(inputFileString);
				selectedFile = inputFile;
				selectedFileLabel.setText(inputFileString);
			}

			boolean isReadingText = false;
			StringBuffer buffer = new StringBuffer();
			while ((line = reader.readLine()) != null) {
				if (line.equals("{")) {
					// Start textarea reading
					isReadingText = true;
					buffer = new StringBuffer();
					continue;
				} else if (line.equals("}")) {
					// finish textarea reading
					isReadingText = false;
					selected.add(buffer.toString());
					continue;
				} else if (isReadingText) {
					buffer.append(line + "\n");
					continue;
				}
				if (line.equals("%")) {
					selected = end;
					continue;
				} else if (line.equals("&")) {
					selected = output;
					continue;
				}
				
				selected.add(line);
				
			}
		} catch (IOException e) {
			
			e.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				
			}
		}
		
		String[] startSettings = new String[start.size()];
		startSettings = start.toArray(startSettings);
		String[] endSettings = new String[end.size()];
		endSettings = end.toArray(endSettings);
		String[] outputSettings = new String[output.size()];
		outputSettings = output.toArray(outputSettings);
		
		leftEditPane.setSettings(startSettings);
		rightEditPane.setSettings(endSettings);
		
	}

	@Override
	/**
	 * This method is for when any of the buttons are clicked.It checks that the correct input is there and that
	 * the valid fields are filled and allows the user to process if everything is valid.
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == selectInputFileButton) {
			
			// Open a file chooser and only allow a video file to be chosen
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setCurrentDirectory(new java.io.File("."));
			fileChooser.setDialogTitle("Choose Video File");
			fileChooser.addChoosableFileFilter(SwingFileFilterFactory.newVideoFileFilter());
			// Prompt user for file selection
			fileChooser.setFileFilter(SwingFileFilterFactory.newVideoFileFilter());
			fileChooser.setAcceptAllFileFilterUsed(false);
	        int returnValue = fileChooser.showOpenDialog(EditTextFrame.this);
	        
	        if (returnValue == JFileChooser.APPROVE_OPTION) {
	          selectedFile = fileChooser.getSelectedFile();
	          selectedFileLabel.setText(selectedFile.getAbsolutePath());
	        }
	        
		} else if (e.getSource() == selectOutputDirectoryButton) {
			JFileChooser outputChooser = new JFileChooser();
			outputChooser.setCurrentDirectory(new java.io.File("."));
			outputChooser.setDialogTitle("Choose a directory to output to");
			
			outputChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			
			int returnValue = outputChooser.showOpenDialog(EditTextFrame.this);
			
			if (returnValue == JFileChooser.APPROVE_OPTION) {
				outputDirectory = outputChooser.getSelectedFile().getAbsoluteFile();
				JOptionPane.showMessageDialog(EditTextFrame.this, "Your file will be saved to " + outputDirectory);
			}
			
		} else if (e.getSource() == startButton) {
			// Check that an input file has been specified
			if (selectedFile == null) {
				// No file is specified
				JOptionPane.showMessageDialog(this, "You must specify an input file.", "ERROR", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			// Check that file is a video
			String command = "file " + "-ib " + "\"" + selectedFile.getAbsolutePath() + "\"" + " | grep \"video\"";
			ProcessBuilder builder = new ProcessBuilder("bash", "-c", command);
			boolean isValidMedia = false;

			try {
				Process process = builder.start();
				process.waitFor();
				if (process.exitValue() == 0) {
					isValidMedia = true;
				}
				
			} catch (IOException | InterruptedException e1) {
				// Couldn't determine file type. Warn user
				JOptionPane.showMessageDialog(EditTextFrame.this, "Unable to determine input file type. Cannot load file.", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			if (!isValidMedia) {
				JOptionPane.showMessageDialog(EditTextFrame.this, "You have specified an invalid input file.", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			// Check that an output filename has been specified
			if (outputFilenameField.getText().isEmpty()) {
				// No file is specified
				JOptionPane.showMessageDialog(this, "You must specify an output file.", "ERROR", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			// Check if output file already exists
			
			
			// Check that atleast one checkbox is selected
			if (!(leftEditPane.shouldProcess() || rightEditPane.shouldProcess())) {
				JOptionPane.showMessageDialog(this, "Neither adding text at start nor end has been selected", "Warning", JOptionPane.WARNING_MESSAGE);
				return;
			}
			
			// Check that text has been added for enabled boxes
			if (leftEditPane.shouldProcess() && leftEditPane.getText().isEmpty()) {
				JOptionPane.showMessageDialog(this, "No text given for start of video", "Warning", JOptionPane.WARNING_MESSAGE);
				return;
			} else if (rightEditPane.shouldProcess() && rightEditPane.getText().isEmpty()) {
				JOptionPane.showMessageDialog(this, "No text given for end of video", "Warning", JOptionPane.WARNING_MESSAGE);
				return;
			}
			
			// Check that an output directory has been specified
			if (outputDirectory == null) {
				// No directory is specified tell user where file will be saved
				JOptionPane.showMessageDialog(this, "No output directory given");
				return;
			}
			
			
			TextWorker worker = new TextWorker();
			startButton.setEnabled(false);
			worker.execute();
			progressBar.setIndeterminate(true);

		} else if (e.getSource() == saveMenuItem) {
			saveProject();
		} else if (e.getSource()== openMenuItem) {
			loadProject();
		}
	}
	
	/**
	 * 
	 *This is the class that does the adding of the text. It ensures that 
	 *the text is received from the text areas and that the text is added to the start and the
	 *end only if the user has selected it to be added to them. Input is a video file
	 *and output is a output MP4.
	 * 
	 * output: mp4 file
	 * @author anmol
	 *
	 */
	class TextWorker extends SwingWorker<Integer, Void> {
		
		private int startFontSize;
		private String startFontPath;
		private String startText;
		private int startDuration;
		private String startFontColor;
		
		private int endFontSize;
		private String endFontPath;
		private String endText;
		private int endDuration;
		private String endFontColor;
		
		private boolean startEnabled;
		private boolean endEnabled;
		
		TextWorker() {
			this.startFontSize = leftEditPane.getFontSize();
			this.startFontPath = fontPaths.get(leftEditPane.getFontIndex());
			this.startText = leftEditPane.getText();
			this.startDuration = leftEditPane.getTimeValue();
			this.startFontColor = leftEditPane.getColor();
			
			this.endFontSize = rightEditPane.getFontSize();
			this.endFontPath = fontPaths.get(rightEditPane.getFontIndex());
			this.endText = rightEditPane.getText();
			this.endDuration = rightEditPane.getTimeValue();
			this.endFontColor = rightEditPane.getColor();
			
			this.startEnabled = leftEditPane.shouldProcess();
			this.endEnabled = rightEditPane.shouldProcess();
			
		}

		@Override
		protected Integer doInBackground() throws Exception {
			// TODO make sure that output directory is specified for default no selection 	
			// TODO incorrect font is used
			
			
			// Check if text overlay for start is enabled
			if (startEnabled) {
				// Do text operation for start
				String fileName = outputFilenameField.getText();
				if (!fileName.contains(".mp4")) { // This should be more sophisticated
					fileName = fileName + ".mp4";
				}
				
				String output = outputDirectory.getAbsolutePath() + System.getProperty("file.separator") + fileName;
				String cmd = "avconv -y -i " + "\""+selectedFile.getAbsolutePath() + "\"" + " -strict experimental -vf \"drawtext=fontfile='" + startFontPath + "':text='" + startText + "':x=main_w/2:y=main_h/2:draw='lt(t\\," + startDuration + "):fontsize=" + startFontSize + ":fontcolor=" + startFontColor + "\" -crf 16 " + "\"" + output + "\"";
				ProcessBuilder textProcessBuilder = new ProcessBuilder("/bin/bash", "-c", cmd);
				textProcessBuilder.redirectErrorStream(true);
				Process textProcess = textProcessBuilder.start();
				
				textProcess.waitFor();
				int exitValue = textProcess.exitValue();
				if (!endEnabled) {
					return exitValue;
				} else if (exitValue != 0) {
					return exitValue;
				}
				
			}
			
			
			// Check if text overlay for end is enabled
			if (endEnabled) {
				
				// Do text operation for end
				String fileName = outputFilenameField.getText();
				if (!fileName.contains(".mp4")) { // This should be more sophisticated
					fileName = fileName + ".mp4";
				}
				
				String inputFilePath;
				String output;
				String originalOutput = outputDirectory.getAbsolutePath() + System.getProperty("file.separator") + fileName;
				//Check if text was overlayed at start
				if (startEnabled) {
					inputFilePath = outputDirectory.getAbsolutePath() + System.getProperty("file.separator") + fileName;
					output = "/tmp/tempOutput.mp4";
				} else {
					inputFilePath = selectedFile.getAbsolutePath();
					output = outputDirectory.getAbsolutePath() + System.getProperty("file.separator") + fileName;
				}
				
				// Get length of video
				MediaLengthWorker mediaWorker = new MediaLengthWorker(inputFilePath);
				mediaWorker.execute();
				int length = mediaWorker.get();
				endDuration = length - endDuration;
				
				String cmd = "avconv -y -i " +  "\"" + inputFilePath + "\"" + " -strict experimental -vf \"drawtext=fontfile='" + endFontPath + "':text='" + endText + "':x=main_w/2:y=main_h/2:draw='gt(t," + endDuration + ")':fontsize=" + endFontSize + ":fontcolor=" + endFontColor + "\" -crf 16 " + "\"" + output + "\"";
				ProcessBuilder textProcessBuilder = new ProcessBuilder("/bin/bash", "-c", cmd);
				textProcessBuilder.redirectErrorStream(true);
				Process textProcess = textProcessBuilder.start();

				textProcess.waitFor();
				
				if (startEnabled) {
					ProcessBuilder pb = new ProcessBuilder("/bin/bash","-c", "mv /tmp/tempOutput.mp4 " +  "\"" + originalOutput+ "\"");
					Process p = pb.start();
					p.waitFor();
				}
				
				int exitValue = textProcess.exitValue();
				return exitValue;
			}
			
			return 1;
			
		}
		
		@Override
		/**
		 * Shows the user what the result of the process was in a nice way.
		 */
		protected void done() {
			progressBar.setIndeterminate(false);

			// Check if process was successful
			startButton.setEnabled(true);
			int exitValue = 0;
			try {
				exitValue = get();
			} catch (InterruptedException e) {
				JOptionPane.showMessageDialog(EditTextFrame.this, "Adding text was cancelled");
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
			
			if (exitValue == 0) {
				JOptionPane.showMessageDialog(EditTextFrame.this, "Adding text was successful");
			} else {
				JOptionPane.showMessageDialog(EditTextFrame.this, "Adding text failed");
			}
			
		}
		
	}
	
}

/**
 * 
 * This class is a font detector. It gets the fonts 
 * that are used in the adding of text
 * @author anmol
 *
 */
class FontDetectorWorker extends SwingWorker<Void, Void> {
	
	Vector<String> fontPaths;
	Vector<String> fontNames;
	ArrayList<Font> fontList;
	
	public FontDetectorWorker(Vector<String> fontPaths, Vector<String> fontNames, ArrayList<Font> fontList) {
		this.fontPaths = fontPaths;
		this.fontNames = fontNames;
		this.fontList = fontList;
	}

	@Override
	protected Void doInBackground() throws Exception {
		String cmd ="find /usr/share/fonts/truetype | grep .ttf";
		ProcessBuilder fontProcessBuilder = new ProcessBuilder("bash", "-c", cmd);
		
		Process fontProcess = fontProcessBuilder.start();
		InputStream stdout = fontProcess.getInputStream();
		BufferedReader stdoutBuffered = new BufferedReader(new InputStreamReader(stdout));

		String line = null;
		while ((line = stdoutBuffered.readLine()) != null) {

			if (isCancelled()) {
				fontProcess.destroy();
			} else {
				fontPaths.add(line);
			}
		}
		
		Pattern p = Pattern.compile("(/usr/share/fonts/truetype/)([a-z|0-9|-]*/)([a-z|0-9|-]*)(\\.ttf$)", Pattern.CASE_INSENSITIVE);
		
		Vector<String> tempFontPaths = new Vector<String>();
		for (String path : fontPaths) {

			Matcher m = p.matcher(path);
			if (m.matches()) {
				fontNames.add(m.group(3));
				tempFontPaths.add(path);
			}
			
		}
		
		fontPaths = tempFontPaths;
		
		// Load font files
		for (String path : fontPaths) {
			try {
				InputStream input = new FileInputStream(path);
				Font font = Font.createFont(Font.TRUETYPE_FONT, input);
				fontList.add(font);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	
}
