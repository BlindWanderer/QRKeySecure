package edu.mccc.cos210.qrks.util;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;
import javax.swing.filechooser.*;
import javax.imageio.*;
import java.lang.reflect.Array;
import java.util.Arrays;


public final class Utilities {
	private Utilities() {
	}/*
	public static void main(String [] str) {
		Point a = new Point(0,0);
		Point b = new Point(0,-1);
		Point c = new Point(0,1);
		Point d = new Point(1,0);
		Point e = new Point(-1,0);
		Point f = new Point(2,0);
		System.out.println(isIntersectionBetweenSegments(a, b, d, f));
	}*/
	public static double distance(Point a, Point b) {
		int x = a.x - b.x;
		int y = a.y - b.y;
		return  Math.sqrt(x * x + y * y);
	}
	public static int crossProduct(Point a, Point b) {
		return (a.x * b.y) - (a.y * b.x);
	}
	public static int crossProduct(int ax, int ay, int bx, int by) {
		return (ax * by) - (ay * bx);
	}
	public static Point add(Point a, int bx, int by) {
		return new Point(a.x + bx, a.y + by);
	}
	public static Point add(Point left, Point right) {
		return new Point(left.x + right.x, left.y + right.y);
	}
	public static Point subtract(Point left, Point right) {
		return new Point(left.x - right.x, left.y - right.y);
	}
	public static int dot(Point left, Point right) {
		return left.x * right.x + left.y * right.y;
	}
	public static int dot(Point left) {
		return left.x * left.x + left.y * left.y;
	}
	public static Point scale(Point a, double m) {
		return new Point((int)(a.x * m), (int)(a.y * m));
	}
	@SuppressWarnings({"unchecked"})
	public static <T> String toString(T ... values){
		return Arrays.toString(values);
	}
	static boolean isCounterClockWise(Point a, Point b, Point c) {
		return (c.y - a.y) * (b.x - a.x) > (b.y - a.y) * (c.x - a.x);
	}//http://www.bryceboe.com/2006/10/23/line-segment-intersection-algorithm/
	public static boolean doTheseLineSegmentsIntersect(Point aStart, Point aEnd, Point bStart, Point bEnd) {
        return (isCounterClockWise(aStart, bStart, bEnd) != isCounterClockWise(aEnd, bStart, bEnd)) && 
			   (isCounterClockWise(aStart, aEnd, bStart) != isCounterClockWise(aStart, aEnd, bEnd));
	}
	public static double getSegmentIntersectionStrength(Point aStart, Point aEnd, Point bStart, Point bEnd) {
		Point aM = subtract(aEnd, aStart);
		Point bM = subtract(bEnd, bStart);
		Point ab = subtract(aStart, bStart);
		double cM = 1.0 / crossProduct(aM, bM);
		double s = crossProduct(aM, ab) * cM;
		double t = crossProduct(bM, ab) * cM;
//		if (s >= 0 && s <= 1 && t >= 0 && t <= 1) {
//			return add(aStart, scale(aM, t));
//			return add(bStart, scale(bM, s));
//		}
//		return null;
		return Math.abs(s - .5) + Math.abs(t - .5);
	}
	public static Point getLineLineIntersection(Point aStart, Point aEnd, Point bStart, Point bEnd) {
		Point aM = subtract(aEnd, aStart);
		Point bM = subtract(bEnd, bStart);
		Point ab = subtract(aStart, bStart);
//		double cM = 1.0 / crossProduct(aM, bM);
//		double s = crossProduct(aM, ab) * cM;
//		double t = crossProduct(bM, ab) * cM;
//		if (s >= 0 && s <= 1 && t >= 0 && t <= 1) {
			return add(aStart, scale(aM, crossProduct(bM, ab) / (double)crossProduct(aM, bM)));
//			return add(bStart, scale(bM, crossProduct(aM, ab) / (double)crossProduct(aM, bM)));
//			return add(aStart, scale(aM, t));
//			return add(bStart, scale(bM, s));
//		}
//		return null;
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