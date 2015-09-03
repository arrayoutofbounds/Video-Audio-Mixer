package tools;
import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * This class is there to ensure that the help button shows up the help frame.
 * Even if it is a jar file, this frame has to show up. It reads from the README.MD and then puts 
 * it onto the help frame.
 * @author anmol
 *
 */
public class HelpFrame extends JFrame{
	
	private JPanel p;
	private JTextArea area; 
	private JScrollPane scroll;
	File readme;
	
	// make the frame
	public HelpFrame(){
		super("Help");
		setLayout(new BorderLayout());
		
		p = new JPanel(new BorderLayout());
		
		area = new JTextArea();
		area.setLineWrap(true);
		area.setEditable(false);
		
		scroll = new JScrollPane(area,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		p.add(scroll,BorderLayout.CENTER);
		add(p,BorderLayout.CENTER);
		readme = new File(System.getProperty("user.dir") +  File.separator + "src" + File.separator + "tools" + File.separator + "README.md");
		
		 
	}
	
	/**
	 * this method adds the text from the readme file to the frame. Ensures that the 
	 * readme file is FOUND even if it a jar file.
	 */
	public void appendReadmeFile(){
		
		try {
			InputStream in = getClass().getResourceAsStream("README.md");
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
 			//BufferedReader br = new BufferedReader(new FileReader(readme));
 			String line = br.readLine();
 	        while (line != null) {
 	            area.append(line);
 	            area.append(System.lineSeparator());
 	            line = br.readLine();
 	        }
 	        br.close();
 		} catch (IOException e) {
 			// Could not read log file, display error message
 			JOptionPane.showMessageDialog(null, "Could not open log file: No log available", "ERROR", JOptionPane.ERROR_MESSAGE);
 		}
 		
		// makes sure that the scroll is on top when the frame is opened again.
		area.setCaretPosition(0);
	}
	

}
