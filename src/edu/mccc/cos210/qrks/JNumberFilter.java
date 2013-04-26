package edu.mccc.cos210.qrks;
import javax.swing.*;
import javax.swing.text.*;
/**
 * JNumberFilter: A simple number filter for text fields that ensures only positive numbers go in.
 */
public class JNumberFilter extends PlainDocument {
	private static final long serialVersionUID = 1L;
	public JNumberFilter() {
		super();
	}
	public void insertString(final int offset, final String str, final AttributeSet attr) throws BadLocationException {
		if (str == null) {
			return;
		}
		for (int i = 0; i < str.length(); i++) {
			if (!Character.isDigit(str.charAt(i))) {
				return;
			}
		}
		super.insertString(offset, str, attr);
	}
}