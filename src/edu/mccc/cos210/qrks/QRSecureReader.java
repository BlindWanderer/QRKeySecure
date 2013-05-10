package edu.mccc.cos210.qrks;
import edu.mccc.cos210.qrks.util.*;
import edu.mccc.cos210.qrks.qrcode.*;
import java.util.*;
import javax.swing.*;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.filechooser.*;
import java.io.*;
import javax.imageio.*;
import java.awt.Point;
import java.awt.Image;
import java.awt.BorderLayout;
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
				for (j = 0; j < (ba.length - 128); j++) {
					if (ba[j] == 0) {
						last = j;
					}
				}
				if (j >= 0) {
					byte [] message = Arrays.copyOfRange(ba, 0, j);
					byte [] signature = Arrays.copyOfRange(ba, j+1, ba.length);
					secure = false;
					if (secure) {
						data = message;
					}
				}
			}
			super.decodeData(data);
		}
		public JPanel generateGUI() {
			JPanel gui = new JPanel();
			gui.setLayout(new BorderLayout());
			gui.add(new ImagePanel(getImage()), BorderLayout.CENTER);
			JTextArea info = new JTextArea(5, 50);
			info.setEditable(false);
			//Font f = new Font(info.getFont());
			info.setOpaque(false);
			info.setText(getText());
			gui.add(info, BorderLayout.SOUTH);
			return gui;
		}
	}
}