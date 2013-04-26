package edu.mccc.cos210.qrks;
import java.util.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import edu.mccc.cos210.qrks.qrcode.*;

public class QRSecureBuilderPanel extends QRBuilderPanel {
	private static final long serialVersionUID = 1L;
	
	//TODO: figure out what the type should be fore key!
	private Object key;
	public Object getKey() {
		return key;
	}
	
	@Override
	public Generator<BufferedImage> getGenerator() {
		String text = getText();
		//text = key.sign(text);
		return ((QRBuilder) getBuilder()).new QRGenerator(text, getErrorCorrectionLevel(), getPixelsPerUnit());
	}

	public QRSecureBuilderPanel(final QRSecureBuilder builder) {
		super(builder);

		final JTextField keyName = new JTextField("<No Key Selected>", 18);
		keyName.setEditable(false);
		final JButton selectKey = new JButton("Select Key");
		selectKey.setMnemonic(KeyEvent.VK_K);
		//TODO: Don't forget to update keyName with the new info.
		
		JPanel skp = new JPanel();
		skp.setToolTipText("Secure Key:");
		skp.setBorder(BorderFactory.createTitledBorder("Secure Key:"));
		skp.add(selectKey);
		skp.add(keyName);		
		
		add(skp);
	}
}