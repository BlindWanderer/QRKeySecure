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
	private static class Match {
		public final int start;
		public final int end;
		public final int stride;
		public Match(final int start, final int end, final int stride) {
			this.start = start;
			this.end = end;
			this.stride = stride;
		}
		public int getCenter() {
			//If stride > 1 and the number of strides is odd, you can't just average the two numbers as that would put you between strides!
			//end is exclusive <strike>so we have to subtract one strides width</strike>
			return start + (((end - start /*- stride*/) / (stride * 2)) * stride);
		}
		public double getLength(int width) {
			return Math.sqrt(Math.pow((end - start) % width, 2) + Math.pow((end - start) / width, 2));
		}
		public String toString() {
			return "["+start+","+end+") +=" + stride;
		}
	}
	private static class MatchGroup {
		public final Match verticle;
		public final Match horizontal;
		public final Match diagonalPlus;
		public final Match diagonalMinus;
		MatchGroup(final Match verticle, final Match horizontal, final Match diagonalPlus, final Match diagonalMinus) {
			this.verticle = verticle;
			this.horizontal = horizontal;
			this.diagonalPlus = diagonalPlus;
			this.diagonalMinus = diagonalMinus;
		}
		public String toString() {
			return Arrays.toString(Utilities.newGenericArray(verticle, horizontal, diagonalPlus, diagonalMinus));
		}
	}
	public List<Item<BufferedImage>> process(BufferedImage input, SingWorkerProtected<BufferedImage> swp) {
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
//		System.out.println(Arrays.toString(data));
		
		int lower = getCenterColor(data);

//		System.out.println("3");

		boolean [] bw = new boolean[data.length];
		try{
			for (int p = 0; p < data.length; p++) {
				if (bw[p] = (ARGBToLightness(data[p]) >= lower)) {
					setARGB(input, p, 0xFFFFFFFF);
				} else {
					setARGB(input, p, 0xFF000000);
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
/*
		try {
			ImageIO.write(input, "png", new File("C:\\test.png"));
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	*/	
//		System.out.println("4");
		
		swp.publish(input);
		
		BufferedImage prog = input;//Utilities.convertImageToBufferedImage(input);
		
		List<MatchGroup> matches = new LinkedList<>();
		
		for (int y = 0, p = 0; y < height; y++) {
//			System.out.print("y");
			for (Match m : process(bw, p, p+=width, 1, prog)) {//horizontal
//				System.out.print("m");
				int mc = m.getCenter();
				for (Match w : process(bw, mc % width, width * height, width, prog)) {//vertical
					if((mc >= w.start) && (mc <= w.end)){
//						System.out.print("w");
						int wc = w.getCenter();
						int zstride = width + 1;
						int zk = (wc % width);
						int zstart = wc - zk - zk * width;
						int zend = Math.min(zstart + width * width, width * height);
						if (zstart < 0) {
							zstart = (zstart % zstride) + zstride;
						}
						for (int a = zstart; a < zend; a+=zstride) {
							setARGB(prog, a, 0xFF0000FF);
						}
						for (Match z : process(bw, zstart, zend, zstride, prog)) {//diagonal
							if((wc >= z.start) && (wc <= z.end)) {
//								System.out.print("z");
								int zc = z.getCenter();
								int sstride = width - 1;
								int sk = sstride - (zc % width);
								int sstart = zc + sk - sk * width;
								int send = Math.min(sstart + width * (width - 1), width * height);//BUG? not sure why this one needs a minus 1
								if (sstart < 0) {
									sstart = (sstart % sstride) + sstride;
								}
								for (int a = sstart; a < send; a+=sstride) {
									setARGB(prog, a, 0xFF0000FF);
								}
								
								for (Match s : process(bw, sstart, send, sstride, prog)) {//diagonal
									if((zc >= s.start) && (zc <= s.end)) {
										matches.add(new MatchGroup(m,w,z,s));
										setARGB(prog, m.start, 0xFFFF0000);
										setARGB(prog, m.end - 1, 0xFFFF0000);
										setARGB(prog, mc, 0xFF00FF00);
										setARGB(prog, w.start, 0xFFFF0000);
										setARGB(prog, w.end - width, 0xFFFF0000);
										setARGB(prog, wc, 0xFF00FF00);
										setARGB(prog, z.start, 0xFFFF0000);
										setARGB(prog, z.end - zstride, 0xFFFF0000);
										setARGB(prog, z.getCenter(), 0xFF00FF00);
										setARGB(prog, s.start, 0xFFFF0000);
										setARGB(prog, s.end - sstride, 0xFFFF0000);
										setARGB(prog, s.getCenter(), 0xFF00FF00);
									}
								}
							}
						}
					}
				}
			}
		}
		
		try {
			ImageIO.write(prog, "png", new File("C:\\test2.png"));
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}

		swp.publish(prog);

//		System.out.println("5");		
		System.out.println(matches);		
		System.out.println(matches.size());
		return null;
	}
	private static int getCenterColor(int [] data){
		int[] distribution = new int[256];
		Arrays.fill(distribution, 0);

//		System.out.println("1");
		
		for (int p = 0; p < data.length; p++) {
			distribution[ARGBToLightness(data[p])]++;
		}

//		System.out.println("2");

		int lower = 0;
		int upper = 256;
		int lc = 0;
		int uc = 0;
		while (lower < 32) {//This approach didn't take into consideration solid color backgrounds, which overwhelm the QR code signal
				lc += distribution[lower++];
		}
		while (upper > 224) {
				uc += distribution[--upper];
		}			
		while (lower != upper) {
			if (lc < uc) {
				lc += distribution[lower++];
			} else {
				uc += distribution[--upper];
			}
		}
		return lower;
	}
	
	
	private static void setARGB(BufferedImage image, int p, int argb) {
		int w = image.getWidth();
		int x = p % w; 
		int y = p / w;
		image.setRGB(x,y,argb);
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
	private static List<Integer> runLengthEncode(final boolean [] bw, final int start /*inclusive*/, final int end /*exclusive*/, int stride, BufferedImage prog) {
		List<Integer> rle = new LinkedList<Integer>();
		int c = stride;
		boolean b = bw[start];
		for (int p = start + stride; p < end; p+= stride) {
			if (bw[p] != b) {
				setARGB(prog, p, 0xFF0000FF);
				rle.add(c);
				b = !b;
				c = 0;
			}
			c += stride;
		}
		rle.add(c);
		return rle;
	}
	private static List<Match> process(final boolean [] bw, final int start, final int end, int stride, BufferedImage prog) {
		int v1 = 0;
		int v2 = 0;
		int v3 = 0;
		int v4 = 0;
		int c = 0;
		int total = 0;
		int x = 0;
		List<Match> matches = new LinkedList<>();
		for (int v0 : runLengthEncode(bw, start, end, stride, prog)) {
			c++;
			x += v0;
			total += v0;
			if (c >= 5) {
				int t1 = total / 14;
				int t3 = (total * 3) / 14;
				int t5 = (total * 5) / 14;
				int t7 = total / 2;
				if ((v0 >= t1) && (v0 <=t3) && (v1 >= t1) && (v1 <=t3) && (v2 >= t5) && (v2 <=t7) && (v3 >= t1) && (v3 <=t3) && (v4 >= t1) && (v4 <=t3)) {
					matches.add(new Match(start + (x - total), start + x, stride));
				}
			}
			total -= v4;
			v4 = v3;
			v3 = v2;
			v2 = v1;
			v1 = v0;
		}
		return matches;
	}
}