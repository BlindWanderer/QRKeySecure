package edu.mccc.cos210.qrks;

import java.io.File;
import javax.swing.*;
import javax.swing.filechooser.*;

/**
 * Creates a file filter to be used by the FileChooser (in the Viewer class). This filter allows only jpeg, tif, and png image files.
 */
public class ImageFileFilter extends FileFilter {
	
	  public final static String jpeg = "jpeg";
      public final static String jpg = "jpg";
      public final static String gif = "gif";
      public final static String tiff = "tiff";
      public final static String tif = "tif";
      public final static String png = "png";

      /**
       * Allow only jpeg, tif, and png image files.
       */   
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }
        String extension = getExtension(f).toLowerCase();
        if (extension != null) {
            if (extension.equals(tiff) ||
                extension.equals(tif) ||
                extension.equals(gif) ||
                extension.equals(jpeg) ||
                extension.equals(jpg) ||
                extension.equals(png)) {
                    return true;
            } else {
                return false;
            }
        }
        return false;
    }

    /**
     * Returns the name of the filter.
     * @return Name of filter
     */
    public String getDescription() {
        return "Images";
    }
    /**
     * Gets extension of the file.
     * @return File extension.
     */
    static String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf(".");
        if (i > 0 &&  i < s.length() - 1) {
            ext = s.substring(i+1);
        }
        
        return ext;
    }
}
      