package vamix;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.discovery.NativeDiscovery;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;
import uk.co.caprica.vlcj.version.LibVlcVersion;

import com.sun.jna.Native;

/**
 * This is the main class. It runs the whole program. It gets
 * the vlcj libraries and also sets the size of the frame. An exception is thrown if
 * the user does not have a vlc player installed.
 * @author anmol
 *
 */
public class MainFrame {

	public static void main(String[] args) {
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				
				// add the seaglass look and feel
				 try {
					UIManager.setLookAndFeel("com.seaglasslookandfeel.SeaGlassLookAndFeel");
				} catch (ClassNotFoundException e1) {
					e1.printStackTrace();
				} catch (InstantiationException e1) {
					e1.printStackTrace();
				} catch (IllegalAccessException e1) {
					e1.printStackTrace();
				} catch (UnsupportedLookAndFeelException e1) {
					e1.printStackTrace();
				}

				try {
					setupLibVLC();
				} catch (LibraryNotFoundException e) {
					e.printStackTrace();
				}
				
				Native.loadLibrary(RuntimeUtil.getLibVlcLibraryName(), LibVlc.class);
				
				ExtendedFrame frame = new ExtendedFrame();
				frame.setResizable(true);
				frame.setSize(800, 600);
				frame.setMinimumSize(new Dimension(500, 400));
				Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
				frame.setLocationRelativeTo(null);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setVisible(true);
				
				JOptionPane.showMessageDialog(null, "Welcome to Vamix!\n\nHope you enjoy using vamix!\nFor help, please click in the Help menu.\nRefer to user manual for detailed help.\n");
			}
		});
	}
	
	/**
	 * Method that throws exception if the libvlc is not found.
	 * @throws LibraryNotFoundException
	 */
	private static void setupLibVLC() throws LibraryNotFoundException {
	    new NativeDiscovery().discover();
	    // discovery()'s method return value is WRONG on Linux
	    try {
	        LibVlcVersion.getVersion();
	    } catch (Exception e) {
	        throw new LibraryNotFoundException();
	    }
	}
	
}
