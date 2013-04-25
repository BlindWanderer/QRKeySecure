package edu.mccc.cos210.qrks;

import java.awt.*;
import java.awt.image.*;
import javax.swing.*;

class ImageJPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private BufferedImage image = null;
	public ImageJPanel() {
		setPreferredSize(new Dimension(400, 400));
		setBorder(BorderFactory.createLineBorder(Color.BLACK, 8));
	}
	public void setImage(BufferedImage img) {
		image = img;
		repaint();
	}
	public BufferedImage getImage() {
		return image;
	}
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (image != null) {
			//TODO change draw image so that it draws image at size of control while maintaining aspect ratio
			g.drawImage(image, 8, 8, null);
		}
	}
}
