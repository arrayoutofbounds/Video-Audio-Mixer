package vamix;
import java.awt.GridLayout;


import javax.swing.JPanel;

import mediacomponent.VideoPlayer;

/**
 * This is the class that has the VideoPlayer component. This
 * class makes the GUI of the playtab.
 * @author anmol
 *
 */
@SuppressWarnings("serial")
public class Play extends JPanel {
	
	public Play() {
		
		setLayout(new GridLayout());
		
		VideoPlayer videoPlayer = new VideoPlayer();
		this.add(videoPlayer);
	}
}
