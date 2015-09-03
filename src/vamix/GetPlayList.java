package vamix;

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
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

/**
 * This Class allows the user to MAKE a playlist. It is
 * a frame that allows the user to choose where to get the playlist.
 * The playlist is a text file with the paths of the files. But this class
 * does not go into that detail. It is just a class that has the GUI to 
 * make UI and get the playlist folder and destination.
 * 
 * @author anmol
 *
 */
public class GetPlayList extends JFrame implements ActionListener {
	
	private JPanel nameOfPlaylist;
	private JPanel chooseDestination;
	private JPanel startMakingPlaylist;
	
	private JLabel forName;
	private JTextField enterName;
	private JButton chooseOutput;
	private JPanel showOutput;
	private JLabel outputLabel;
	private JButton makePlaylist;
	//set the output directory to the home directory for now.
	public static  File outputDirectory = (new java.io.File("."));
	
	public static String selectedplaylistname;
	
	public GetPlayList(){
		setLayout(new GridLayout(4,1));
		
		nameOfPlaylist = new JPanel(new FlowLayout());
		nameOfPlaylist.setBorder(new EmptyBorder(10, 10, 10, 10));
		chooseDestination = new JPanel();
		chooseDestination.setBorder(new EmptyBorder(10, 10, 10, 10));
		startMakingPlaylist = new JPanel();
		startMakingPlaylist.setBorder(new EmptyBorder(10, 10, 10, 10));
		showOutput = new JPanel(new BorderLayout());
		showOutput.setBorder(new EmptyBorder(10, 10, 10, 10));
		
		forName = new JLabel("Name of playlist: ");
		enterName = new JTextField(5000);
		enterName.setColumns(40);
		chooseOutput = new JButton("Choose Destination");
		outputLabel = new JLabel("Output Destination :");
		makePlaylist = new JButton("Make the playlist");
		
		makePlaylist.addActionListener(this);
		chooseOutput.addActionListener(this);
		
		nameOfPlaylist.add(forName);
		nameOfPlaylist.add(enterName);
		
		chooseDestination.add(chooseOutput);
		
		showOutput.add(outputLabel,BorderLayout.LINE_START);
		startMakingPlaylist.add(makePlaylist);
		
		add(nameOfPlaylist);
		add(chooseDestination);
		add(showOutput);
		add(startMakingPlaylist);
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		
		if(e.getSource() == makePlaylist){
			
			// get the name of the playlist that the user wants and then make a folder with that name and make it a public varaible
			// then make a directory with that name in the chosen ouput directory.
			
			selectedplaylistname = enterName.getText();
			this.dispose();
			
		}
		
		if(e.getSource() == chooseOutput){
			JFileChooser outputChooser = new JFileChooser();
			outputChooser.setCurrentDirectory(new java.io.File("."));
			outputChooser.setDialogTitle("Choose a directory to output to");

			outputChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

			int returnValue = outputChooser.showOpenDialog(GetPlayList.this);

			if (returnValue == JFileChooser.APPROVE_OPTION) {
				outputDirectory = outputChooser.getSelectedFile().getAbsoluteFile();
				outputLabel.setText("Output Destination: " + outputDirectory);
			}
		}
		
	}
}
