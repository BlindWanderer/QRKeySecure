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
import java.io.*;

import edu.mccc.cos210.qrks.qrcode.*;

public class QRSecureBuilderPanel extends QRBuilderPanel {
	private static final long serialVersionUID = 1L;
	private PrivateKey privateKey = null;
	private String myAlias = null;
	//TODO: figure out what the type should be for key!
	@Override
	public byte[] encode(EncodingMode em) {
		if (privateKey != null) {
			Signature sig;
			byte [] data;
			byte[] signature;
			try {
				data = super.encode(em);
				sig = Signature.getInstance(Viewer.ALGORITHM);
				sig.initSign(privateKey);
				sig.update(Viewer.SEED);
				sig.update(data);
				signature = sig.sign();
			} catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
				signature = data = null;
			}
			if (data != null) {
				byte [] packed = Arrays.copyOf(data, data.length + 1 + signature.length);
				for (int i = 0, j = data.length + 1; i < signature.length; i++) {
					packed[i + j] = signature[i];
				}
				return packed;
			}
		}
		return super.encode(em);
	}
	
	@Override
	public Factory<Item<BufferedImage>> getFactory() {
		String text = getText();
		if (privateKey != null) {
			Signature sig;
			byte [] data;
			byte[] signature;
			try {
				data = text.getBytes("ISO-8859-1");
				sig = Signature.getInstance(Viewer.ALGORITHM);
				sig.initSign(privateKey);
				sig.update(Viewer.SEED);
				sig.update(data);
				signature = sig.sign();
			} catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException | UnsupportedEncodingException  e) {
				signature = data = null;
			}
			if (data != null) {
				byte [] packed = Arrays.copyOf(data, data.length + 1 + signature.length);
				for (int i = 0, j = data.length + 1; i < signature.length; i++) {
					packed[i + j] = signature[i];
				}
				return ((QRBuilder) getBuilder()).new QRFactory(packed, EncodingMode.BYTE, getErrorCorrectionLevel(), getPixelsPerUnit());
			}
		}
		return ((QRBuilder) getBuilder()).new QREncodingFactory(text, getErrorCorrectionLevel(), getPixelsPerUnit());
	}

	public QRSecureBuilderPanel(final QRSecureBuilder builder) {
		super(builder);

		final JTextField keyName = new JTextField("<No Key Selected>", 18);
		keyName.setEditable(false);
		final JButton selectKey = new JButton("Select Key");
		selectKey.setMnemonic(KeyEvent.VK_K);
		
		
		ActionListener sal = new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				JFileChooser fc = new JFileChooser();
			//	fc.setAcceptAllFileFilterUsed(false);
			//	fc.addChoosableFileFilter(new KSFileFilter());
				
				int returnVal = fc.showDialog(QRSecureBuilderPanel.this, "Load Key");
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					privateKey = QRKeySecure.getPrivateKey(file);
					if(privateKey != null) {
						keyName.setText(file.getName());
					} else {
						keyName.setText("<No Key Selected>");
					}
					updateInfo();
					/*
					boolean tryAgain = false;
					JPasswordField jpf = new JPasswordField();
					int r = JOptionPane.showConfirmDialog(null, jpf, "Password:", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
					if (r == JOptionPane.OK_OPTION) {
						
						char[] pw = jpf.getPassword();
						KeyStore ks = null;
						try {
							ks = KeyStore.getInstance("JCEKS");	//TODO: how do i know what type the ks is???
						} catch (KeyStoreException kse) {
							//implode
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
							Enumeration<String> aliases = null;
							try {
								aliases = ks.aliases();
							} catch (KeyStoreException e2) {
								return;
							}
							java.util.List<String> pkl = new LinkedList<String>();
							KeyStore.ProtectionParameter kspp = new KeyStore.PasswordProtection(pw); 
							
							while (aliases.hasMoreElements()) {
								String alias = aliases.nextElement();
								KeyStore.PrivateKeyEntry pke;
								try {
									if (ks.entryInstanceOf(alias, PrivateKeyEntry.class)) {
										pkl.add(alias);
									}
								} catch (KeyStoreException e9) {
									return;
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
		
							int m = JOptionPane.showConfirmDialog(null, bp, "Select Key by Alias", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
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
					
					*/
				} 
				//Reset the file chooser for the next time it's shown.
			//	fc.setSelectedFile(null);
			}
		};
		
		selectKey.addActionListener(sal);
		
		
		
		JPanel skp = new JPanel();
		skp.setPreferredSize(new Dimension (200, 135));
		skp.setToolTipText("Secure Key:");
		skp.setBorder(BorderFactory.createTitledBorder("Secure Key:"));
		skp.setLayout(new BorderLayout());
		skp.add(selectKey, BorderLayout.SOUTH);;
		skp.add(keyName, BorderLayout.NORTH);	
		
		
		add(skp);
	}
}