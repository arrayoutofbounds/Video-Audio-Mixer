package textHandling;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;


@SuppressWarnings("serial")
public class JTextAreaLimitedSize extends PlainDocument {
	
	private int characterLimit;
	
	public JTextAreaLimitedSize(int limit) {
		characterLimit = limit;
	}
	
	@Override
	public void insertString(int offset, String str, AttributeSet a) {
		if (str != null) {
			if ((str.length() + this.getLength() <= characterLimit)) {

				try {
					super.insertString(offset, str, a);
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
