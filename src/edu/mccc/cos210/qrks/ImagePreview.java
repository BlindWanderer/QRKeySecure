package edu.mccc.cos210.qrks;

import javax.swing.*;
import java.beans.*;
import java.awt.*;
import java.io.File;
/**
 * Allows the FileChooser to display a thumbnail preview of any jpg / tif / png image file.
 */
public class ImagePreview extends JComponent implements PropertyChangeListener {
    ImageIcon thumbnail = null;
    File file = null;
   
    public ImagePreview(JFileChooser fc) {
        setPreferredSize(new Dimension(100, 50));
        fc.addPropertyChangeListener(this);
    }
    /**
	 * Gets (from image file), resizes, and sets thumbnail preview for image file.
	 */
    public void loadImage() {
        if (file == null) {
            thumbnail = null;
            return;
        }

        ImageIcon tmpIcon = new ImageIcon(file.getPath());
        if (tmpIcon != null) {
            if (tmpIcon.getIconWidth() > 90 || tmpIcon.getIconWidth() < 90) {
                thumbnail = new ImageIcon(tmpIcon.getImage().
                                          getScaledInstance(90, -1,
                                                      Image.SCALE_DEFAULT));
            }
            if (tmpIcon.getIconHeight() > 40) {
                thumbnail = new ImageIcon(tmpIcon.getImage().
                                          getScaledInstance(-1, 40,
                                                      Image.SCALE_DEFAULT));
            } else {
                thumbnail = tmpIcon;
            }
        }
    }
    /**
	 * Changes the thumbnail preview based on file selection; if a directory is selected, nothing is displayed.
	 */
    @Override
    public void propertyChange(PropertyChangeEvent e) {
        boolean update = false;
        String prop = e.getPropertyName();

        //If the directory changed, don't show an image.
        if (JFileChooser.DIRECTORY_CHANGED_PROPERTY.equals(prop)) {
            file = null;
            update = true;

        //If a file became selected, find out which one.
        } else if (JFileChooser.SELECTED_FILE_CHANGED_PROPERTY.equals(prop)) {
            file = (File) e.getNewValue();
            update = true;
        }

        //Update the preview accordingly.
        if (update) {
            thumbnail = null;
            if (isShowing()) {
                loadImage();
                repaint();
            }
        }
    }
    @Override
    protected void paintComponent(Graphics g) {
        if (thumbnail == null) {
            loadImage();
        }
        if (thumbnail != null) {
            int x = getWidth()/2 - thumbnail.getIconWidth()/2;
            int y = getHeight()/2 - thumbnail.getIconHeight()/2;

            if (y < 0) {
                y = 0;
            }

            if (x < 5) {
                x = 5;
            }
            thumbnail.paintIcon(this, g, x, y);
        }
    }
}