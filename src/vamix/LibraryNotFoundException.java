package vamix;

@SuppressWarnings("serial")
/**
 * This class is to check for existence of VLC on the owners computer. It is called
 * in the MainFrame.java which is the main class.
 * @author anmol
 *
 */
public class LibraryNotFoundException extends Exception {
	
	public LibraryNotFoundException() {}

    //Constructor that accepts a message
    public LibraryNotFoundException(String message)
    {
       super(message);
    }

}
