package edu.mccc.cos210.qrks;
import javax.swing.*;
import javax.swing.text.*;

class JNumberFilter extends PlainDocument {
	private static final long serialVersionUID = 1L;
	public JNumberFilter() {
		super();
	}
	public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
		if (str == null) {
			return;
		}
		for (int i=0; i < str.length(); i++ ) {
			if (!Character.isDigit(str.charAt(i))){
				return;
			}
		}
		super.insertString(offset, str, attr);
    }
}