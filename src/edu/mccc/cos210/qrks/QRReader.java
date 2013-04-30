package edu.mccc.cos210.qrks;
import java.util.*;
import javax.swing.*;
//import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.filechooser.*;
import java.io.*;
import javax.imageio.*;

public class QRReader implements Reader<BufferedImage, BufferedImage> {
	public java.util.List<Item<BufferedImage>> process(BufferedImage input) {
		int width = input.getWidth();
		int height = input.getHeight();
		int[] data = new int[(width) * (height)];
		//Arrays.fill(data, 0x88888888);
		data = input.getRGB(0, 0, width, height, data, 0, width)
		/*
		try {
			;
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(Arrays.toString(data));
		*/
			
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
			for (int x = 0; x < width; x++, p++) {
				
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
		return runLengthEncode(bw, 0, bw.length);
	}
	private static List<Integer> runLengthEncode(final boolean [] bw, final int start /*inclusive*/, final int end /*exclusive*/) {
		List<Integer> rle = new LinkedList<Integer>();
		int c = 1;
		boolean b = bw[start];
		for (int p = start + 1; p < end; p++) {
			if (bw[p] != b) {
				rle.add(c);
				b = !b;
				c = 0;
			}
		}
		rle.add(c);
		return rle;
	}
}