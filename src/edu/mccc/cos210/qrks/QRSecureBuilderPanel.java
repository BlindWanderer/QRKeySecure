package edu.mccc.cos210.qrks;
import java.util.*;
import javax.swing.*;
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
					String text = pps.getText();
					if (text == null || text == "") {
						pps.setText("1");
					} else {
						long value = Long.valueOf(text);
						if (value < 1) {
							pps.setText("1");
						} else {
							if (512 < value) {
								pps.setText("512");
							}
						}
					}
				}
			});
		JPanel ppsp = new JPanel();//new BoxLayout(pane, BoxLayout.Y_AXIS)
		ppsp.setToolTipText("Pixels Per Square:");
        ppsp.setBorder(BorderFactory.createTitledBorder("Pixels Per Square:"));
        ppsp.add(pps);
		
		add(tp);
		add(ecp);
		add(ppsp);
	}
}