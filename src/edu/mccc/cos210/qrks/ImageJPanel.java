package edu.mccc.cos210.qrks;

import java.awt.Graphics;
import java.awt.image.*;

import javax.swing.JPanel;

class ImageJPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private BufferedImage image = null;
	public void setImage(BufferedImage img) {
		image = img;
		invalidate();
	}
	public BufferedImage getImage() {
		return image;
	}
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (image != null) {
			//TODO change draw image so that it draws image at size of control while maintaining aspect ratio
			g.drawImage(image, 0, 0, null);
		}
	}
}
