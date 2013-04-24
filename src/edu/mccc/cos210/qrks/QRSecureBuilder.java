package edu.mccc.cos210.qrks;
import java.util.*;
import javax.swing.*;
import java.awt.*;

public class QRSecureBuilder extends QRBuilder {
	private class QRSecureBuilderPanel extends JPanel {
		public QRSecureBuilderPanel() {
			//setLayout(new BorderLayout());
			JTextArea input;
			
		}
	}
	@Override;
	public JPanel generateGUI() {
		return new QRSecureBuilderPanel();
	}
	@Override;
	public String getName() {
		return "Signed QRCode";
	}
	@Override;
	public void reset() {
	}
}