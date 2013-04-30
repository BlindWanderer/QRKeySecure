package edu.mccc.cos210.qrks;
import edu.mccc.cos210.qrks.util.*;
import java.util.*;
import javax.swing.*;
//import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.filechooser.*;
import java.io.*;
import javax.imageio.*;

public class QRReader implements Reader<BufferedImage, BufferedImage> {
	private class Match {
		public final int start;
		public final int end;
		public Match(final int start, final int end) {
			this.start = start;
			this.end = end;
		}
		public int getCenter() {
			return (start + end) / 2;
		}
	}
	public List<Item<BufferedImage>> process(BufferedImage input) {
		int width = input.getWidth();
		int height = input.getHeight();
		int[] data = new int[(width) * (height)];
		data = input.getRGB(0, 0, width, height, data, 0, width);
		/*
		try {
			;
		} catch (Exception e) {
			e.printStackTrace();
		}
		*/
		System.out.println(Arrays.toString(data));
			
		int[] distribution = new int[256];
		Arrays.fill(distribution, 0);
		
		for (int p = 0; p < data.length; p++) {
			distribution[ARGBToLightness(data[p])]++;
		}
		int lower = 1;
		int lc = distribution[0];
		int uc = distribution[255];
		for(int upper = 255; lower != upper;) {
			if (lc < uc) {
				lc += distribution[lower++];
			} else {
				uc += distribution[--upper];
			}
		}

		boolean [] bw = new boolean[data.length];
		for (int p = 0; p < data.length; p++) {
			bw[p] = ARGBToLightness(data[p]) >= lower;
		}
		//not a hundered percent sure this is the order it goes in!

		for (int y = 0, p = 0; y < height; y++) {
			for (Match m : process(bw, p, p+=height, 1)) {
				int width = m.end - m.start;
				for (Match w : process(bw, m.center() % height, width * height, height)) {
					for (Match w : process(bw, m.center(), width * height, height+1)) {
						
					}
				}
			}
		}
		return null;
	}
	public String getName() {
		return "QRCode Reader";
	}
	public void reset() {
	}
	private static int ARGBToLightness(int argb) {
//		int a = (rgba >> 24) & 0xFF;
		int r = (argb >> 16) & 0xFF;
		int g = (argb >> 8) & 0xFF;
		int b = (argb >> 0) & 0xFF;
		int min = Math.min(Math.min(r,g), b);
		int max = Math.max(Math.max(r,g), b);
		return (min + max) / 2;
	}
	private static List<Integer> runLengthEncode(boolean [] bw) {
		return runLengthEncode(bw, 0, bw.length, 1);
	}
	private static List<Integer> runLengthEncode(final boolean [] bw, final int start /*inclusive*/, final int end /*exclusive*/) {
		return runLengthEncode(bw, start, end, 1);
	}
	private static List<Integer> runLengthEncode(final boolean [] bw, final int start /*inclusive*/, final int end /*exclusive*/, int stride) {
		List<Integer> rle = new LinkedList<Integer>();
		int c = 1;
		boolean b = bw[start];
		for (int p = start + stride; p < end; p+= stride) {
			if (bw[p] != b) {
				rle.add(c);
				b = !b;
				c = 0;
			}
		}
		rle.add(c);
		return rle;
	}
	private static List<Match> process(final boolean [] bw, final int start, final int end, int stride) {
		int v1 = 0;
		int v2 = 0;
		int v3 = 0;
		int v4 = 0;
		int c = 0;
		int total = 0;
		int x = 0;
		List<Match> matches = new LinkedList<>();
		for (int v0 : runLengthEncode(bw, start, end, stride)) {
			total -= v4;
			v4 = v3;
			v3 = v2;
			v2 = v1;
			v1 = v0;
			c++;
			x += v0;
			total += v0;
			if (c > 4) {
				int t1 = total / 14;
				int t3 = (total * 3) / 14;
				int t5 = (total * 5) / 14;
				int t7 = total / 2;
				if ((v0 >= t1) && (v0 <=t3) && (v1 >= t1) && (v1 <=t3) && (v2 >= t5) && (v2 <=t7) && (v3 >= t1) && (v3 <=t3) && (v4 >= t1) && (v4 <=t3)) {
					matches.add(new Match(x - total, x));
				}
			}
		}
		return matches;
	}
}