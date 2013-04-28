package edu.mccc.cos210.qrks;
import java.security.*;
import java.util.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;
import javax.swing.filechooser.*;

import java.security.KeyStore;
import java.security.KeyStore.PrivateKeyEntry;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;

import edu.mccc.cos210.qrks.qrcode.*;

public class QRSecureBuilderPanel extends QRBuilderPanel {
	private static final long serialVersionUID = 1L;
	private PrivateKey privateKey = null;
	private String myAlias = null;
	//TODO: figure out what the type should be for key!
	private Object key;
	public Object getKey() {
		return key;
	}
	
	@Override
	public Generator<Item<BufferedImage>> getGenerator() {
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
		
		
		ActionListener sal = new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				JFileChooser fc = new JFileChooser();
			//	fc.setAcceptAllFileFilterUsed(false);
			//	fc.addChoosableFileFilter(new KSFileFilter());
				
				int returnVal = fc.showDialog(QRSecureBuilderPanel.this, "Load Key");
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					boolean tryAgain = false;
					JPasswordField jpf = new JPasswordField();
					int r = JOptionPane.showConfirmDialog(null, jpf, "Password:", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
					if (r == JOptionPane.OK_OPTION) {
						
						char[] pw = jpf.getPassword();
						KeyStore ks = null;
						try {
							ks = KeyStore.getInstance("JCEKS");	//TODO: how do i know what type the ks is???
						} catch (KeyStoreException kse) {
						}
			
						try (FileInputStream fis = new FileInputStream(file.getPath())) {
							ks.load(fis, pw);
						} catch (IOException ex){
							tryAgain = true;
						} catch (Exception ex){
							
						}
						while (tryAgain) {
							tryAgain = false;
							r = JOptionPane.showConfirmDialog(null, jpf, "Bad Password; Try again!", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
							if (r == JOptionPane.OK_OPTION) {
								pw = jpf.getPassword();
						                  
								try (FileInputStream fis = new FileInputStream(file.getPath())) {
									ks.load(fis, pw);
								} catch (IOException ex){
									tryAgain = true;
								} catch (Exception ex){
									
								}
							}
						}
						if (r == JOptionPane.OK_OPTION) {
							java.util.List<String> l = null;
							try {
								l = Collections.list(ks.aliases());
							} catch (KeyStoreException e2) {
								return;
							}
							java.util.List<String> pkl = new LinkedList<String>();
							KeyStore.ProtectionParameter kspp = new KeyStore.PasswordProtection(pw); 
							
							for (int i = 0; i < l.size(); i++) {
								KeyStore.PrivateKeyEntry pke;
								try {
									pke = (PrivateKeyEntry) ks.getEntry(l.get(i), kspp);
								} catch (UnsupportedOperationException haha) {
									continue;
								} catch (NoSuchAlgorithmException
										| UnrecoverableEntryException
										| KeyStoreException e9) {
									return;
								}		 
								PrivateKey pk = pke.getPrivateKey();
								if (pk != null) {
									pkl.add(l.get(i));
								}
							}
							
							Collections.sort(pkl);
							
							JPanel bp = new JPanel(new GridLayout(0, 1));
							ButtonGroup bg = new ButtonGroup();
							for (int i = 0; i < pkl.size(); i++) {
								JRadioButton t = new JRadioButton(pkl.get(i));
								bg.add(t);
								bp.add(t);
								t.setActionCommand(pkl.get(i));
							}
		
							int m = JOptionPane.showConfirmDialog(null, bp, "Select Key Alias", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
							//JOptionPane.showConfirmDialog(null, new JComboBox<String>(al), "Select Key Alias", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
							if ( m == JOptionPane.OK_OPTION) {
								myAlias = bg.getSelection().getActionCommand();
								try {
									
									KeyStore.PrivateKeyEntry pke = (PrivateKeyEntry) ks.getEntry(myAlias, kspp);		 
									privateKey = pke.getPrivateKey();
								} catch (KeyStoreException kse) {
									return;
								} catch (NoSuchAlgorithmException e1) {
									return;
								} catch (UnrecoverableEntryException e1) {
									return;
								}
								keyName.setText(file.getName() + " " + myAlias);
							}
						}
					}
				} 
				//Reset the file chooser for the next time it's shown.
			//	fc.setSelectedFile(null);
			}
		};
		
		selectKey.addActionListener(sal);
		
		
		
		JPanel skp = new JPanel();
		skp.setToolTipText("Secure Key:");
		skp.setBorder(BorderFactory.createTitledBorder("Secure Key:"));
		skp.add(selectKey);
		skp.add(keyName);		
		
		add(skp);
	}
}