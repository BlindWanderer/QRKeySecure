package edu.mccc.cos210.qrks;

import java.awt.*;
import java.awt.image.*;

import javax.swing.*;
/**
 * ImageJPanel: A JPanel that can conditionally have an image drawn into it.
 */
public class ImageJPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private static final int height = 400;
	private static final int width= 400;
	private static final int border = 8;
	private BufferedImage image = null;
	public ImageJPanel() {
		setPreferredSize(new Dimension(width, height));
		setBorder(BorderFactory.createLineBorder(Color.BLACK, border));
	}
	/**
	 * Sets the image inside this panel to the BufferedImage obtained from the a file (via FileChooser).
	 */
	public void setImage(final BufferedImage img) {
		image = img;
		repaint();
	}
	/**
	 * Gets the image inside this panel.
	 * @return BufferedImage currently displayed in the panel
	 */
	public BufferedImage getImage() {
		return image;
	}
	@Override
	public void paintComponent(final Graphics g) {
		super.paintComponent(g);
		if (image != null) {
			Image t = image;
			int x;
			int y;
	
			double percentW = (getWidth() - border*2) / (double)image.getWidth();
			t = image.getScaledInstance((int)(image.getWidth() * percentW), -1, Image.SCALE_DEFAULT);
			x = (int)(image.getWidth() * percentW);
			y = (int)(image.getHeight() * percentW);
			if ((image.getHeight() * percentW) > (getHeight() - border*2)) {
				t = image.getScaledInstance(-1, getHeight() - border*2, Image.SCALE_DEFAULT);
				double percentH = (getHeight() - border*2) / (double)y;
				x = (int)(x * percentH);
				y = (int)(y * percentH);
			}
			
			g.drawImage(t, (getWidth() )/ 2 - x / 2, (getHeight() ) / 2 - y / 2, null);
		}
	}
}
