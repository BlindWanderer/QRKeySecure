package edu.mccc.cos210.qrks;
import java.awt.*;
import java.awt.image.*;
import javax.swing.*;
/**
 * ImageJPanel: A JPanel that can conditionally have an image drawn into it.
 */
public class ImagePanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private static final int WIDTH= 400;
	private static final int HEIGHT = 400;
	private static final int BORDER = 8;
	private static final int FULL_BORDER = BORDER * 2;
	private static final int FULL_WIDTH = FULL_BORDER + WIDTH;
	private static final int FULL_HEIGHT = FULL_BORDER + HEIGHT;
	private BufferedImage image = null;
	public ImagePanel() {
		Dimension d = new Dimension(FULL_WIDTH, FULL_HEIGHT);
		setPreferredSize(d);
		setSize(d);
		setBorder(BorderFactory.createLineBorder(Color.BLACK, BORDER));
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
			int ix = image.getWidth();
			int iy = image.getHeight();
			if ((ix > 0) && (iy > 0)) { //There is something to display.
				int cx = getWidth();
				int cy = getHeight();
				//Figure out how much we have to shrink it to get it to fit.
				final double sm = Math.min((cx - FULL_BORDER) / (double)ix, (cy - FULL_BORDER) / (double)iy);
				if (sm > 0) { //has to be space to display it in. Zero or less indicates there is no space to display it in.
					Image t = image;
					if (sm < 1.0) { //it's too big
						ix = (int)Math.round(ix * sm);
						iy = (int)Math.round(iy * sm);
						t = image.getScaledInstance(ix, iy, Image.SCALE_DEFAULT);
					}
					g.drawImage(t, (cx - ix)/ 2, (cy - iy) / 2, null);
				}
			}
		}
	}
}
