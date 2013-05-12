package edu.mccc.cos210.qrks;
import edu.mccc.cos210.qrks.util.*;
import edu.mccc.cos210.qrks.qrcode.*;

import java.util.*;

import javax.swing.*;

import java.awt.event.*;
import java.awt.image.*;
import java.io.*;

import javax.imageio.*;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.TextArea;
import java.awt.geom.*;
import java.security.*;

public class QRSecureReader extends QRReader {
	private PublicKey publicKey;
	public QRSecureReader(PublicKey pk){
		super();
		publicKey = pk;
	}
	@Override
	public QRCode newQRCode(boolean [][] matrix, Object raw){
		return new QRSecureCode(matrix, raw);
	}
	@Override
	public List<Item<BufferedImage>> process(BufferedImage input, SwingWorkerProtected<?, BufferedImage> swp) {
		List<Item<BufferedImage>> out = super.process(input, swp);
		for(int i = 0; i < out.size(); i++) {
			QRCode c =  (QRCode)out.get(i);

		}
		return out;
	}
	@Override
	public String getName() {
		return "QRCode Reader";
	}
	@Override
	public void reset() {
		super.reset();
		//it's possible we might have our own things to reset so we have one as well.
	}
	public class QRSecureCode extends QRCode {
		private QRCode base;
		private boolean secure;
		private QRSecureCode(boolean [][] matrix, Object data) {
			super(matrix, data);
		}
		@Override
		protected void decodeData(Object data) {
			secure = false;
			if (data instanceof byte[]) {
				//look for a signature after a rouge null or something
				byte [] ba = (byte[])data;
				int j;
				int last = -1;
				if (ba.length > Viewer.DIGEST_SIZE && ba[ba.length - Viewer.DIGEST_SIZE - 1] == 0) {
					byte [] message = Arrays.copyOfRange(ba, 0, ba.length - Viewer.DIGEST_SIZE - 1);
					byte [] signature = Arrays.copyOfRange(ba, ba.length - Viewer.DIGEST_SIZE, ba.length);
					if (publicKey != null) {
						try {
							Signature sig = Signature.getInstance(Viewer.ALGORITHM);
							sig.initVerify(publicKey);
							sig.update(Viewer.SEED);
							sig.update(message);
							secure = sig.verify(signature);
							if (secure) {
								data = message;
							} else {
								System.out.println("Failed to validate");
							}
						} catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
							e.printStackTrace();
						}
					} else {
						System.out.println("Public key not available to verify file against");
					}
				} else {
					System.out.println("Length mismatch");
				}
			} else {
				System.out.println("wrong data type to be signed");
			}
			super.decodeData(data);
		}
		public JPanel generateGUI() {
			BufferedImage redX = new BufferedImage(50, 50, BufferedImage.TYPE_4BYTE_ABGR);
			Graphics2D g = redX.createGraphics();
			g.setColor(Color.RED);
			g.setStroke(new BasicStroke(5));
			g.setFont(new Font("Dialog", Font.PLAIN, 22));
			g.drawString("X", 25, 25);
			
			BufferedImage green = new BufferedImage(50, 50, BufferedImage.TYPE_4BYTE_ABGR);
			Graphics2D gd = green.createGraphics();
			gd.setColor(Color.GREEN);
			gd.setStroke(new BasicStroke(5));
			gd.drawOval(25, 25, 40, 40);
			
			JPanel gui = new JPanel();;
			gui.setLayout(new BorderLayout());
			JPanel ip = new ImagePanel(getImage());
			Utilities.cloberSizes(ip, new Dimension(200,200));
			ip.setPreferredSize(new Dimension(200, 200));
			//gui.add(ip, BorderLayout.WEST);
			JPanel top = new JPanel();
			top.setLayout(new BorderLayout());
			top.add(ip, BorderLayout.WEST);
			if (secure) {
		//		gui.add(new ImagePanel(green), BorderLayout.NORTH);
				JTextArea secure = new JTextArea("     Secure");
				secure.setForeground(Color.GREEN);
				secure.setBackground(null);
				secure.setFont(new Font("Dialog", Font.PLAIN, 22));
				top.add(secure, BorderLayout.CENTER);
				//gui.add(secure, BorderLayout.CENTER);
			} else {
		//		gui.add(new ImagePanel(redX), BorderLayout.NORTH);
				JTextArea notSecure = new JTextArea("     NOT Secure");
				notSecure.setForeground(Color.RED);
				notSecure.setBackground(null);
				notSecure.setFont(new Font("Dialog", Font.PLAIN, 22));
				//gui.add(notSecure, BorderLayout.CENTER);
				top.add(notSecure, BorderLayout.CENTER);
			}
			JTextArea info = new JTextArea(20, 50);
			info.setMargin(new Insets(10, 10, 10, 10));
			JScrollPane jp = new JScrollPane(info);
			info.setEditable(false);
			info.setLineWrap(true);
			info.setWrapStyleWord(true);
			info.setTabSize(3);
			//Font f = new Font(info.getFont());
			info.setOpaque(false);
			info.setText(getText());
			info.setFont(new Font("Dialog", Font.PLAIN, 22));
		
			gui.add(top, BorderLayout.NORTH);
			gui.add(jp, BorderLayout.SOUTH);		
			return gui;
		}
		@Override
		public boolean getSecure(){
			return secure;
		}
	}
}