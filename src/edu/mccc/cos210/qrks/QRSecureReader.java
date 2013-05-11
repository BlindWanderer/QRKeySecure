package edu.mccc.cos210.qrks;
import edu.mccc.cos210.qrks.util.*;
import edu.mccc.cos210.qrks.qrcode.*;
import java.util.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
import java.awt.BorderLayout;
import java.awt.Image;
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
		private boolean secure = false;
		private QRSecureCode(boolean [][] matrix, Object data) {
			super(matrix, data);
		}
		@Override
		protected void decodeData(Object data) {
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
							}
						} catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
							e.printStackTrace();
						}
					}
				}
			}
			super.decodeData(data);
		}
		public JPanel generateGUI() {
			BufferedImage redX = new BufferedImage(50, 50, BufferedImage.TYPE_4BYTE_ABGR);
			Graphics2D g = redX.createGraphics();
			g.setColor(Color.RED);
			g.setStroke(new BasicStroke(5));
			g.drawString("X", 5, 45);
			
			
			BufferedImage green = new BufferedImage(50, 50, BufferedImage.TYPE_4BYTE_ABGR);
			Graphics2D gd = green.createGraphics();
			gd.setColor(Color.GREEN);
			gd.setStroke(new BasicStroke(5));
			gd.drawOval(5, 5, 40, 40);
			
			JPanel gui = new JPanel();;
			gui.setLayout(new BorderLayout());
			gui.add(new ImagePanel(getImage()), BorderLayout.NORTH);
			if (secure) {
				gui.add(new ImagePanel(green), BorderLayout.NORTH);
			} else {
				gui.add(new ImagePanel(redX), BorderLayout.NORTH);
			}
			JTextArea info = new JTextArea(25, 50);
			info.setEditable(false);
			//Font f = new Font(info.getFont());
			info.setOpaque(false);
			info.setText(getText());
			gui.add(info, BorderLayout.SOUTH);
//TODO: draw green o's			
			return gui;
		}
		@Override
		public boolean getSecure(){
			return secure;
		}
	}
}