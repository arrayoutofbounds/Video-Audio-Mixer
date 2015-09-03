package vamix;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTabbedPane;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import textHandling.EditTextFrame;
import tools.DownloadFrame;
import tools.HelpFrame;
import videoFeatures.ExtractPart;
import videoFeatures.Gif;
import videoFeatures.Images;
import videoFeatures.Subtitles;
import videoFeatures.VideoFilter;
import audioFeatures.AudioFilter;
import audioFeatures.ExtractFrame;
import audioFeatures.ReplaceAudio;
import mediacomponent.LogFile;
import mediacomponent.VideoPlayer;


@SuppressWarnings("serial")
/**
 * This class is the main frame. It is the oen that contains all the components.
 * It has been extended from the jframe and instantiated in the the MainFrame.java.
 * This has the menu bar and allows the opening of all the features and their frames.
 * @author anmol
 *
 */
public class ExtendedFrame extends JFrame implements ActionListener, MenuListener {
	
	static JTabbedPane tabsPane;
	//private Download downloadTab;
	private Play playTab;
	private ExtractFrame extractFrame;
	private EditTextFrame editTextFrame;
	private JMenuBar menuBar;
    private JMenu audioFeatures;
    private JMenuItem extractMenuItem;
    private JMenuItem editTextMenuItem;
    private JMenuItem replaceAudio;
    private ReplaceAudio replaceAudioFrame;
    private JMenuItem makeGif;
    private JMenuItem extractImagesFromVideo;
    private JMenuItem delete;
    private JMenu help;
    private JMenuItem hide;
    
    private HelpFrame helpFrame;;
    private JMenu videoFeatures;
    private JMenu other;
    private JMenuItem download;
    private Gif gifFrame;
	private Images i;
	
	private DownloadFrame downloadFrame;

	private JMenu speed;
	// made static because it is accessed from the VideoPlayer.java where the playback speed is set
	// i.e there are 2 ways to set the playback speed
	private static JRadioButtonMenuItem playingSpeed[];
	private ButtonGroup group;
	private JMenuItem videoFilters;
	private VideoFilter v;
	private static JCheckBoxMenuItem replay;
	private JMenuItem audioFilters;
	private AudioFilter af;
	private JMenuItem extractAPart;
	private ExtractPart ep;
	private Library library;
	
	private JMenuItem makeSubtitles;
	
	private Subtitles subframe;
	private File selectedSrt;
	
	public static JMenuItem addSubtitles;

	public ExtendedFrame() {
		super("Vamix");
		
		// create all the menu and menu items and other components
		
		tabsPane = new JTabbedPane();
		//downloadTab = new Download();
		playTab  = new Play();
		library = new Library();

	    menuBar = new JMenuBar();
        add(menuBar);

        audioFeatures = new JMenu("Audio Features");
        audioFeatures.setMnemonic('a');
        menuBar.add(audioFeatures);
        
        speed = new JMenu("Playback speed");
        audioFeatures.add(speed);
        
        videoFeatures = new JMenu("Video Features");
        videoFeatures.setMnemonic('v');
        menuBar.add(videoFeatures);
        
        other = new JMenu("Tools");
        other.setMnemonic('t');
        menuBar.add(other);
        
        help = new JMenu("Help");
        help.setMnemonic('h');
        menuBar.add(help);
        help.addMenuListener(this);
        
        download = new JMenuItem("Download audio/video");
        download.setMnemonic('o');
        other.add(download);
        download.addActionListener(this);
        
        extractAPart  = new JMenuItem("Extract part of a video");
        other.add(extractAPart);
        extractAPart.addActionListener(this);


        extractMenuItem = new JMenuItem("Extract Audio");
        extractMenuItem.setMnemonic('e');
        audioFeatures.add(extractMenuItem);
        
        editTextMenuItem = new JMenuItem("Add text to video");
        editTextMenuItem.setMnemonic('t');
        videoFeatures.add(editTextMenuItem);
        
        replaceAudio = new JMenuItem("Replace/Overlay Audio of a Video");
        replaceAudio.setMnemonic('r');
        audioFeatures.add(replaceAudio);
        
        makeGif = new JMenuItem("Make a GIF Image");
        makeGif.setMnemonic('g');
        videoFeatures.add(makeGif);
        
        extractImagesFromVideo = new JMenuItem("Extract Images from video");
        extractImagesFromVideo.setMnemonic('i');
        videoFeatures.add(extractImagesFromVideo);
        
        videoFilters = new JMenuItem("Add Video Filters");
        videoFilters.setMnemonic('f');
        videoFeatures.add(videoFilters);
        
        delete = new JMenuItem("Delete history");
        delete.setMnemonic('d');
        other.add(delete);
        
        hide = new JMenuItem("Hide/Load History");
        other.add(hide);
        
        audioFilters = new JMenuItem("Add Audio Filters");
        audioFeatures.add(audioFilters);
        
        replay = new JCheckBoxMenuItem("Replay");
        replay.setMnemonic('r');
        audioFeatures.add(replay);
        
        makeSubtitles = new JMenuItem("Make subtitles");
        videoFeatures.add(makeSubtitles);
        
        addSubtitles = new JMenuItem("Add existing subtitles");
        videoFeatures.add(addSubtitles);
        addSubtitles.setEnabled(false);
        //only enable adding of subtitles if there is a video playing
        
       


        setJMenuBar(menuBar);
        
        // add listeners to all the menu items
        extractMenuItem.addActionListener(this);
        editTextMenuItem.addActionListener(this);
        replaceAudio.addActionListener(this);
        makeGif.addActionListener(this);
		extractImagesFromVideo.addActionListener(this);
		delete.addActionListener(this);
		hide.addActionListener(this);
		videoFilters.addActionListener(this);
		audioFilters.addActionListener(this);
		makeSubtitles.addActionListener(this);
		addSubtitles.addActionListener(this);

        //tabsPane.add("Download",downloadTab);
        tabsPane.add("Play",playTab);
        tabsPane.add("Library",library);
        tabsPane.setSelectedComponent(playTab);
		add(tabsPane);
		// Create single extract frame, so that the user can close it, and reopen it
		// and maintain extraction progress
		extractFrame = new ExtractFrame();
		extractFrame.setResizable(false);
		extractFrame.setSize(600, 400);
		extractFrame.setLocationRelativeTo(null);
		extractFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		// Create single edit text frame
		editTextFrame = new EditTextFrame();
		editTextFrame.setResizable(false);
		editTextFrame.setSize(750, 700);
		editTextFrame.setLocationRelativeTo(null);
		editTextFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		// create the replace/overlay audio frame
		replaceAudioFrame = new ReplaceAudio();
		replaceAudioFrame.setResizable(false);
		replaceAudioFrame.setSize(700, 600);
		replaceAudioFrame.setLocationRelativeTo(null);
		replaceAudioFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		// create the make gif frame
		gifFrame = new Gif();
		gifFrame.setResizable(false);
		gifFrame.setSize(700, 500);
		gifFrame.setLocationRelativeTo(null);
		gifFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		// creat the get images frame
		i = new Images();
		i.setResizable(false);
		i.setSize(500, 400);
		i.setLocationRelativeTo(null);
		i.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		// create the help frame
		helpFrame = new HelpFrame();
		helpFrame.setResizable(false);
		helpFrame.setSize(1000, 700);
		helpFrame.setLocationRelativeTo(null);
		helpFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		// create the download frame.
		downloadFrame = new DownloadFrame();
		downloadFrame.setResizable(false);
		downloadFrame.setSize(700, 500);
		downloadFrame.setLocationRelativeTo(null);
		downloadFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		// create the video filter frame
		v = new VideoFilter();
		v.setResizable(false);
		v.setSize(700, 500);
		v.setLocationRelativeTo(null);
		v.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		// create the aduio filter frame
		af = new AudioFilter();
		af.setResizable(false);
		af.setSize(700, 500);
		af.setLocationRelativeTo(null);
		af.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		// create the extract part of a video frame
		ep = new ExtractPart();
		ep.setResizable(false);
		ep.setSize(700, 500);
		ep.setLocationRelativeTo(null);
		ep.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		subframe = new Subtitles();
		subframe.setResizable(false);
		subframe.setSize(700, 500);
		subframe.setLocationRelativeTo(null);
		subframe.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		// initialise all the speed radio buttons and set the 1x speed to the inital speed
		String speeds[] = {"0.5x","1.0x","1.5x","2.0x","2.5x","3.0x"};
		playingSpeed = new JRadioButtonMenuItem[speeds.length];
		group = new ButtonGroup();

		for(int count = 0;count<playingSpeed.length;count++){
			playingSpeed[count] = new JRadioButtonMenuItem(speeds[count]);
			speed.add(playingSpeed[count]);
			group.add(playingSpeed[count]);
			playingSpeed[count].addActionListener(this);
		}
		playingSpeed[1].setSelected(true);
	}
	@Override
	/**
	 * This method is for all the options that the user can press
	 * from the menu and its items
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == extractMenuItem) {
			extractFrame.setVisible(true);
		} else if (e.getSource() == editTextMenuItem) {
			editTextFrame.setVisible(true);
		}else if(e.getSource() == replaceAudio){
			replaceAudioFrame.setVisible(true);
		}else if(e.getSource() == makeGif){
			gifFrame.setVisible(true);
		}else if(e.getSource() == extractImagesFromVideo){
			i.setVisible(true);
		}else if(e.getSource() == delete){
			LogFile.delete();
		}else if(e.getSource() == hide){
			LogFile.hide();
		}else if(e.getSource() == download){
			//get the download frame and do the download.
			downloadFrame.setVisible(true);
		}else if(e.getSource() == playingSpeed[0]){
			VideoPlayer.setCurrentRate((float) 0.5);
		}else if(e.getSource() == playingSpeed[1]){
			VideoPlayer.setCurrentRate(1);
		}else if(e.getSource() == playingSpeed[2]){
			VideoPlayer.setCurrentRate((float) 1.5);
		}else if(e.getSource() == playingSpeed[3]){
			VideoPlayer.setCurrentRate(2);
		}else if(e.getSource() == playingSpeed[4]){
			VideoPlayer.setCurrentRate((float) 2.5);
		}else if(e.getSource() == playingSpeed[5]){
			VideoPlayer.setCurrentRate(3);
		}else if(e.getSource() == videoFilters){
			v.setVisible(true);
		}else if(e.getSource() == audioFilters){
			af.setVisible(true);
		}else if(e.getSource() == extractAPart){
			ep.setVisible(true);
		}else if(e.getSource() == makeSubtitles){
			subframe.setVisible(true);
		}else if(e.getSource() == addSubtitles){
			addSubtitles();
		}
	}
	private void addSubtitles() {
		// open a jfilechooser and then allow the user to select a srt file and then set that file enabled
		// to the current video.
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(new java.io.File("."));
		fileChooser.setDialogTitle("Choose Video File");
		FileNameExtensionFilter filter = new FileNameExtensionFilter("SRT FILES", "srt");
		fileChooser.setFileFilter(filter);
		fileChooser.setAcceptAllFileFilterUsed(false);
		int returnValue = fileChooser.showOpenDialog(ExtendedFrame.this);
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			selectedSrt = fileChooser.getSelectedFile();
			VideoPlayer.mediaPlayer.setSubTitleFile(selectedSrt);
		}
	}
	@Override
	public void menuCanceled(MenuEvent arg0) {
	}
	@Override
	public void menuDeselected(MenuEvent arg0) {
	}
	@Override
	public void menuSelected(MenuEvent arg0) {
		helpFrame.setVisible(true);
		helpFrame.appendReadmeFile();
	}
	
	/**
	 * This method sets the radio button in the menu bar. It is visible 
	 * to VideoPlayer.java as well because the playback speed can be set from there as well.
	 * @param j
	 */
	public static void setRadioButton(int j) {
		playingSpeed[j].setSelected(true);
	}
	/**
	 * This method returns the value of the replay button. It is public as it 
	 * is used in VideoPlayer.java class when the media file finished playing.
	 * @return
	 */
	public static boolean getReplay() {
		return replay.isSelected();
	}

}
