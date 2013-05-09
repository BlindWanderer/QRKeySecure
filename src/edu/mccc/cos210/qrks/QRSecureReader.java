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

public class QRSecureReader extends QRReader {
	public class QRSecureCode extends QRCode {
		private QRCode base;
		private QRSecureCode(QRCode q) {
			super(q.matrix, q.data);
			base = q;
		}
		public BufferedImage save() {
			return base.save();
		}
		public JPanel generateGUI() {
			//TODO add some indication that it is secure!
			return base.generateGUI();
		}
	}
	@Override
	public List<Item<BufferedImage>> process(BufferedImage input, SwingWorkerProtected<?, BufferedImage> swp) {
		List<Item<BufferedImage>> out = super.process(input, swp);
		for(int i = 0; i < out.size(); i++) {
			QRCode c =  (QRCode)out.get(i);
			if (c.data instanceof byte[]) {
				//look for a signature after a rouge null or something
				if (true) {
					out.set(i, new QRSecureCode(c));
				}
			}
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
}