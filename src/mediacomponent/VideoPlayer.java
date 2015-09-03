package mediacomponent;


import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import uk.co.caprica.vlcj.filter.swing.SwingFileFilterFactory;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.player.embedded.videosurface.CanvasVideoSurface;
import vamix.ExtendedFrame;
import vamix.InvalidCheck;
import vamix.InvalidCheckAudio;

/**
 * This class is the main class of the program. It has the media player component, playing features and
 * a history. This is a component,so that it can be moved to any container and be placed. i.e It can be
 * placed in a JPanel or a JFrame. The EmbeddedMediaPlayer is part of the component. There are also other 
 * features like screenshot, forwarding ,rewinding etc. The most detailed feature is that a part of a video
 * can be extracted by double clicking on the screen when recording has to be started and then double clicked 
 * when the recording can be stopped. 
 * @author anmol
 *
 */
@SuppressWarnings("serial")
public class VideoPlayer extends JPanel  implements ActionListener, ChangeListener, MouseListener{
    
	//initalise and declare variables
	
	
	public static EmbeddedMediaPlayer mediaPlayer;

	private int clicked = 0;

	private JButton toggleExtraPanel;

	// panels in the extra panel
	private JPanel extra;

	// inside the extra panel
	private JPanel area;
	private JPanel other;

	// inside the "other" panel, put 2 more panels
	private JPanel chooseFilePanel;
	private JPanel skipButtonsPanel;

	// in the area panel
	private JLabel showHistoryTitle = new JLabel("History");
	public static JTextArea history;
	private JPopupMenu popup;
	private JMenuItem normalPlay;
	private JMenuItem onepointfivePlay;
	private JMenuItem twoPlay;
	private JMenuItem twopointfivePlay;
	private JMenuItem threePlay;
	private JMenuItem slow;

	private JButton hide;
	private JButton load;

	private JButton snapShotButton;
	private JButton rewindBack;
	private JButton fastForwardButton;
	private static JButton playButton; 
	private JButton muteButton;
	private JButton forwardButton;
	private JButton backButton;
	private JLabel timeLabel;
	public static JSlider timeSlider;
	private JSlider volumeSlider;

	private JPanel everythingElse = new JPanel(new FlowLayout());
	private JLabel volumeLabel;


	public static String filePath;
	private boolean hasPlayed;

	//references to the images for the icons
	// Rest of the images are all from the same site (iconfinder.com)
	//https://www.iconfinder.com/icons/216309/media_pause_icon#size=16
	//https://www.iconfinder.com/icons/211876/play_icon#size=16
	private ImageIcon play = null;
	private ImageIcon pause = null;
	private ImageIcon skipback = null;
	private ImageIcon skipforward = null;
	private ImageIcon mute = null;
	private ImageIcon unmuted = null;
	private ImageIcon stop = null;
	private ImageIcon rewind = null;
	private ImageIcon fastForward = null;
	private ImageIcon collapse = null;
	private ImageIcon show = null;
	private ImageIcon snapshot = null;
	private int volumeBeforeMuted;
	private JButton stopVideo;

	private JButton chooseFileToPlay;

	private boolean loadedPlayIcon = true;
	private boolean loadedPauseIcon = true;
	private boolean loadedMuteIcon = true;
	private boolean loadedUnmuteIcon = true;

	private boolean isFastForwarding = false;
	private boolean isRewinding = false;

	rewindWorker worker;
	VideoExtracter vx;
	String startTime = "";
	String lengthTime = "";

	private static long start;
	private static long end;
	private static long length;

	public VideoPlayer()  {
		super(new BorderLayout(10,10));

		// Initialize Video surface
		Canvas canvas = new Canvas();
		MediaPlayerFactory mediaPlayerFactory = new MediaPlayerFactory();
		CanvasVideoSurface videoSurface = mediaPlayerFactory.newVideoSurface(canvas);
		mediaPlayer = mediaPlayerFactory.newEmbeddedMediaPlayer();
		mediaPlayer.setVideoSurface(videoSurface);

		// Add the media player to the center of the video player panel
		add(canvas,BorderLayout.CENTER);
		
		// add the canvas
		
		this.add(canvas);
		videoSurface.canvas().setSize(800, 600);
		videoSurface.canvas().setBackground(Color.BLACK);

		canvas.addMouseListener(this);
		
		// declare all the speed changes
		popup = new JPopupMenu("Popup");
		slow = new JMenuItem("0.5x");
		normalPlay = new JMenuItem("1x");
		onepointfivePlay = new JMenuItem("1.5x");
		twoPlay = new JMenuItem("2x");
		twopointfivePlay = new JMenuItem("2.5x");
		threePlay = new JMenuItem("3x");
		
		// add the speeds to the popup
		popup.add(slow);
		popup.add(normalPlay);
		popup.add(onepointfivePlay);
		popup.add(twoPlay);
		popup.add(twopointfivePlay);
		popup.add(threePlay);
		
		// add all the listeners to the speed
		slow.addActionListener(this);
		normalPlay.addActionListener(this);
		onepointfivePlay.addActionListener(this);
		twoPlay.addActionListener(this);
		twopointfivePlay.addActionListener(this);
		threePlay.addActionListener(this);
	
		// declare all the panels in the extra
		extra = new JPanel(new BorderLayout());

		area = new JPanel(new BorderLayout());
		other = new JPanel(new BorderLayout());

		chooseFilePanel = new JPanel(new BorderLayout());
		skipButtonsPanel = new JPanel(new BorderLayout(5, 5));

		history = new JTextArea();	
		history.setEditable(false);

		
		// Read log file into log panel
		try {
			File logFile = LogFile.getLogFile();
			BufferedReader br = new BufferedReader(new FileReader(logFile));
			String line = br.readLine();
			while (line != null) {
				history.append(line);
				history.append(System.lineSeparator());
				line = br.readLine();
			}
			br.close();
		} catch (IOException e) {
			// Could not read log file, display error message
			JOptionPane.showMessageDialog(null, "Could not open log file: No log available", "ERROR", JOptionPane.ERROR_MESSAGE);
		}

		// get the snapshot image and if not then put the text
		try {
			snapshot = new ImageIcon(ImageIO.read(getClass().getResourceAsStream("resources/snapshot.png")));
		} catch (IllegalArgumentException | IOException e) {
			// Unable to load mute icon, revert to using text
			loadedMuteIcon = false;
			//muteButton.setText("Mute");
		}

		// add listeners to the snpashot
		snapShotButton = new JButton(snapshot);
		snapShotButton.setToolTipText("Take Screenshot");
		snapShotButton.addActionListener(this);
		
		// Everything is in a grid bag layout for the panel. now add stuff to the panel and
		// put it in a grid bag layout
		everythingElse.setLayout(new GridBagLayout());

		// create the volume label and add it
		volumeLabel = new JLabel("Volume");
		GridBagConstraints gb0 = new GridBagConstraints();
		gb0.gridx = 0;
		gb0.gridy = 1;
		gb0.gridwidth = 1;
		gb0.weightx = 0;
		gb0.weighty = 0;
		everythingElse.add(volumeLabel,gb0);

		// Add the volume slider
		volumeSlider = new JSlider(SwingConstants.HORIZONTAL);
		volumeSlider.setToolTipText("" + volumeSlider.getValue());
		GridBagConstraints gb = new GridBagConstraints();
		gb.gridx = 1;
		gb.gridy = 1;
		gb.gridwidth = 1;
		gb.weightx = 0.5;
		gb.weighty = 0;
		gb.fill = GridBagConstraints.HORIZONTAL;
		everythingElse.add(volumeSlider,gb);

		// Add mute/unmute buttons
		try {
			mute = new ImageIcon(ImageIO.read(getClass().getResourceAsStream("resources/mute.png")));
		} catch (IllegalArgumentException | IOException e) {
			// Unable to load mute icon, revert to using text
			loadedMuteIcon = false;
			//muteButton.setText("Mute");
		}

		try {
			unmuted = new ImageIcon(ImageIO.read(getClass().getResourceAsStream("resources/unmuted.png")));
		} catch (IllegalArgumentException | IOException e) {
			// Unable to load unmute icon, revert to using text
			loadedUnmuteIcon = false;
		}

		if (loadedUnmuteIcon) {
			muteButton = new JButton(unmuted);
		} else {
			muteButton = new JButton("Unmuted");
		}
		GridBagConstraints gb1 = new GridBagConstraints();
		gb1.gridx = 2;
		gb1.gridy = 1;
		gb1.gridwidth = 1;
		gb1.gridheight = 1;
		gb1.weightx = 0;
		gb1.weighty = 0;
		gb1.insets = new Insets(0,5,0,5);
		everythingElse.add(muteButton,gb1);
		muteButton.setToolTipText("Mute/Unmute");


		boolean iconLoaded = true;

		// Add back button and its icon
		try {
			skipback = new ImageIcon(ImageIO.read(getClass().getResourceAsStream("resources/skipback.png")));
		} catch (IllegalArgumentException | IOException e) {
			// Couldn't load icon
			iconLoaded = false;
		}

		if (iconLoaded) {
			backButton = new JButton(skipback);
		} else {
			backButton = new JButton("Back");
			iconLoaded = true;
		}

		backButton.setToolTipText("Skip Back");

		// add rewind button

		try {
			rewind = new ImageIcon(ImageIO.read(getClass().getResourceAsStream("resources/rewind.png")));
		} catch (IllegalArgumentException | IOException e) {
			// Couldn't load icon
			iconLoaded = false;
		}

		if (iconLoaded) {
			rewindBack= new JButton(rewind);
		} else {
			rewindBack = new JButton("Rewind");
			iconLoaded = true;
		}

		rewindBack.setToolTipText("Rewind");
		// add the rewind button to the panel
		GridBagConstraints gb2 = new GridBagConstraints();
		gb2.gridx = 3;
		gb2.gridy = 1;
		gb2.gridwidth = 1;
		gb2.gridheight = 1;
		gb2.weightx = 0;
		gb2.weighty = 0;
		gb2.insets = new Insets(0,5,0,5);
		rewindBack.addActionListener(this);
		everythingElse.add(rewindBack,gb2);

		// Add play button
		try {
			play = new ImageIcon(ImageIO.read(getClass().getResourceAsStream("resources/play.png")));
		} catch (IllegalArgumentException | IOException e) {
			// Couldn't load play icon
			loadedPlayIcon = false;
		}
		
		// add the pause button icon

		try {
			pause = new ImageIcon(ImageIO.read(getClass().getResourceAsStream("resources/pause.png")));
		} catch (IllegalArgumentException | IOException e) {
			// Couldn't load pause icon
			loadedPauseIcon = false;
		}

		if (loadedPlayIcon) {
        	playButton = new JButton(play);
        } else {
        	playButton = new JButton("Play");
        }
		GridBagConstraints gb3 = new GridBagConstraints();
		gb3.gridx = 4;
		gb3.gridy = 1;
		gb3.gridwidth = 1;
		gb3.gridheight = 1;
		gb3.weightx = 0;
		gb3.weighty = 0;
		gb3.insets = new Insets(0,5,0,5);
        playButton.setToolTipText("Play");
		everythingElse.add(playButton,gb3);

		// Add forward button icon
		try {
			skipforward = new ImageIcon(ImageIO.read(getClass().getResourceAsStream("resources/skipforward.png")));
		} catch (IllegalArgumentException | IOException e) {
			// Couldn't load forward icon
			iconLoaded = false;
		}

		
		if (iconLoaded) {
			forwardButton = new JButton(skipforward);
		} else {
			forwardButton = new JButton("Forward");
			iconLoaded = true;
		}
		forwardButton.setToolTipText("Skip Forward");



		/// add the fast forward button

		try {
			fastForward = new ImageIcon(ImageIO.read(getClass().getResourceAsStream("resources/fastforward.png")));
		} catch (IllegalArgumentException | IOException e) {
			// Couldn't load forward icon
			iconLoaded = false;
		}


		if (iconLoaded) {
			fastForwardButton = new JButton(fastForward);
		} else {
			forwardButton = new JButton("Fast Forward");
			iconLoaded = true;
		}
		fastForwardButton.setToolTipText("Fast Forward");

		
		// add fast forward to the panel
		GridBagConstraints gb4 = new GridBagConstraints();
		gb4.gridx = 5;
		gb4.gridy = 1;
		gb4.gridwidth = 1;
		gb4.gridheight = 1;
		gb4.weightx = 0;
		gb4.weighty = 0;
		gb4.insets = new Insets(0,5,0,5);
		fastForwardButton.addActionListener(this);
		everythingElse.add(fastForwardButton,gb4);

		chooseFileToPlay = new JButton(" Choose file to play");
		chooseFileToPlay.setToolTipText("Select a file to play in the video player");
		chooseFileToPlay.addActionListener(this);

		// Add time label to the panel
		timeLabel = new JLabel("00:00:00");
		GridBagConstraints gb5 = new GridBagConstraints();
		gb5.gridx = 0;
		gb5.gridy = 0;
		gb5.gridheight = 1;
		gb5.gridwidth = 1;
		gb5.weightx = 0;
		gb5.weighty = 0;
		gb5.insets = new Insets(5,10,20,10);
		timeLabel.setToolTipText("Time Elapsed");
		everythingElse.add(timeLabel,gb5);

		// Add time slider to the panel
		timeSlider = new JSlider();
		GridBagConstraints gb6 = new GridBagConstraints();
		gb6.gridx = 1;
		gb6.gridy =0;
		gb6.gridwidth = 7;
		gb6.gridheight = 1;
		gb6.fill = GridBagConstraints.HORIZONTAL;
		gb6.weightx = 1;
		gb6.weighty = 0;
		gb6.insets = new Insets(5,10,20,10);
		timeSlider.setToolTipText("Time Slider");
		everythingElse.add(timeSlider,gb6);

		// add image to the stop button and add it to the panel
		try {
			stop = new ImageIcon(ImageIO.read(getClass().getResourceAsStream("resources/stop.png")));
		} catch (IllegalArgumentException | IOException e) {
			// Couldn't load stop icon
			iconLoaded = false;
		}

		if (iconLoaded) {
			stopVideo = new JButton(stop);
		} else {
			stopVideo = new JButton("Stop");
		}
		GridBagConstraints gb7 = new GridBagConstraints();
		gb7.gridx = 6;
		gb7.gridy = 1;
		gb7.gridwidth = 1;
		gb7.weightx = 0;
		gb7.weighty = 0;
		everythingElse.add(stopVideo,gb7);
		stopVideo.setToolTipText("Stop");
		stopVideo.addActionListener(this);

		// add toggle icons 
		try {
			show = new ImageIcon(ImageIO.read(getClass().getResourceAsStream("resources/toggleon.png")));
			collapse = new ImageIcon(ImageIO.read(getClass().getResourceAsStream("resources/toggleoff.png")));
		} catch (IllegalArgumentException | IOException e) {
			// Couldn't load forward icon
			iconLoaded = false;
		}
		toggleExtraPanel = new JButton(collapse);
		GridBagConstraints gb12 = new GridBagConstraints();
		gb12.gridx = 7;
		gb12.gridy = 1;
		gb12.gridwidth = 1;
		gb12.weightx = 0;
		gb12.weighty = 0;
		gb12.insets = new Insets(0,5,0,5);

		toggleExtraPanel.addActionListener(this);
		everythingElse.add(toggleExtraPanel,gb12);
		toggleExtraPanel.setToolTipText("Toggle extra panel");

		// add the choose file button to the panel
		chooseFilePanel.add(chooseFileToPlay, BorderLayout.CENTER);
		chooseFilePanel.setBorder( new EmptyBorder( 5, 5, 5, 5 ) );

		// add the buttons to the panel
		skipButtonsPanel.add(backButton,BorderLayout.WEST);
		skipButtonsPanel.add(snapShotButton,BorderLayout.CENTER);
		skipButtonsPanel.add(forwardButton,BorderLayout.EAST);
		skipButtonsPanel.setBorder( new EmptyBorder( 5, 5, 5, 5 ) );

		// add the two panel above to the other panel
		other.add(chooseFilePanel,BorderLayout.NORTH);
		other.add(skipButtonsPanel,BorderLayout.SOUTH);
		other.setBorder( new EmptyBorder( 0, 0, 10, 0 ) );

		// add area to history panel
		area.add(showHistoryTitle,BorderLayout.NORTH);
		area.add(history,BorderLayout.CENTER);
		area.setBorder( new EmptyBorder( 0,0, 0,0 ) );

		// add both the other and history panel to the extra panel
		extra.add(other,BorderLayout.NORTH);
		extra.add(area,BorderLayout.CENTER);

		add(extra,BorderLayout.EAST);
		add(everythingElse,BorderLayout.SOUTH);
		
		setupListeners();
		
	}
	
	// setup all the listeners to the buttons and any other components
	private void setupListeners() {

		playButton.addActionListener(this);
		muteButton.addActionListener(this);
		forwardButton.addActionListener(this);
		backButton.addActionListener(this);

		timeSlider.addChangeListener(this);
		volumeSlider.addChangeListener(this);

		// get the time and convert it to a hh:mm:ss format for avconv
		ActionListener updater = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				long milliSeconds = mediaPlayer.getTime();
				long hours = TimeUnit.MILLISECONDS.toHours(milliSeconds);
				long minutes = TimeUnit.MILLISECONDS.toMinutes(milliSeconds) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliSeconds));
				long seconds = TimeUnit.MILLISECONDS.toSeconds(milliSeconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliSeconds));

				String time = String.format("%02d:%02d:%02d", hours, minutes, seconds);
				timeLabel.setText(time);
				timeSlider.setValue((int)mediaPlayer.getTime());
				timeSlider.setToolTipText(time);
			}
		};
		
		Timer timer = new Timer(100, updater);
		timer.start();

	}
	
	@Override
	/**
	 * This method does the actions for the events generated for the buttons
	 */
	public void actionPerformed(ActionEvent e) {
		
		//For when toggle panel is clicked
		if(e.getSource() == toggleExtraPanel){
			togglePanelPressed();
		}
		
		// For when the play button is clicked
		if (e.getSource() == playButton) {
			playButtonPress();
		} 
		
		// For when the mute buttons is pressed
		if (e.getSource() == muteButton) {
			muteButtonPress();
		}
		// for when the forward button is clicked it goes forward 10 seconds
		if (e.getSource() == forwardButton) {
			
			if (mediaPlayer.getTime() + 10000 <= mediaPlayer.getLength()) { // Prevent skipping past end of file
				mediaPlayer.skip(10000);
			}
		}

		// for when the back button is clicked it goes back 10 seconds.
		if (e.getSource() == backButton) {
			mediaPlayer.skip(-10000);	
		}
		
		// for when the choose file button is pressed it ensures that the 
		// correct file is chosen
		if (e.getSource() == chooseFileToPlay) {
			//check if a file is already playing
			boolean a = (mediaPlayer.isPlaying())||(mediaPlayer.isPlayable());
				// only allow to choose MEDIA FILES ONLY!
					final JFileChooser fc = new JFileChooser();
					fc.setFileFilter(SwingFileFilterFactory.newMediaFileFilter());
					int returnVal = fc.showOpenDialog(VideoPlayer.this);
					if (returnVal == JFileChooser.APPROVE_OPTION) {

						/**
						 * This swingworker is gets the file from the file chooser in the background while the current
						 * file is still playing. If the file is chosen then it starts it in the background.
						 * This is reallt crucial as wihtout the swing worker the method would hang any time a file was not chosen.
						 * Now, everything works on the background thread and runs perfectly and does not hang the GUI.
						 */
						
					//  SwingWorker worker = new SwingWorker<Void,Void>(){
					//	@Override
					//	protected Void doInBackground() throws Exception {

						String newFile = fc.getSelectedFile().getAbsolutePath();
						// Check that file is a video or audio file.
						InvalidCheck i = new InvalidCheck();
						boolean isValidMedia = i.invalidCheck(newFile);
						// only allows the user to choose a valid file. If not, then a warning is shown!
						if (!isValidMedia) {
							JOptionPane.showMessageDialog(VideoPlayer.this, "You have specified an invalid file.", "Error", JOptionPane.ERROR_MESSAGE);
							return ;
						} else if (!newFile.equals(filePath)) {
							// set the path
							VideoPlayer.this.filePath = newFile;
							// before starting the video add it to the log
							LogFile.writeToLog(VideoPlayer.this.filePath.substring(VideoPlayer.this.filePath.lastIndexOf(File.separator)+1));
							VideoPlayer.this.hasPlayed = false;
							// start the media
							mediaPlayer.startMedia(filePath);
							ExtendedFrame.addSubtitles.setEnabled(true);
							
							InvalidCheckAudio ica = new InvalidCheckAudio();
							boolean isAudio = ica.invalidCheckAudio(newFile);
							if(!isAudio){
								ExtendedFrame.addSubtitles.setEnabled(true);
							}else{
								ExtendedFrame.addSubtitles.setEnabled(false);
							}
						}
				/**
				 * sets up the volume and time slider
				 */
		
					playButton.setIcon(pause);
					VideoPlayer.this.hasPlayed = true;
					while (mediaPlayer.getVolume() != volumeSlider.getValue() || mediaPlayer.getLength() != timeSlider.getMaximum()) {
						mediaPlayer.setVolume(volumeSlider.getValue());
						timeSlider.setMaximum((int)mediaPlayer.getLength());
					}
		}
		}
		
		// For when the stop video button is clicked. It sets the play rate
		// back to 1.
		
		if(e.getSource() == stopVideo){
			mediaPlayer.stop();
			mediaPlayer.setRate(1.0f);
			playButton.setIcon(play);
		}
		// when the snapshot button is clicked then it will take a shot of the
		// current image and saves it to the home folder
		if(e.getSource() == snapShotButton){
			snapShotPressed();
		}
		
		// When the fast forward is pressed it ensures that rewind button is not pressed and disables
		// it if its pressed. 
		if(e.getSource() == fastForwardButton){
			boolean isr = isRewinding;
			
			fastForwardPressed(isr);
		}
		
		// when the rewind is pressed it checks if the forward is pressed and disables it if its is pressed.
		// Otherwise, it keeps rewinding till disabled.
		if (e.getSource() == rewindBack) {
			
			boolean isf = isFastForwarding;
			
			rewindPressed(isf);
		}
		
		// The rest of the methods below all set the speed
		// of the file as per the users choice
		// they range from 0.5 to 3.0
		
		if(e.getSource() == slow){
			mediaPlayer.setRate(0.5f);
			ExtendedFrame.setRadioButton(0);
		}
		
		if(e.getSource() == normalPlay){
			mediaPlayer.setRate(1.0f);
			ExtendedFrame.setRadioButton(1);
		}
		
		if(e.getSource() == onepointfivePlay){
			mediaPlayer.setRate(1.5f);
			ExtendedFrame.setRadioButton(2);
		}
		
		if(e.getSource() == twoPlay){
			mediaPlayer.setRate(2.0f);
			ExtendedFrame.setRadioButton(3);
		}
		
		if(e.getSource() == twopointfivePlay){
			mediaPlayer.setRate(2.5f);
			ExtendedFrame.setRadioButton(4);
		}
		
		if(e.getSource() == threePlay){
			mediaPlayer.setRate(3.0f);
			ExtendedFrame.setRadioButton(5);
		}
	}
	
	/**
	 * When the fast foward button is pressed it fast forwards till it is disabled
	 * @param isr
	 */
	private void fastForwardPressed(boolean isr){
		
		if(isr){
			worker.cancel(true);
			rewindBack.setBackground(null);
			isRewinding = false;
		}
		
		if (isFastForwarding) {
			mediaPlayer.setRate(1.0f);
			fastForwardButton.setBackground(null);
			isFastForwarding = false;
		} else {
			mediaPlayer.setRate(3.0f);
			fastForwardButton.setBackground(Color.darkGray);
			isFastForwarding = true;
		}
		
	}
	
	/**
	 * Rewind buttons rewinds till it is disabled. It also ensures that the fast forward is not enabled at
	 * the same time. Also starts the rewind worker when needed.
	 * @param isf
	 */
	private void rewindPressed(boolean isf){
		
		if(isf){
			mediaPlayer.setRate(1.0f);
			fastForwardButton.setBackground(null);
			isFastForwarding = false;
		}
		// start the worker
		if (isRewinding) {
			worker.cancel(true);
			rewindBack.setBackground(null);
			isRewinding = false;
		} else {
			
			worker = new rewindWorker();
			rewindBack.setBackground(Color.darkGray);
			worker.execute();
			isRewinding = true;
		}
		
		
	}

	/**
	 * This has the history, snapshot, skip forward/backwards features.
	 * It allows the user to toggle the panel.
	 */
	private void togglePanelPressed() {
		boolean visible = extra.isVisible();
		boolean change = true;
		if(change){
			if(!visible){

				change = false;
				toggleExtraPanel.setIcon(collapse);
				extra.setVisible(true);
			}
		}
		if(change){
			if(visible){
				change = false;
				toggleExtraPanel.setIcon(show);
				extra.setVisible(false);
			}
		}
	}
	
	/**
	 * This method gets the image on the current frame and
	 * stores it in the users home drive. 
	 * output : A PNG file
	 */
	private void snapShotPressed(){
		BufferedImage image = mediaPlayer.getSnapshot();
		boolean ifNull = (image == null);

		if(!ifNull){
			File outputImage = new File(System.getProperty("user.home") + File.separator +  +mediaPlayer.getTime() + ".png");
			try {
				ImageIO.write(image, "png", outputImage);
				JOptionPane.showMessageDialog(VideoPlayer.this,"The snapshot was saved to your home folder with name " + outputImage.getName());
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				JOptionPane.showMessageDialog(VideoPlayer.this,"Sorry, snapshot failed!");
				e1.printStackTrace();
			}
		}
	}

	@Override
	/**
	 * This method checks for the time and volume slider and updates them
	 * as the user manipulates them
	 */
	public void stateChanged(ChangeEvent e) {
		
		// updates the volume slider and the current volume
		
		if (e.getSource() == volumeSlider) {
			if (e.getSource() instanceof JSlider) {
				JSlider slider = (JSlider)e.getSource();
				if (slider.getValueIsAdjusting()) {
					if (mediaPlayer.isPlayable()) {
						// Checks the volume and changes it to mute icon if 0 or 1.
						if ((mediaPlayer.getVolume() <= 1)) {
							muteButton.setIcon(mute);
						} else {
							muteButton.setIcon(unmuted);
							mediaPlayer.mute(false);
						}
					}
					mediaPlayer.setVolume(slider.getValue());
					volumeSlider.setToolTipText("" + volumeSlider.getValue());
				}
			}
		}

		// changes the time as per the time slider
		
		if (e.getSource() == timeSlider) {
			JSlider slider = (JSlider)e.getSource();

			// If the slider has reached the end then make it go back to start and then make the icon  switch to play
			if(timeSlider.getValue() == mediaPlayer.getLength()){
				playButton.setIcon(play);
				timeSlider.setValue(0);
				mediaPlayer.stop();
				
				if(ExtendedFrame.getReplay()){
					//mediaPlayer.start();
					playButton.doClick();
				}
			
			}

			if (slider.getValueIsAdjusting()) {
				mediaPlayer.setTime(slider.getValue());
			}
		}
	}
	
	/**
	 * This class is for the rewind worker. VLCJ does not have a rewind feature.
	 * So, this skips back and imitates a rewind. This is done in 
	 * the background to ensure that the GUI runs fast.
	 * @author anmol
	 *
	 */
	private class rewindWorker extends SwingWorker<Void, Void>{

		@Override
		protected Void doInBackground() throws Exception {
			while (true) {
				if (isCancelled()) {
					break;
				}
				Thread.sleep(100);
				process(null);
			}
			return null;
		}
		
		@Override
		protected void done() {
			// TODO Auto-generated method stub
			super.done();
		}

		@Override
		protected void process(List<Void> chunks) {
			mediaPlayer.skip(-1000);
		}

	}


	@Override
	/**
	 * This is a mouse listener. When the right mouse is clicked
	 * it will allow the user to set the speed of the media file. If the left mouse button is clicked twice
	 * then the extraction of the media file playing starts
	 * 
	 * OUTPUT FROM DOUBLE LEFT CLICK : mp4
	 */
	public void mouseClicked(MouseEvent arg0) {


		if((mediaPlayer.isPlaying())&&(arg0.getModifiers() == MouseEvent.BUTTON3_MASK)){
			popup.show(arg0.getComponent(), arg0.getX(), arg0.getY());
			
		}else if((mediaPlayer.isPlaying())&&(arg0.getModifiers() == MouseEvent.BUTTON1_MASK)){
			clicked++;
			
			// start the recording
			if(clicked == 2){
				start = mediaPlayer.getTime();
				startTime = convertTime(mediaPlayer.getTime());

				JOptionPane.showMessageDialog(VideoPlayer.this,"Started video recording");
				
				// end the recording
			}else if(clicked == 4){
				end = mediaPlayer.getTime();
				length = end-start;
				lengthTime = convertTime(length);
				
				// warns the users that the file will have problems if the time is less than 8 seconds
				if(length<8000){
					JOptionPane.showMessageDialog(VideoPlayer.this, "WARNING! Extraction less than 8 seconds may give orthodox results!");
				}
				
				// reset the variables to allow the starting of the recording
				vx = new VideoExtracter(startTime,lengthTime);
				vx.execute();
				startTime = "";
				lengthTime = "";
				start = 0;
				end = 0;
				length = 0;

				JOptionPane.showMessageDialog(VideoPlayer.this,"Ended Recording");
				clicked = 0;
			}
		}
	}

	/**
	 * This method is used to convert the time from the media player into the HH:MM:SS format
	 * This is useful as avconv only accepts the hh:mm:ss format.
	 * 
	 * @param milliSeconds
	 * @return
	 */
	private String convertTime(long milliSeconds) {
		//long milliSeconds = mediaPlayer.getTime();
		long hours = TimeUnit.MILLISECONDS.toHours(milliSeconds);
		long minutes = TimeUnit.MILLISECONDS.toMinutes(milliSeconds) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliSeconds));
		long seconds = TimeUnit.MILLISECONDS.toSeconds(milliSeconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliSeconds));

		String time = String.format("%02d:%02d:%02d", hours, minutes, seconds);
		return time;
	}
	@Override
	public void mouseEntered(MouseEvent arg0) {
	}
	@Override
	public void mouseExited(MouseEvent arg0) {
	}
	@Override
	public void mousePressed(MouseEvent ev) {
	}
	@Override
	public void mouseReleased(MouseEvent ev) {
	}
	
	public static void setCurrentRate(float i){
		mediaPlayer.setRate(i);
	}
	
	/**
	 * This method is used by Library.java to play the list
	 * This just simulates the play button click.
	 */
	public static void startPlaying() {
		// stop the media player to ensure that the previous file is removed
		mediaPlayer.stop();
		playButton.doClick();
		AddToFile a = new AddToFile();
		a.add();
	}
	
	/**
	 * This method is for when the play button is pressed.
	 * It just sets the rate to 1.0 and also pauses if 
	 * the media player is currently playing
	 */
	private void playButtonPress(){
		
		// just change the icons depending on what is the current state.
		if (isRewinding) {
			worker.cancel(true);
			isRewinding = false;
			rewindBack.setBackground(null);
		}
		if (isFastForwarding) {
			mediaPlayer.setRate(1.0f);
			fastForwardButton.setBackground(null);
			isFastForwarding = false;
		}
		if (mediaPlayer.isPlaying()) {

			if (loadedPlayIcon) {
				playButton.setIcon(play);
			} else {
				playButton.setText("Play");
			}
		} else if (filePath != null) {
			if (loadedPauseIcon) {
				playButton.setIcon(pause);
			} else {
				playButton.setText("Pause");
			}
		}
		
		// pause the media player if it is currently playing
		mediaPlayer.pause();
		
		// if the media player is not playable then start the media file.
		if (!mediaPlayer.isPlayable()) {
			if (filePath != null) {
				mediaPlayer.startMedia(filePath);
				this.hasPlayed = true;
				// Continually try to set correct volume on player, and get total length.
				// This is necessary as, it takes a while for the audio output to be created
				// and the volume won't get set until then
				while (mediaPlayer.getVolume() != volumeSlider.getValue() || mediaPlayer.getLength() != timeSlider.getMaximum()) {
					if (volumeSlider.getValue() > 1) {
						mediaPlayer.mute(false);
					}
					mediaPlayer.setVolume(volumeSlider.getValue());
					timeSlider.setMaximum((int)mediaPlayer.getLength());
				}
			}
			// if the file has not been played then start playing the loaded media.
		} else if (!this.hasPlayed) {
		
			mediaPlayer.startMedia(filePath);
			this.hasPlayed = true;
			// check if the file path is the same, if it is then don't add it to the history
			// if file path is not the same then add it to the file path
			while (mediaPlayer.getVolume() != volumeSlider.getValue() || mediaPlayer.getLength() != timeSlider.getMaximum()) {
				mediaPlayer.setVolume(volumeSlider.getValue());
				timeSlider.setMaximum((int)mediaPlayer.getLength());
			}
		}
	}
	
	/**
	 * This method is for when the mute button is pressed.
	 * It ensures that when the mute button is pressed the current volume is saved and when unmuted it
	 * will go back to the saved volume. 
	 */
	private void muteButtonPress(){
		if (volumeSlider.getValue() <= 1) {
			// The volume is mute so now the user presses unmute so change the icon.
			// Restore the volume of the volume slider to one before mute
			if (loadedUnmuteIcon) {
				muteButton.setIcon(unmuted);
			} else {
				muteButton.setText("Unmuted");
			}
			volumeSlider.setValue(volumeBeforeMuted);
			mediaPlayer.setVolume(volumeBeforeMuted);
		} else {
			volumeBeforeMuted = volumeSlider.getValue();
			if (loadedMuteIcon) {
				muteButton.setIcon(mute);
			} else {
				muteButton.setText("Mute");
			}
			volumeSlider.setValue(0);
		}
		mediaPlayer.mute();
		volumeSlider.setToolTipText("" + volumeSlider.getValue());
	}


}
