package edu.mccc.cos210.qrks;
import edu.mccc.cos210.qrks.util.*;
import javax.swing.*;
import javax.swing.filechooser.*;
import java.io.*;
import java.net.URLEncoder;
import javax.imageio.*;
import java.util.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.awt.event.*;
import java.awt.image.*;
import java.awt.Image;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.BorderLayout;
public abstract class QRCode implements Item<BufferedImage> {
	public QRCode(boolean[][] matrix, Object data) {
		setMatrix(matrix);
		setData(data);
	}
	private Object data;
	private String text;
	private boolean[][] matrix;
	private BufferedImage img = null;
	protected BufferedImage getImage() {
		if(img == null){
			img = Utilities.rescaleImage(Decoder.visualizeMatrix(matrix), 10.0);
		}
		return img;
	}
	public BufferedImage save() {
		return getImage();
	}
	protected void setText(String text) {
		this.text = text;
	}
	public final String getText() {
		return this.text;
	}
	protected final void setData(Object data) {
		this.data = data;
		decodeData(data);
	}
	protected void decodeData(Object data) {
		String text;
		if (data instanceof byte[]) {
			try{
				text = new String((byte[])data, "ISO-8859-1");
			} catch(UnsupportedEncodingException e) {
				text = "I'm a little tea pot, short and stout.";
			}
		} else if (data instanceof String) {
			text = (String)data;
		} else if (data == null){
			text = "<null>";
		} else {
			text = data.toString();
		}
		setText(text);
	}
	public final Object getData() {
		return this.data;
	}
	protected void setMatrix(boolean[][] matrix) {
		this.matrix = matrix;
	}
	public final boolean[][] getMatrix() {
		return this.matrix;
	}
	public boolean getSecure(){
		return false;
	}
}