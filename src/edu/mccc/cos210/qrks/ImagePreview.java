package edu.mccc.cos210.qrks;

import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
import java.beans.*;
/**
 * Allows the FileChooser to display a thumbnail preview of any jpg / tif / png image file.
 */
public class ImagePreview extends JComponent implements PropertyChangeListener {
    private static final long serialVersionUID = 1L;
	private static final int MARGIN_WIDTH_DOUBLED = 10;
    private BufferedImage image = null;
    private Image thumbnail = null;
    private File file = null;
	private boolean reloadNeeded = false;
	
    public ImagePreview(JFileChooser fc) {
        setPreferredSize(new Dimension(100, 50));
        fc.addPropertyChangeListener(this);
    }
	
	public BufferedImage getImage(){
		if (reloadNeeded) {
			loadImage();
		}
		return image;
	}
	
    /**
	 * Gets (from image file), resizes, and sets thumbnail preview for image file.
	 */
    public void loadImage() {
		reloadNeeded = false;
		thumbnail = null;
		image = null;
        if (file != null) {
			try {
				image = ImageIO.read(file);
			} catch (IOException ioe) {
				return;
			}
			if (image != null) {
				double ix = image.getWidth();
				double iy = image.getHeight();
				if (ix > 0 && iy > 0) { //avoid dbz
					double mult = Math.min((  getWidth() - MARGIN_WIDTH_DOUBLED) / ix, 
										   ( getHeight() - MARGIN_WIDTH_DOUBLED) / iy);
					if (mult > 0) {
						if (mult < 1.0){
							thumbnail = image.getScaledInstance((int)Math.round(mult * ix),
																(int)Math.round(mult * iy),
																Image.SCALE_DEFAULT);
						} else {
							thumbnail = image;
						}
					}
				}
			}
		}
    }
    /**
	 * Changes the thumbnail preview based on file selection; if a directory is selected, nothing is displayed.
	 */
    @Override
    public void propertyChange(PropertyChangeEvent e) {
		switch (e.getPropertyName()) {
			case JFileChooser.DIRECTORY_CHANGED_PROPERTY:
				file = null;
				break;
			case JFileChooser.SELECTED_FILE_CHANGED_PROPERTY:
				file = (File) e.getNewValue();
				break;
			default:
				return;
		}
		reloadNeeded = true;
		image = null;
		thumbnail = null;
		if (isShowing()) {
			loadImage();
			repaint();
		}
    }
    @Override
    protected void paintComponent(Graphics g) {
		if (reloadNeeded) {
			loadImage();
		}
        if (thumbnail != null) {
            int x = (getWidth() - thumbnail.getWidth(null)) / 2;
            int y = (getHeight() - thumbnail.getHeight(null)) / 2;
            g.drawImage(thumbnail, x, y, null);
        }
    }
}