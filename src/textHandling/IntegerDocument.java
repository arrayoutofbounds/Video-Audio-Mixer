package textHandling;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

@SuppressWarnings("serial")
class IntegerDocument extends PlainDocument {
	
	public void insertString(int offset, String str, AttributeSet a) {
		try {
			Integer.parseInt(str);
			super.insertString(offset, str, a);
		} catch (NumberFormatException | BadLocationException e) {
			// Invalid input
		}
	}
	
}