package videoFeatures;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import uk.co.caprica.vlcj.filter.swing.SwingFileFilterFactory;
import vamix.InvalidCheck;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class allows the user
 * to make their own subtitles for a video. It also makes the GUI for the
 * subtitles window. It write the subtitles to a srt file and stores that
 * in the same place as the video chosen. Then when the video is played it shows the
 * subtitles.
 * @author anmol
 *
 */
public class Subtitles extends JFrame implements ActionListener {


	private JSplitPane splitPane;
	private JTextArea left;
	private JTextArea info;
	private JButton add;
	private JButton makesrt;
	private JPanel container;

	// the panel that has everything on the left
	private JPanel initial;

	// this is the panel that has the time
	private JPanel times;
	// this has the text area "left"
	private JPanel enterString;

	private JPanel arrangeButtons;
	// panels inside the times panel
	private JPanel startTime;
	private JPanel endTime;
	private JPanel labelPreview;
	private JPanel chooseInput;
	private JPanel showInput;

	private JLabel startLabel;
	private JLabel endLabel;
	private JLabel foruser;
	private JButton helpButton;

	private JTextField enterStart;
	private JTextField enterEnd;

	private JButton getInput;
	private JLabel input;
	private File selectedFile;
	private boolean carryOn;

	private int counter =1;

	private File srtFile;

	public Subtitles(){
		getContentPane().setLayout(new BorderLayout());

		enterString = new JPanel(new BorderLayout());

		container = new JPanel(new BorderLayout());
		arrangeButtons = new JPanel(new GridLayout(2,1));

		initial = new JPanel(new BorderLayout());
		times = new JPanel(new GridLayout(5,1));
		
		//create all the panels
		startTime = new JPanel(new FlowLayout());
		endTime = new JPanel(new FlowLayout());
		labelPreview = new JPanel(new FlowLayout());
		chooseInput = new JPanel(new FlowLayout());
		showInput = new JPanel(new BorderLayout());

		//create all the components
		startLabel = new JLabel("Start Time (hh:mm:ss): ");
		endLabel = new JLabel("End Time (hh:mm:ss): ");
		enterStart = new JTextField(10);
		enterEnd = new JTextField(10);
		enterStart.setText("");
		enterEnd.setText("");
		foruser = new JLabel("Enter what you want at this time:");
		getInput = new JButton("Choose file");
		getInput.addActionListener(this);
		input = new JLabel("Video chosen: ");
		helpButton = new JButton("Need Help! Click here!");
		helpButton.setPreferredSize(new Dimension(180,28));
		helpButton.addActionListener(this);

		startTime.add(startLabel);
		startTime.add(enterStart);

		endTime.add(endLabel);
		endTime.add(enterEnd);

		labelPreview.add(foruser);

		chooseInput.add(getInput);
		chooseInput.add(helpButton);
		showInput.add(input,BorderLayout.WEST);

		times.add(chooseInput);
		times.add(showInput);
		times.add(startTime);
		times.add(endTime);
		times.add(labelPreview);

		left = new JTextArea(8,20);
		JScrollPane scroll = new JScrollPane(left);
		// add the left are to the enter string panel
		enterString.add(scroll);

		// this has the times and the area for the user to add string
		initial.add(times,BorderLayout.NORTH);
		initial.add(enterString,BorderLayout.CENTER);	
		
		
		// add the scroll
		info = new JTextArea();
		info.setEditable(true);
		JScrollPane scroll2 = new JScrollPane(info);

		add = new JButton("Add to subtitles preview ---->");
		add.addActionListener(this);
		add.setEnabled(false);
		arrangeButtons.add(add);

		makesrt = new JButton("Make subtitles file");
		makesrt.addActionListener(this);
		// enable when there is atleast one thing in the info text area on the right side!
		makesrt.setEnabled(false);
		arrangeButtons.add(makesrt);

		// this is the container for the the buttons at the bottom
		container.add(arrangeButtons);


		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,initial,scroll2);

		getContentPane().add(splitPane,BorderLayout.CENTER);
		getContentPane().add(container,BorderLayout.SOUTH);
		splitPane.setResizeWeight(0.5d);

	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if(e.getSource() == getInput){
			choosingInputPressed();
		}

		if(e.getSource() == add){
			addPressed();
		}

		// when the making the subtitles button is pressed
		if(e.getSource() == makesrt){
			makeSubtitlesPressed();
		}
		
		if(e.getSource()==helpButton){
			JOptionPane.showMessageDialog(Subtitles.this,"Add what you want in the text box underneath. \nThen click add to preview to get it in srt format. \n The area"
					+ " at the bottom is just a place to add a normal sentence or paragraph before adding it to subtitles! \n ONLY THE TEXT FROM THE AREA "
					+ "ON THE RIGHT IS ADDED AS SUBTITLES! \n\n So remember to click ADD TO PREVIEW before making the subtitles!\n The preview on the right canbe edited if you know srt format!\n PLEASE BE CAREFUL WITH EDITING PREVIEW!");
		}
	}

	/**
	 * This method takes the text from the right text area and puts it into a text file
	 * that is name with a srt extension and the same name as the video. Its destination
	 * is also in the same place as the selected video file. 
	 */
	private void makeSubtitlesPressed() {
		// get the text inside the right text area
		String finalContent = info.getText();
		// now make a text file
		// and send the string to there and make the extension srt.

		JOptionPane.showMessageDialog(Subtitles.this, "WARNING! IF a subtitles file exists for this video then it will be overwritten!");
		
		srtFile = new File(selectedFile.getParent() + File.separator + selectedFile.getName()+".srt");

		//now add the whole string to the text file

		// if file doesnt exists, then create it
		if (!srtFile.exists()) {
			try {
				srtFile.createNewFile();
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, "Sorry, the making of the subtitles file failed!");
				e.printStackTrace();
			}
		}
		try{
			FileWriter fw = new FileWriter(srtFile.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(finalContent);
			bw.close();
			JOptionPane.showMessageDialog(Subtitles.this, "Adding the subtitles was completed successfully!");
		}catch(Exception e ){
			JOptionPane.showMessageDialog(null, "Writing to the file failed");
		}
	}
	
	/**
	 *This method checks that all the fields are filled. Then
	 *it sends a call to the sendToPreview() method, which 
	 *creates the final srt format.
	 */
	private void addPressed() {
		// first check that all fields are filled
		// Then ensure that the times entered are correct
		// make a global int counter and add the srt format to the text area

		if((selectedFile==null)||(enterStart.getText().equals(""))||(enterEnd.getText().equals(""))||(left.getText().equals(""))){
			JOptionPane.showMessageDialog(Subtitles.this, "All fields are not filled! Please fill to carry on!");
			return;
		}

		// now check that the times entered are correct.

		String inputStartTime = enterStart.getText();
		String inputLengthTime = enterEnd.getText();
		String pattern = "^[0-9]{2}:[0-9]{2}:[0-9]{2}$";
		Pattern regex = Pattern.compile(pattern);
		Matcher check = regex.matcher(inputStartTime);
		Matcher check2 = regex.matcher(inputLengthTime);


		boolean carryOn2;
		boolean carryOn3;
		if(!check.find()){
			JOptionPane.showMessageDialog(Subtitles.this, "Sorry the start time is not the correct format!");
			carryOn2 = false;
		}else{
			carryOn2 = true;
		}

		if(!check2.find()){
			JOptionPane.showMessageDialog(Subtitles.this, "Sorry the end time is not the correct format!");
			carryOn3 = false;
		}else{
			carryOn3 = true;
		}

		if(carryOn3 && carryOn2 ){
			carryOn = true;
		}else{
			carryOn = false;
		}

		if(carryOn){
			// if everything is fine then carry on
			sendToPreview();
		}

	}
	
	/**
	 * This method is called when the add to preview
	 * button is pressed. It converts the string to srt format 
	 * and puts it on the right side text area, ready to put into a srt file.
	 */
	private void sendToPreview() {
		// Now get the srt format and then make a global counter and add to the preview text area on the right.
		String starting = enterStart.getText();
		String ending = enterEnd.getText();
		String line = left.getText().trim();
		// allow the user to make the srt file
		makesrt.setEnabled(true);

		info.append(""+counter + "\n");
		info.append(""+starting + ",000 " + "--> " + "" +ending + ",000\n");
		info.append(line);
		info.append("\n\n");

		counter++;
		clear();
		// algorithm for srt format:
		// counter
		// time start --> time end
		// line

	}
	
	/**
	 * This method clears the fields and allows them to be filled again
	 * after one input.
	 */
	private void clear() {
		left.setText("");
	}
	
	/**
	 * This method allows the user to select a valid video file to add
	 * subtitles to.
	 */
	private void choosingInputPressed() {
		info.setText("");
		JFileChooser fileChooser = new JFileChooser();

		fileChooser.setCurrentDirectory(new java.io.File("."));

		fileChooser.setDialogTitle("Choose Video File");

		fileChooser.addChoosableFileFilter(SwingFileFilterFactory.newVideoFileFilter());

		// Allows files to be chosen only. Make sure they are video files in the extract part

		fileChooser.setFileFilter(SwingFileFilterFactory.newVideoFileFilter());
		fileChooser.setAcceptAllFileFilterUsed(false);
		int returnValue = fileChooser.showOpenDialog(Subtitles.this);

		if (returnValue == JFileChooser.APPROVE_OPTION) {
			selectedFile = fileChooser.getSelectedFile();
			input.setText("Video chosen: " + selectedFile.getName());
			InvalidCheck i = new InvalidCheck();
			boolean isValidMedia = i.invalidCheck(fileChooser.getSelectedFile().getAbsolutePath());

			if (!isValidMedia) {
				JOptionPane.showMessageDialog(Subtitles.this, "You have specified an invalid file.", "Error", JOptionPane.ERROR_MESSAGE);
				add.setEnabled(false);
				return;
			}else{
				add.setEnabled(true);
			}
		}
	}
}
