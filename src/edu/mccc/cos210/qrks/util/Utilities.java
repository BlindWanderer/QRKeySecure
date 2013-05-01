package edu.mccc.cos210.qrks.util;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;
import javax.swing.filechooser.*;
import javax.imageio.*;
import java.lang.reflect.Array;

public final class Utilities {
	private Utilities() {
	}
	public static int crossProduct(Point a, Point b) {
		return (a.x * b.y) - (a.y * b.x);
	}
	public static Point add(Point left, Point right) {
		return new Point(left.x + right.x, left.y + right.y);
	}
	public static Point subtract(Point left, Point right) {
		return new Point(left.x - right.x, left.y - right.y);
	}
	public static boolean isIntersectionBetweenSegments(Point aStart, Point aEnd, Point bStart, Point bEnd) {
			Point e = subtract(aEnd, aStart);
			Point f = subtract(bEnd, bStart);
			Point p = new Point(-e.y, e.x);
			int acp = crossProduct(subtract(aStart, bStart), p);
			int fp = crossProduct(f, p);
			return ((acp <= fp) && (fp >= 0) && acp >= 0) || ((acp >= fp) && (fp <= 0) && acp <= 0); //0 <= (acp / fp) <= 1
	}
	public static double getSegmentIntersectionStrength(Point aStart, Point aEnd, Point bStart, Point bEnd) {
			Point e = subtract(aEnd, aStart);
			Point f = subtract(bEnd, bStart);
			Point p = new Point(-e.y, e.x);
			int fp = crossProduct(f, p);
			return Math.abs((2 * crossProduct(subtract(aStart, bStart), p) - fp) / (double)fp);
	}
	public static BufferedImage convertImageToBufferedImage(Image image) {
		BufferedImage bi = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = bi.createGraphics();
		g.drawImage(image, 0,0, null);
		g.dispose();
		return bi;
	}
	public static Dimension addDimensions(Dimension left, Dimension ... right) {
		return addDimensions(left.getWidth(), left.getHeight(), right);
	}
	public static Dimension addDimensions(double x, double y, Dimension ... right) {
		if (right != null) {
			for (Dimension r : right) {
				x += r.getWidth();
				y += r.getHeight();
			}
		}
		Dimension d = new Dimension();
		d.setSize(x, y);
		return d;
	}
	public static Dimension cloberSizes(JComponent c, Dimension d) {
		c.setPreferredSize(d);
		c.setMinimumSize(d);
		c.setMaximumSize(d);
		c.setSize(d);
		return d;
	}
	@SuppressWarnings({"unchecked"})
	public static <T> T[] newGenericArray(Class<T> clazz, int capacity){
		return (T[])Array.newInstance(clazz,capacity);
	}
	@SuppressWarnings({"unchecked"})
	public static <T> T[] newGenericArray(T ... values){
		return values;
	}
}