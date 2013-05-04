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
	private static Point newPoint(int p, int width) {
		return new Point(p % width, p / width);
	}
	private static class Match {
		public final int start;
		public final int start_end;
		public final int center_start;
		public final int center_end;
		public final int end_start;
		public final int end;
		public final int stride;
		public Match(final int start, final int start_end, final int center_start, final int center_end, final int end_start, final int end, final int stride) {
			this.start = start;
			this.start_end = start_end;
			this.center_start = center_start;
			this.center_end = center_end;
			this.end_start = end_start;
			this.end = end;
			this.stride = stride;
		}/*
		public int getCenter() {
			//If stride > 1 and the number of strides is odd, you can't just average the two numbers as that would put you between strides!
			//end is exclusive <strike>so we have to subtract one strides width</strike>
			return start + (((end - start - stride) / (stride * 2)) * stride);
		}*/
		public Point getLinearCenter(int width) {
			return newPoint(start + (((end - start - stride) / (stride * 2)) * stride), width);
		}
		public double getLength(int width) {
			int x = (end % width) - (start % width);
			int y = (end / width) - (start / width);
			double value = Math.sqrt(x * x + y * y);
			//System.out.println(value);
			return value;
		}
		public int getScanLength() {
			return (end / stride) - (start / stride);
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
		private class MatchSorter implements Comparable<MatchSorter> {
			public final double distance;
			public final Match match;
			public MatchSorter(final double distance, final Match match) {
				this.distance = distance;
				this.match = match;
			}
			public int compareTo(MatchHead that) {
				return Math.signum(that.distance - this.distance);
			}
		}
		private static MatchHeadLight {
			public final double distance;
			public final Match match;
			private Point sum = new Point(0,0);
			public List<Match> matches = new LinkedList<Match>();
			public MatchHeadLight(Match match, double distance) {
				this.distance = distance;
				this.match = match;
				this.matches.add(match);
			}
			public getCenter(){
				return Utilities.scale(sum, 1.0 / matches.size());
			}
			public add(Match m){
				matches.add(m);
				sum = Utilities.add(sum, m.getCenter());
			}
		}
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
//		@SuppressWarnings({"unchecked"})
		public List<Line2D> getLines() {
			List<Line2D> lines = new List<Line2D>();
			List<MatchSorter> matches = ArrayList<>();
			Point center = getCenter();
			for (Match m : getAll()) {
				matches.add(new MatchSorter(Utilities.getDistance(center, m.getCenter()), m));
			}
			Collections.sort(matches);
			
			//1) sort Matches by distance of center from MatchHead center.
			//2) chew them off and group them by 1/7 their scale
			//3) Get 5 groups. Discard the 5th.
			//4) Take the 4 groups and turn them into line segments that intersect and hopefully traverse the center.
			//X) if not even points exist try to figure it out.
			List<MatchHeadLight> groups = new LinkedList<>();
			for (MatchSorter m : matches) {
				for (int i = 0;  i < groups.size(); i++) {
					MatchHeadLight g = groups.get(i);
					if(Utilities.distance(g.match.getCenter(), m.match.getCenter()) < (m.distance / 2)) {
						g.add(m);
						groups.remove(i);
						groups.add(i);
						break;
					}
				}
				if (groups.size() == 4) {
					break;
				}
				groups.add(new MatchHeadLight(m), m.distance);
			}
			switch (groups.size()) {
				case 4: {
					double max = 0;
					int a = 0, b = 0;
					for(int i = 0; i < groups.size(); i++) {
						for(int j = i + 1; j < groups.size(); j++) {
							double d = Utilities.distance(groups.get(i).getCenter(), groups.get(i).getCenter());
							if(d > max) {
								a = i;
								b = j;
								max = d;
							}
						}
					}
					lines.add(new Line2D.Float(groups.get(a),groups.get(b)));
					groups.remove(a);
					groups.remove(b);
					lines.add(new Line2D.Float(groups.get(0),groups.get(1)));
					break;	
				}
				case 3:
					//MH center is not real center.
					//Two furthest points likely form a line unless there is large amounts off scew.
					//TODO write me!
				case 2:
					//MH center is not real center.
					//If the center lies between the points, you have one line.
					//If the center does not lie between the points you have two lines.
					//TODO write me!
				case 1:
					//So Screwed.
				default:
					break;
			}
			return lines;
		}
		public void addVertical(Match ... values) {
			addVerticals(Arrays.asList(values));
		}
		public void addVerticals(List<Match> values) {
			if(vertical.size() == 0 && values.size() > 0) {
				completeness++;
			}
			for (Match m : values) {
				sum = Utilities.add(sum,m.getLinearCenter(width));
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
				sum = Utilities.add(sum,m.getLinearCenter(width));
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
				sum = Utilities.add(sum,m.getLinearCenter(width));
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
				sum = Utilities.add(sum,m.getLinearCenter(width));
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
		for (MatchHead matchhead : matchheads){
//			List<Point> centersOfInterest 
		}
		
		
		swp.publish(Utilities.convertImageToBufferedImage(prog));

		System.out.println(matchheads);		
		System.out.println(matchheads.size());
		return null;
	}
	private static Collection<MatchHead> dumbFinder(int height, int width, boolean[] bw, BufferedImage prog, SwingWorkerProtected<?, BufferedImage> swp) {
//		try {
		LinkedList<MatchHead> matchheads = new LinkedList<MatchHead>();
		final double strength = 0.25;
		for (int y = 0, p = 0; y < height; y++) {//addHorizontal
			next:
			for (Match m : process(bw, p, p+=width, 1, prog)) {
				//first iteration can intersect with nothing they are all parallel
				/*
				for (int i = 0; i < matchheads.size(); i++) {
					MatchHead t = matchheads.get(i);
					if((bw[t.match.start] == bw[m.start]) && (t.match.getIntersectionStrength(m, width) < strength)) {
						matchheads.remove(t);
						matchheads.add(0, t);
						t.addHorizontal(m);
						continue next;
					}
				}*/
				MatchHead u = new MatchHead(width, m);
				matchheads.add(u);
				u.addHorizontal(m);
			}
		}
		for (int x = 0; x < width; x++) {//addVertical
			next:
			for (Match m : process(bw, x, width * height, width, prog)) {
				for (int i = 0; i < matchheads.size(); i++) {
					MatchHead t = matchheads.get(i);
					if((bw[t.match.start] == bw[m.start]) && (t.match.getIntersectionStrength(m, width) < strength)) {
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
		for (int x = 0; x < width; x++) {//addDiagonalPlus
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
					if((bw[t.match.start] == bw[m.start]) && (t.match.getIntersectionStrength(m, width) < strength)) {
						matchheads.remove(t);
						matchheads.add(0, t);
						t.addDiagonalPlus(m);
						continue next;
					}
				}
				//Shouldn't add new heads at this point since they will only be orphaned.
				MatchHead u = new MatchHead(width, m);
				matchheads.add(u);
				u.addDiagonalPlus(m);
			}
		}
		for (int y = 1; y < height; y++) {//addDiagonalPlus
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
					if((bw[t.match.start] == bw[m.start]) && (t.match.getIntersectionStrength(m, width) < strength)) {
						matchheads.remove(t);
						matchheads.add(0, t);
						t.addDiagonalPlus(m);
						continue next;
					}
				}
				//Shouldn't add new heads at this point since they will only be orphaned.
				MatchHead u = new MatchHead(width, m);
				matchheads.add(u);
				u.addDiagonalPlus(m);
			}
		}
		for (int x = 0; x < width; x++) {//addDiagonalMinus
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
					if((bw[t.match.start] == bw[m.start]) && (t.match.getIntersectionStrength(m, width) < strength)) {
						matchheads.remove(t);
						matchheads.add(0, t);
						t.addDiagonalMinus(m);
						continue next;
					}
				}
				//Shouldn't add new heads at this point since they will only be orphaned.
				MatchHead u = new MatchHead(width, m);
				matchheads.add(u);
				u.addDiagonalMinus(m);
			}
		}
		for (int y = 1; y < height; y++) {//addDiagonalMinus
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
					if((bw[t.match.start] == bw[m.start]) && (t.match.getIntersectionStrength(m, width) < strength)) {
						matchheads.remove(t);
						matchheads.add(0, t);
						t.addDiagonalMinus(m);
						continue next;
					}
				}
				//Shouldn't add new heads at this point since they will only be orphaned.
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
				MatchHead t = matchheads.get(j);
				if (bw[t.match.start] == bw[m.match.start]){
					if (m.merge(t, strength)) {
						matchheads.remove(j);
						continue;
					}
				}
				j++;
			}
		}
		for (MatchHead m : matchheads) {
			if((m.getTypeCount() >= 3) && (m.getCount() >= 5)) {
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
				setARGB(prog, u.getLinearCenter(width), CENTER_COLOR);
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
					int e = start + x;
					matches.add(new Match(e - total, e - v3 - v2 - v1 - v0, e - v2 - v1 - v0, e - v1 - v0, e - v0, e, stride));
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