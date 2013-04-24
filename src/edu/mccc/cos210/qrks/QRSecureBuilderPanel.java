package edu.mccc.cos210.qrks;
import java.util.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import edu.mccc.cos210.qrks.qrcode.*;

class QRSecureBuilderPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private QRSecureBuilder builder;
	public QRSecureBuilderPanel(QRSecureBuilder builder) {
		this.builder = builder;
		//setLayout(new BorderLayout());
		
		JTextArea input = new JTextArea(5,50);
		JPanel tp = new JPanel();//new BoxLayout(pane, BoxLayout.Y_AXIS)
		tp.setBorder(BorderFactory.createTitledBorder("QRCode Text:"));
		tp.add(input);
		
		JComboBox<ErrorCorrectionLevel> ec = new JComboBox<ErrorCorrectionLevel>(ErrorCorrectionLevel.values());
		JPanel ecp = new JPanel();//new BoxLayout(pane, BoxLayout.Y_AXIS)
		ecp.setToolTipText("Error Correction Level:");
		ecp.setBorder(BorderFactory.createTitledBorder("Error Correction Level:"));
		ecp.add(ec);
		
		final JTextField pps = new JTextField(new JNumberFilter(), "1", 10);
		pps.addFocusListener(new FocusAdapter(){
				public void focusLost(FocusEvent e){
					JTextComponent c = (JTextComponent)e.getComponent();
					String text = c.getText();
					if (text == null || text == "") {
						c.setText("1");
					} else {
						long value;
						try {
							value = Long.valueOf(text);
						} catch (NumberFormatException nfe) {
							value = Long.MIN_VALUE;
						}
						if (value < 1) {
							c.setText("1");
						} else {
							if (512 < value) {
								c.setText("512");
							}
						}
					}
				}
			});
		JPanel ppsp = new JPanel();//new BoxLayout(pane, BoxLayout.Y_AXIS)
		ppsp.setToolTipText("Pixels Per Square:");
		ppsp.setBorder(BorderFactory.createTitledBorder("Pixels Per Square:"));
		ppsp.add(pps);
		
		final JTextArea info = new JTextArea("Version: \nDimensions: \nNumber of Characters: 0");
		info.setEditable(false);
		//Font f = new Font(info.getFont());
		info.setOpaque(false);
		JPanel ip = new JPanel();//new BoxLayout(pane, BoxLayout.Y_AXIS)
		ip.setToolTipText("Information:");
		ip.setBorder(BorderFactory.createTitledBorder("Information:"));
		ip.add(info);
		
		input.getDocument().addDocumentListener(new DocumentListener() {
			public void insertUpdate(DocumentEvent e) {
				update(e);
			}
			public void removeUpdate(DocumentEvent e) {
				update(e);
			}
			public void changedUpdate(DocumentEvent e) {
				update(e);//Plain text components do not fire these events
			}
			public void update(DocumentEvent e) {
				Document doc = e.getDocument();
				//TODO: calculate other values, this is trivial but implementation specific
				info.setText("Version: \nDimensions: \nNumber of Characters: " + doc.getLength());
			}
		});
		
		add(tp);
		add(ecp);
		add(ppsp);
		add(ip);
	}
}