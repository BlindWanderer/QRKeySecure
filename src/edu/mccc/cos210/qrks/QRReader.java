package edu.mccc.cos210.qrks;
import java.util.*;
import javax.swing.*;

public class QRReader implements Reader<BufferedImage, BufferedImage> {
	public List<Item<BufferedImage>> process(BufferedImage input) {
		int width = input.getWidth();
		int height = input.getHeight();
		int[] data = input.getRGB(0, 0, width, height, new int[width * height], 0, 1);

		int[] distribution = new int[256];
		Arrays.fill(distribution, 0);
		
		for (int p = 0; p < data.length; p++) {
			distribution[RGBAToLightness(data[p])]++;
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
			bw[p] = RGBAToLightness(data[p]) >= lower;
		}
		for (int x = 0, p = 0; x < width; x++) {
			for (int y = 0; y < height; y++, p++) {
				
			}
		}
	}
	public String getName() {
		return "QRCode Reader";
	}
	public void reset() {
	}
	private static int RGBAToLightness(int rgba) {
		int r = (rgba >> 0) & 0xFF;
		int g = (rgba >> 8) & 0xFF;
		int b = (rgba >> 16) & 0xFF;
		int min = Math.min(Math.min(r,g), b);
		int max = Math.max(Math.max(r,g), b);
		return (min + max) / 2;
	}
	private static List<Integer> runLengthEncode(boolean [] bw) {
		return runLengthEncoder(bw, 0, bw.length);
	}
	private static List<Integer> runLengthEncode(final boolean [] bw, final int start /*inclusive*/, final int end /*exclusive*/) {
		List<Integer> rle = new List<Integer>();
		int c = 1;
		boolean b = bw[start];
		for (int p = start + 1, end; p < end; p++) {
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