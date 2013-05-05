//NOTES:
//Use center on MatchHead to associate Matches better.
//Consider a post processing step to merge MatchHeads.
//Histagram of edges only, current method doesn't really work.
//Also it shouldn't pick colors in the middle of a gradiant.

package edu.mccc.cos210.qrks;
import edu.mccc.cos210.qrks.util.*;
import java.util.*;
import javax.swing.*;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.filechooser.*;
import java.io.*;
import javax.imageio.*;
import java.awt.Point;
import java.awt.geom.*;

public class QRReader implements Reader<BufferedImage, BufferedImage> {
	private static class MatchPointDistanceComparator implements Comparator<MatchPoint>{
		public Point center = null;
		public int compare(MatchPoint o1, MatchPoint o2) {
			if (o1 == null) 
				return -1;
			if (o2 == null)
				return 1;
			return -(int)Math.signum(Utilities.distance(o1.center, center) - Utilities.distance(o2.center, center));
		}
		public MatchPointDistanceComparator setCenter(Point p) {
			center = p;
			return this;
		}
	}
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
		private class MatchingLine2D extends Line2D.Float{
			private static final long serialVersionUID = 1L;
			public final Match start;
			public final Match end;
			public MatchingLine2D(Match start, Match end) {
				super(start.getLinearCenter(width), end.getLinearCenter(width));
				this.start = start;
				this.end = end;
			}
		}
		private class MatchSorter implements Comparable<MatchSorter> {
			public final double distance;
			public final Match match;
			public MatchSorter(final double distance, final Match match) {
				this.distance = distance;
				this.match = match;
			}
			public int compareTo(MatchSorter that) {
				return (int)Math.signum(that.distance - this.distance);
			}
		}
		private class MatchHeadLight {
			public final double distance;
			public final Match match;
			private Point sum = new Point(0,0);
			public List<Match> matches = new LinkedList<Match>();
			public MatchHeadLight(Match match, double distance) {
				this.distance = distance;
				this.match = match;
				this.matches.add(match);
			}
			public Point getCenter(){
				return Utilities.scale(sum, 1.0 / matches.size());
			}
			public void add(Match m){
				matches.add(m);
				sum = Utilities.add(sum, m.getLinearCenter(width));
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
		private static int getTotal(List<Match> list) {
			int length = 0;
			for (Match m : list) {
				length += m.getScanLength();
			}
			return length;
		}
		private double getCertainty() {
			int v = getTotal(vertical);
			int h = getTotal(horizontal);
			int p = getTotal(diagonalPlus);
			int m = getTotal(diagonalMinus);
			int vh = v - h;
			int hv = v + h;
			int pm = p - m;
			int mp = p + m;
			int hhvv = hv * hv;
			int mmpp = mp * mp;
			return 1.0 - ((vh * vh * mmpp + pm * pm * hhvv) / (2.0 * hhvv * mmpp));
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
		public List<MatchingLine2D> getFindingPattern() {
			List<MatchingLine2D> lines = new ArrayList<MatchingLine2D>(2);
			List<Match> all = getAll();
			List<MatchSorter> matches = new ArrayList<>(all.size());
			Point center = getCenter();
			for (Match m : all) {
				matches.add(new MatchSorter(Utilities.distance(center, m.getLinearCenter(width)), m));
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
					if(Utilities.distance(g.match.getLinearCenter(width), m.match.getLinearCenter(width)) < (m.distance / 2)) {
						g.add(m.match);
						groups.remove(i);
						groups.add(g);
						break;
					}
				}
				if (groups.size() == 4) {
					break;
				}
				groups.add(new MatchHeadLight(m.match, m.distance));
			}
			switch (groups.size()) {
				case 4: {
					double max = 0;
					int a = 0, b = 0;
					for(int i = 0; i < groups.size(); i++) {
						for(int j = i + 1; j < groups.size(); j++) {
							double d = Utilities.distance(groups.get(i).match.getLinearCenter(width), groups.get(j).match.getLinearCenter(width));
							if(d > max) {
								a = Math.max(i, j);
								b = Math.min(i, j);
								max = d;
							}
						}
					}
					//TODO this is quite neive.
					lines.add(new MatchingLine2D(groups.get(a).match, groups.get(b).match));
					groups.remove(a);
					groups.remove(b);
					lines.add(new MatchingLine2D(groups.get(0).match, groups.get(1).match));
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

	private class MatchPoint implements Comparable<MatchPoint>{
		public int compareTo(MatchPoint o) {
			if (o == null)
				return 1;
			return -(int)Math.signum(getPossiblesWeight() - o.getPossiblesWeight());
		}
		public double getPossiblesWeight() {
			double total = 0;
			for (Monkey m : mh.possibles) {
				total += m.hits / m.scanSize;
			}
		}
		final int width;
		final int height;
		final int symetric_width;
		final int symetric_height;
		final int symetric_x;
		final int symetric_y;
		final MatchHead mh;
		final List<Match> matches;
		final List<Point> points;
		final Point center;
		final List<Monkey> possibles = new LinkList<Monkey>();
		public MatchPoint(MatchHead mh, int width, int height) {
			int x_min = width;
			int x_max = 0;
			int y_min = height;
			int y_max = 0;
			Point center = mh.getCenter();
			this.mh = mh;
			this.matches = mh.getAll();
			this.points = new ArrayList<Point>(matches.size());
			for (Match match : matches) {
				Point mc = match.getLinearCenter(width);
				this.points.add(mc);
				x_min = Math.min(x_min, mc.x);
				x_max = Math.max(x_max, mc.x);
				y_min = Math.min(y_min, mc.y);
				y_max = Math.max(y_max, mc.y);
			}
			this.width = x_max - x_min + 1;
			this.height = y_max - y_min + 1;
			//TODO get this code working
			/* - I don't know why this isn't working. It's impossible for the score to always be zero
			//I just figured it out: It needs to be rotated about the center point, not by how it is placed in the matrix.
			int shift_x = 0;
			int shift_y = 0;
			int high_score = 0;
			int pokes = 0;
			{
				int start = -3;
				int end = -start;
//			System.out.println("!1");
				for (int p = start; p <= end; p++){
//			System.out.println("!2");
					for (int q = start; q <= end; q++){
						int score = 0;
						boolean [][] mask = new boolean[width + Math.abs(p) * 2][height + Math.abs(q) * 2];
						for (Point point : points) {
							int x = (point.x - x_min) + (Math.max(0, p) * 2);
							int y = (point.y - y_min) + (Math.max(0, q) * 2);
							mask[x][y] = true;
							pokes++;
						}
						for (Point point : points) {
							int x = (point.x - x_min) + (Math.max(0, -p) * 2);
							int y = (point.y - y_min) + (Math.max(0, -q) * 2);
							if (mask[mask.length - 1 - x][mask[0].length - 1 - y]){
								score++;
							}
						}
						if(score > high_score) {
							high_score = score;
							shift_x = p;
							shift_y = q;
						}
					}
				}
			}
			System.out.println(Utilities.subtract(center, this.center) + " ~ " +high_score + " ~ " + pokes);
			//*/
			this.center = center;
			this.symetric_x = Math.max(x_max - center.x, center.x - x_min);
			this.symetric_y = Math.max(y_max - center.y, center.y - y_min);
			this.symetric_width = 2 * symetric_x + 1;
			this.symetric_height = 2 * symetric_y + 1;
		}
		public double getDistanceFrom(Point p){
			return Utilities.distance(p, center);
		}
		public String toString() {
			return "<"+center+", "+this.points.size()+">";
		}
	}
	private class Monkey {//I'm running out of names for classes
		public int scanSize;
		public int hits;
		public final MatchPoint matchpoint;
		Monkey(MatchPoint mp, int scanSize, int hits) {
			this.matchpoint = mp;
			this.scanSize = scanSize;
			this.hits = hits;
		}
	}
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
		try{
		//Graphics2D g = prog.createGraphics();

		MatchPointDistanceComparator compares = new MatchPointDistanceComparator();
		
		List<MatchPoint> mps = new ArrayList<>(matchheads.size());
		List<MatchPoint> work = new ArrayList<>(matchheads.size());
		//Graphics2D g = prog.createGraphics()
		for (MatchHead mh : matchheads) {
			mps.add(new MatchPoint(mh, width, height));
		}
		
		for (int j = 0; j < mps.size(); j++){
			MatchPoint mh = mps.get(j);
			work = new ArrayList<>(mps);
			work.set(j, null);
			Collections.sort(work, compares.setCenter(mh.center));
			
			BufferedImage field = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = field.createGraphics();
			g.setColor(new Color(0x00000000, true));
			g.fillRect(0,0, width, height);
			
			BufferedImage mini = new BufferedImage(mh.symetric_width, mh.symetric_height, BufferedImage.TYPE_INT_ARGB);
//			System.out.println(mini);
			Graphics2D mg = mini.createGraphics();
			mg.setColor(new Color(0x00000000, true));
			mg.fillRect(0,0, mh.symetric_width, mh.symetric_height);
			mg.dispose();
			for (Point point : mh.points) {
				int x = point.x - mh.center.x + mh.symetric_x;
				int y = point.y - mh.center.y + mh.symetric_y;
				mini.setRGB(x, y, 0x8000FFFF);
				//TODO: symbols should be symetric, we can use this to denoise this.
				mini.setRGB(mh.symetric_width - 1 - x, mh.symetric_height - 1 - y, 0x8000FFFF);
			}
			mini.setRGB(mh.symetric_x, mh.symetric_height, 0x00000000);//no center
			
			for (int i = 3, q = 2 * width / mh.width; i < q; i = (int)(i * 1.5)) {//this should be smarter
				g.drawImage(mini,
								-mh.symetric_x * i + mh.center.x + i / 2,
								-mh.symetric_y * i + mh.center.y + i / 2, 
								mh.symetric_x * i + mh.center.x + i / 2,
								mh.symetric_y * i + mh.center.y + i / 2, 0, 0, mh.symetric_width, mh.symetric_height, null);
				
//				swp.publish(field);
				{
					BufferedImage display = Utilities.convertImageToBufferedImage(prog);
					Graphics2D blah = display.createGraphics();
					blah.drawImage(field, 0, 0, width, height, 0, 0, width, height, null);
					blah.dispose();
					swp.publish(display);
				}
				System.out.println(i + " - " + mh.possibles);
//				Thread.sleep(1000);
				for (int k = 0; k < work.size(); k++) {
					MatchPoint mp = work.get(k);
					if (mp != null) {
						int hits = 0;
						for (Point point : mp.points) {
							if(field.getRGB(point.x, point.y) != 0x00000000) {
								hits++;
							}
						}
						if (hits > 0) {
							if (hits > Math.min(i, mp.matches.size() * 0.75)) {
								mh.possibles.add(new Monkey(mp, i, hits));
								work.set(k, null);
							}
						}
						Point a = Utilities.subtract(mp.center, mh.center);
						if ((Math.abs(a.x) / (double)mh.symetric_x > i) || Math.abs(a.y) / (double)mh.symetric_y > i) {
							break;
						}
					}
				}
			}
			for (Monkey m : mh.possibles) {
				
			}
			//Tasks:
			//1) Test found patters to see if they make sense. That is, try to read the complete finding patter.
			//2) Decode!
			//3) 
			
			
			
			g.dispose();
			//matrix[compares.center.x][compares.center.y] = null;
		}
		//g.dispose();
		} catch (Exception e) {e.printStackTrace();}
		swp.publish(prog);
		//swp.publish(Utilities.convertImageToBufferedImage(prog));

		System.out.println(matchheads);		
		System.out.println(matchheads.size());
		return null;
	}
	private static Collection<MatchHead> dumbFinder(int height, int width, boolean[] bw, BufferedImage prog, SwingWorkerProtected<?, BufferedImage> swp) {
//		try {
		LinkedList<MatchHead> matchheads = new LinkedList<MatchHead>();
		final double strength = 0.25;
		final double certainty = 0.75;
		LinkedList<MatchHead> newmatchheads = new LinkedList<MatchHead>();
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
				newmatchheads.add(u);
				u.addHorizontal(m);
			}
		}
		swp.publish(prog);
		matchheads.addAll(newmatchheads);
		newmatchheads.clear();
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
				newmatchheads.add(u);
				u.addVertical(m);
			}
		}
		swp.publish(prog);
		matchheads.addAll(newmatchheads);
		newmatchheads.clear();
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
				newmatchheads.add(u);
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
				newmatchheads.add(u);
				u.addDiagonalPlus(m);
			}
		}
		swp.publish(prog);
		matchheads.addAll(newmatchheads);
		newmatchheads.clear();
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
				newmatchheads.add(u);
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
				newmatchheads.add(u);
				u.addDiagonalMinus(m);
			}
		}
		swp.publish(prog);
		matchheads.addAll(newmatchheads);
		newmatchheads.clear();
		
		BufferedImage first = Utilities.convertImageToBufferedImage(prog);
		for (MatchHead mh : matchheads){
			for (Match u : mh.getAll()) {
				drawLine(first, u.start, u.end, u.stride, LINE_COLOR);
			}
		}
		for (MatchHead mh : matchheads){
			for (Match u : mh.getAll()) {
				setARGB(first, u.getStartPoint(width), START_COLOR);
				setARGB(first, u.getEndPoint(width), END_COLOR);
				setARGB(first, u.getLinearCenter(width), CENTER_COLOR);
			}
		}
		swp.publish(first);
		
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
			if((m.getTypeCount() >= 3) && (m.getCount() >= 5) && m.getCertainty() > certainty) {
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
		swp.publish(prog);
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
		//try {
		for (int p = 0; p < data.length; p++) {
			distribution[ARGBToLightness(data[p])]++;
		}
		// } catch (Exception e){ e.printStackTrace(); }
		
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
	public String getName() { return "QRCode Reader"; }
	public void reset() { }
}