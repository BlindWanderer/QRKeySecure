//NOTES:
//Use center on MatchHead to associate Matches better.
//Consider a post processing step to merge MatchHeads.
//Histagram of edges only, current method doesn't really work.
//Also it shouldn't pick colors in the middle of a gradiant.

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
import java.awt.Point;
import java.awt.geom.*;

public class QRReader implements Reader<BufferedImage, BufferedImage> {
	public static Point newPoint(int p, int width) {
		return new Point(p % width, p / width);
	}
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
		public Point getCenter(int width) {
			return newPoint(getCenter(), width);
		}
		public double getLength(int width) {
			double value = Math.sqrt(Math.pow((end % width) - (start % width), 2) + Math.pow((end / width) - (start / width), 2));
			//System.out.println(value);
			return value;
		}
		public String toString() {
			return "["+start+","+end+") +=" + stride;
		}
		public boolean intersectsWith(Match that, int width){
			int e = this.end - this.stride;
			int f = that.end - that.stride;
			return Line2D.linesIntersect(this.start % width, this.start / width, e % width, e / width, that.start % width, that.start / width, f % width, f / width) ;
//			return Utilities.isIntersectionBetweenSegments(this.getStartPoint(width), this.getEndPoint(width), that.getStartPoint(width), that.getEndPoint(width));
		}
		public double getIntersectionStrength(Match that, int width) {
			return Utilities.getSegmentIntersectionStrength(this.getStartPoint(width), this.getEndPoint(width), that.getStartPoint(width), that.getEndPoint(width));
		}
		public Point getStartPoint(int width) {
			return new Point(start % width, start / width);
		}
		public Point getEndPoint(int width) {
			int e = this.end - this.stride;
			return new Point(e % width, e / width);
		}
	}
	private static class MatchHead/*<T>*/ implements Comparable<MatchHead> {
		private int width;
		private Match match;
		private double radius;
//		private int x;
//		private int y;
//		private int center;
		private int completeness = 0;
		private int count = 0;
		private Point sum = new Point(0,0);
		private List<Match> vertical = new LinkedList<>();
		private List<Match> horizontal = new LinkedList<>();
		private List<Match> diagonalPlus = new LinkedList<>();
		private List<Match> diagonalMinus = new LinkedList<>();
		public MatchHead(int width, Match match){
			this.width = width;
			setHead(match);
		}
		public Match getHead(){
			return match;
		}
		private void setHead(Match match){
			this.match = match;
			this.radius = match.getLength(width);
//			this.center = match.getCenter();
//			this.x = this.center % this.width;
//			this.y = this.center / this.width;
		}
		public Point getCenter() {
			return Utilities.scale(sum, 1.0 / count);
		}
//		public boolean overlaps(int c) {
//			return Math.sqrt(Math.pow((c % this.width) - x, 2) + Math.pow((c / this.width) - y, 2)) <= radius;
//		}
//		@SuppressWarnings({"unchecked"})
		public void addVertical(Match ... values) {
			addVerticals(Arrays.asList(values));
		}
		public void addVerticals(List<Match> values) {
			if(vertical.size() == 0 && values.size() > 0) {
				completeness++;
			}
			for (Match m : values) {
				sum = Utilities.add(sum,m.getCenter(width));
				vertical.add(m);
				count++;
			}
		}
		public void addHorizontal(Match ... values) {
			addHorizontals(Arrays.asList(values));
		}
		public void addHorizontals(List<Match> values) {
			if(horizontal.size() == 0 && values.size() > 0) {
				completeness++;
			}
			for (Match m : values) {
				sum = Utilities.add(sum,m.getCenter(width));
				horizontal.add(m);
				count++;
			}
		}
		public void addDiagonalPlus(Match ... values) {
			addDiagonalPlusses(Arrays.asList(values));
		}
		public void addDiagonalPlusses(List<Match> values) {
			if(diagonalPlus.size() == 0 && values.size() > 0) {
				completeness++;
			}
			for (Match m : values) {
				sum = Utilities.add(sum,m.getCenter(width));
				diagonalPlus.add(m);
				count++;
			}
		}
		public void addDiagonalMinus(Match ... values) {
			addDiagonalMinuses(Arrays.asList(values));
		}
		public void addDiagonalMinuses(List<Match> values) {
			if(diagonalMinus.size() == 0 && values.size() > 0) {
				completeness++;
			}
			for (Match m : values) {
				sum = Utilities.add(sum,m.getCenter(width));
				diagonalMinus.add(m);
				count++;
			}
		}
		public String toString(){
			return "<"+match+", "+getCount()+">";
		}
		public int compareTo(MatchHead that) {
//			int c = that.getTypeCount() - this.getTypeCount(); 
			return that.getTypeCount() * that.getCount() - this.getTypeCount() * this.getCount();//c == 0?this.getCount() - that.getCount():0;
		}
		public int getCount(){
			return count;
		}
		public int getTypeCount(){
			return completeness;
		}
		public List<Match> getAll() {
			List<Match> out = new ArrayList<>();
			out.addAll(vertical);
			out.addAll(horizontal);
			out.addAll(diagonalPlus);
			out.addAll(diagonalMinus);
			return out;
		}
		public boolean merge(MatchHead mh, double strength){
			if (mh == this) {
				return false;
			}
			Point dist = Utilities.subtract(this.getCenter(), mh.getCenter());
			double d = Utilities.dot(dist, dist) / (strength * strength);
			if(d < radius * radius && d < mh.radius * mh.radius) {
				addVerticals(mh.vertical);
				addHorizontals(mh.horizontal);
				addDiagonalPlusses(mh.diagonalPlus);
				addDiagonalMinuses(mh.diagonalMinus);
				return true;
			}
			return false;
		}
	}
/*
	private static class MatchPriority implements Comparable<MatchPriority> {
		public final double strength;
		public final Match match;
		public MatchPriority(final double strength, final Match match){
			this.strength = strength;
			this.match = match;
		}
		public MatchPriority(final Match match, final double strength){
			this.strength = strength;
			this.match = match;
		}
		public int compareTo(MatchPriority that) {
			if (this.strength > that.strength) {
				return 1;
			}
			if (this.strength < that.strength) {
				return -1;
			}
			return 0;
		}
	}
	private static class MatchGroup implements Comparable<MatchGroup> {
		public final Match vertical;
		public final Match horizontal;
		public final Match diagonalPlus;
		public final Match diagonalMinus;
		MatchGroup(final Match vertical, final Match horizontal, final Match diagonalPlus, final Match diagonalMinus) {
			this.vertical = vertical;
			this.horizontal = horizontal;
			this.diagonalPlus = diagonalPlus;
			this.diagonalMinus = diagonalMinus;
		}
		public String toString() {
			return Arrays.toString(Utilities.newGenericArray(vertical, horizontal, diagonalPlus, diagonalMinus));
		}
		public int getNonNullCount() {
			return (vertical!=null?1:0) + (horizontal!=null?1:0) + (diagonalPlus!=null?1:0) + (diagonalMinus!=null?1:0);
		}
		public int compareTo(MatchGroup that) {
			return this.getNonNullCount() - that.getNonNullCount();
		}
	}
*/
	private static final int START_COLOR = 0xFFFF0000;
	private static final int END_COLOR = 0xFFFF0000;
	private static final int CENTER_COLOR = 0xFF00FF00;
	private static final int RLE_COLOR = 0xFF0000FF;
	private static final int LINE_COLOR = 0xFFFADADD;
	public List<Item<BufferedImage>> process(BufferedImage input, SwingWorkerProtected<?, BufferedImage> swp) {
		int width = input.getWidth();
		int height = input.getHeight();

		BufferedImage prog = Utilities.convertImageToBufferedImage(input);
		swp.publish(prog);

		int[] data = input.getRGB(0, 0, width, height, new int[(width) * (height)], 0, width);
		int lower = getCenterColor(data);
		boolean [] bw = new boolean[data.length];
		for (int p = 0; p < data.length; p++) {
			if (bw[p] = (ARGBToLightness(data[p]) >= lower)) {
				setARGB(prog, p, 0xFFFFFFFF);
			} else {
				setARGB(prog, p, 0xFF000000);
			}
		}

		Collection<MatchHead> matchheads = dumbFinder(height, width, bw, prog, swp);
		
		swp.publish(Utilities.convertImageToBufferedImage(prog));

		System.out.println(matchheads);		
		System.out.println(matchheads.size());
		return null;
	}
/*	private static Collection<MatchHead> lazyFinder(int height, int width, boolean[] bw, BufferedImage prog, SwingWorkerProtected<?, BufferedImage> swp) {
		List<MatchHead> matchheads = new LinkedList<>();
		for (int y = 0, p = 0; y < height; y++) {
			swp.setProgress((y * 100) / height);
//			System.out.print("y");
			mLoop:
			for (Match m : process(bw, p, p+=width, 1, prog)) {//horizontal
//				System.out.print("m");
				int mc = m.getCenter();
				wLoop:
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
							setARGB(prog, a, LINE_COLOR);
						}
						zLoop:
						for (Match z : process(bw, zstart, zend, zstride, prog)) {//diagonal
							if((wc >= z.start) && (wc <= z.end)) {
//								System.out.print("z");
								int zc = z.getCenter();
								int sstride = width - 1;
								int sk = sstride - (zc % width);
								int sstart = zc + sk - sk * width;
								int send = Math.min(sstart + width * sstride, width * height);
								if (sstart < 0) {
									sstart = (sstart % sstride) + sstride;
								}
								for (int a = sstart; a < send; a+=sstride) {
									setARGB(prog, a, LINE_COLOR);
								}
								
								sLoop:
								for (Match s : process(bw, sstart, send, sstride, prog)) {//diagonal
									if((zc >= s.start) && (zc <= s.end)) {
										int sc = s.getCenter();
										setARGB(prog, m.start, START_COLOR);
										setARGB(prog, m.end - 1, END_COLOR);
										setARGB(prog, mc, CENTER_COLOR);
										setARGB(prog, w.start, START_COLOR);
										setARGB(prog, w.end - width, END_COLOR);
										setARGB(prog, wc, CENTER_COLOR);
										setARGB(prog, z.start, START_COLOR);
										setARGB(prog, z.end - zstride, END_COLOR);
										setARGB(prog, z.getCenter(), CENTER_COLOR);
										setARGB(prog, s.start, START_COLOR);
										setARGB(prog, s.end - sstride, END_COLOR);
										setARGB(prog, s.getCenter(), CENTER_COLOR);
										for(MatchHead mh : matchheads) {
											if (mh.overlaps(sc)) {
												//mh.add(new MatchGroup(m,w,z,s));
												//continue sLoop;
												continue mLoop;
											}
										}
										matchheads.add(new MatchHead(width, s, new MatchGroup(m,w,z,s)));
									}
								}
							}
						}
					}
				}
			}
		}
		return matchheads;
	}*/
	private static Collection<MatchHead> dumbFinder(int height, int width, boolean[] bw, BufferedImage prog, SwingWorkerProtected<?, BufferedImage> swp) {
//		try {
		LinkedList<MatchHead> matchheads = new LinkedList<MatchHead>();
		final double strength = 0.25;
		for (int y = 0, p = 0; y < height; y++) {
			next:
			for (Match m : process(bw, p, p+=width, 1, prog)) {
				for (int i = 0; i < matchheads.size(); i++) {
					MatchHead t = matchheads.get(i);
					if(t.match.getIntersectionStrength(m, width) < strength) {
						matchheads.remove(t);
						matchheads.add(0, t);
						t.addHorizontal(m);
						continue next;
					}
				}
				MatchHead u = new MatchHead(width, m);
				matchheads.add(u);
				u.addHorizontal(m);
			}
		}
		for (int x = 0; x < width; x++) {
			next:
			for (Match m : process(bw, x, width * height, width, prog)) {
				for (int i = 0; i < matchheads.size(); i++) {
					MatchHead t = matchheads.get(i);
					if(t.match.getIntersectionStrength(m, width) < strength) {
						matchheads.remove(t);
						matchheads.add(0, t);
						t.addVertical(m);
						continue next;
					}
				}
				MatchHead u = new MatchHead(width, m);
				matchheads.add(u);
				u.addVertical(m);
			}
		}
		for (int x = 0; x < width; x++) {
			int stride = width + 1;
			int start = x;
			int end = Math.min(width * height, (width - x) * width);
			if(x % 10 == 0){
//				drawLine(prog, start, end, stride, LINE_COLOR);
			}
			next:
			for (Match m : process(bw, start, end, stride, prog)) {
				for (int i = 0; i < matchheads.size(); i++) {
					MatchHead t = matchheads.get(i);
					if(t.match.getIntersectionStrength(m, width) < strength) {
						matchheads.remove(t);
						matchheads.add(0, t);
						t.addDiagonalPlus(m);
						continue next;
					}
				}
				MatchHead u = new MatchHead(width, m);
				matchheads.add(u);
				u.addDiagonalPlus(m);
			}
		}
		for (int y = 1; y < height; y++) {
			int stride = width + 1;
			int start = y * width;
			int end = Math.min(width * height, (y + width) * width);
			if(y % 10 == 0){
//				drawLine(prog, start, end, stride, LINE_COLOR);
			}
			next:
			for (Match m : process(bw, start, end, stride, prog)) {
				for (int i = 0; i < matchheads.size(); i++) {
					MatchHead t = matchheads.get(i);
					if(t.match.getIntersectionStrength(m, width) < strength) {
						matchheads.remove(t);
						matchheads.add(0, t);
						t.addDiagonalPlus(m);
						continue next;
					}
				}
				MatchHead u = new MatchHead(width, m);
				matchheads.add(u);
				u.addDiagonalPlus(m);
			}
		}
		for (int x = 0; x < width; x++) {
			int stride = width - 1;
			int start = x;
			int end = Math.min(width * height, (x + 1) * width);
			if(x % 10 == 0){
//				drawLine(prog, start, end, stride, LINE_COLOR);
			}
			next:
			for (Match m : process(bw, start, end, stride, prog)) {
				for (int i = 0; i < matchheads.size(); i++) {
					MatchHead t = matchheads.get(i);
					if(t.match.getIntersectionStrength(m, width) < strength) {
						matchheads.remove(t);
						matchheads.add(0, t);
						t.addDiagonalMinus(m);
						continue next;
					}
				}
				MatchHead u = new MatchHead(width, m);
				matchheads.add(u);
				u.addDiagonalMinus(m);
			}
		}
		for (int y = 1; y < height; y++) {
			int stride = width - 1;
			int start = y * width + stride;
			int end = Math.min(width * height, width * (y + stride));
			if(y % 10 == 0){
//				drawLine(prog, start, end, stride, LINE_COLOR);
			}
			next:
			for (Match m : process(bw, start, end, stride, prog)) {
				for (int i = 0; i < matchheads.size(); i++) {
					MatchHead t = matchheads.get(i);
					if(t.match.getIntersectionStrength(m, width) < strength) {
						matchheads.remove(t);
						matchheads.add(0, t);
						t.addDiagonalPlus(m);
						continue next;
					}
				}
				MatchHead u = new MatchHead(width, m);
				matchheads.add(u);
				u.addDiagonalMinus(m);
			}
		}
//		System.out.println(matchheads);
		LinkedList<MatchHead> good = new LinkedList<MatchHead>();
		for (int i = 0; i < matchheads.size(); i++) {
			MatchHead m = matchheads.get(i);
			for (int j = i + 1; j < matchheads.size(); ) {
				if (m.merge(matchheads.get(j), strength)){
					matchheads.remove(j);
					continue;
				}
				j++;
			}
		}
		for (MatchHead m : matchheads) {
			if(m != null && m.getTypeCount() > 2) {
				good.add(m);
				for(Match u : m.getAll()) {
					drawLine(prog, u.start, u.end, u.stride, LINE_COLOR);
				}
			}
		}
		for (MatchHead m : good) {
			for(Match u : m.getAll()) {
				setARGB(prog, u.getStartPoint(width), START_COLOR);
				setARGB(prog, u.getEndPoint(width), END_COLOR);
				setARGB(prog, u.getCenter(), CENTER_COLOR);
			}
		}
		Collections.sort(good);
		return good;//new PriorityQueue<MatchHead>(matchheads);

//		}catch(Exception e){ e.printStackTrace();return null;}
		
	}
	private static void drawLine(BufferedImage prog, int start, int end, int stride, int color) {
		for (int a = start; a < end; a+=stride) {
			setARGB(prog, a, color);
		}
	}
	/*
	private static Collection<MatchHead> smartFinder(int height, int width, boolean[] bw, BufferedImage prog, SwingWorkerProtected<?, BufferedImage> swp) {
		List<MatchHead> matchheads = new LinkedList<>();
		//*
		//IDEA it must be found in 3 of the 4 scans.
		List<Match> horizontal = new LinkedList<>();
		List<Match> vertical = new LinkedList<>();
		List<Match> diagonalPlus = new LinkedList<>();
		List<Match> diagonalMinus = new LinkedList<>();
		for (int y = 0, p = 0; y < height; y++) {
			horizontal.addAll(process(bw, p, p+=width, 1, prog));
		}
		for (int x = 0; x < width; x++) {
			vertical.addAll(process(bw, x, width * height, width, prog));
		}
		for (int x = 0; x < width; x++) {
			int stride = width + 1;
			int start = x;
			int end = Math.min(width * height, (width - x) * width);
			if(x % 10 == 0){
				for (int a = start; a < end; a+=stride) {
					setARGB(prog, a, LINE_COLOR);
				}
			}
			diagonalPlus.addAll(process(bw, start, end, stride, prog));
		}
		for (int y = 1; y < height; y++) {
			int stride = width + 1;
			int start = y * width;
			int end = Math.min(width * height, (y + width) * width);
			if(y % 10 == 0){
				for (int a = start; a < end; a+=stride) {
					setARGB(prog, a, LINE_COLOR);
				}
			}
			diagonalPlus.addAll(process(bw, start, end, stride, prog));
		}
		for (int x = 0; x < width; x++) {
			int stride = width - 1;
			int start = x;
			int end = Math.min(width * height, (x + 1) * width);
			if(x % 10 == 0){
				for (int a = start; a < end; a+=stride) {
					setARGB(prog, a, LINE_COLOR);
				}
			}
			diagonalMinus.addAll(process(bw, start, end, stride, prog));
		}
		for (int y = 1; y < height; y++) {
			int stride = width - 1;
			int start = y * width + stride;
			int end = Math.min(width * height, width * (y + stride));
			if(y % 10 == 0){
				for (int a = start; a < end; a+=stride) {
					setARGB(prog, a, LINE_COLOR);
				}
			}
			diagonalMinus.addAll(process(bw, start, end, stride, prog));
		}
		
		for (Match h: horizontal){
			int misses = 0;
			Match k = h;
			Match v = null;
			Match p = null;
			Match m = null;
			Queue<MatchPriority> hq = new PriorityQueue<MatchPriority>();
			for (Match t : vertical) {
				double strength = k.getIntersectionStrength(t, width);
				if (strength <= 1) {
					hq.add(new MatchPriority(t, strength));
				}
			}
			if(hq.isEmpty()) {
				misses++;
			} else {
				v = k = hq.peek().match;
			}
			Queue<MatchPriority> pq = new PriorityQueue<MatchPriority>();
			for (Match t : diagonalPlus) {//horizontal
				double strength = k.getIntersectionStrength(t, width);
				if (strength <= 1) {
					pq.add(new MatchPriority(t, strength));
				}
			}
			if(pq.isEmpty()) {
				misses++;
			} else {
				p = k = pq.peek().match;
			}
			Queue<MatchPriority> mq = new PriorityQueue<MatchPriority>();
			for (Match t : diagonalMinus) {//horizontal
				double strength = k.getIntersectionStrength(t, width);
				if (strength <= 1) {
					mq.add(new MatchPriority(t, strength));
				}
			}
			if(mq.isEmpty()) {
				misses++;
			} else {
				m = k = mq.peek().match;
			}
			if (misses < 2) {
				matchheads.add(new MatchHead(width, k, new MatchGroup(h,v,p,m)));
				for (Match u : Arrays.asList(h,v,p,m)) {
					if (u != null) {
						setARGB(prog, u.getStartPoint(width), START_COLOR);
						setARGB(prog, u.getEndPoint(width), END_COLOR);
						setARGB(prog, u.getCenter(), CENTER_COLOR);
					}
				}
			}
		}
		return matchheads;
	}*/
	private static int getCenterColor(int [] data) {
		int[] distribution = new int[256];
		Arrays.fill(distribution, 0);

		try {
		for (int p = 0; p < data.length; p++) {
			distribution[ARGBToLightness(data[p])]++;
		}
		} catch (Exception e){
			e.printStackTrace();
		}
		
		int lower = 0;
		int upper = 256;
		int lc = 0;
		int uc = 0;
//*
		for (int p = 0; p < 128; p++) {
			if(lc < distribution[p]) {
				lc = distribution[p];
				lower = p;
			}
		}
		for (int p = 128; p < 256; p++) {
			if(uc < distribution[p]) {
				uc = distribution[p];
				upper = p;
			}
		}
	
//		return upper;
		return ((upper * 1) + (lower * 1)) / 2;

		/*/
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
		//*/
	}
	private static void setARGB(BufferedImage image, int p, int argb) {
		int w = image.getWidth();
		int x = p % w; 
		int y = p / w;
		image.setRGB(x,y,argb);
	}
	private static void setARGB(BufferedImage image, Point p, int argb) {
		image.setRGB(p.x, p.y, argb);
	}
	private static int ARGBToLightness(int argb) {
//		int a = (rgba >> 24) & 0xFF;
		int r = (argb >> 16) & 0xFF;
		int g = (argb >> 8) & 0xFF;
		int b = (argb >> 0) & 0xFF;
		return (int)((0.2126*b) + (0.7152*g) + (0.0722*b));
//		int min = Math.min(Math.min(r,g), b);
//		int max = Math.max(Math.max(r,g), b);
//		return (min + max) / 2;
	}
	private static List<Integer> runLengthEncode(final boolean [] bw, final int start /*inclusive*/, final int end /*exclusive*/, int stride, BufferedImage prog) {
		List<Integer> rle = new LinkedList<Integer>();
		int c = stride;
		boolean b = bw[start];
		for (int p = start + stride; p < end; p+= stride) {
			if (bw[p] != b) {
				setARGB(prog, p, RLE_COLOR);
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
	public String getName() {
		return "QRCode Reader";
	}
	public void reset() {
	}
}