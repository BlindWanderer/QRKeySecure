package edu.mccc.cos210.qrks.qrcode;
import java.util.*;

public final class Version {
	private Version() throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}
	private static int[][] alignmentLocations = {
		{},//1 ~ 0
		{18},//2 ~ 1
		{22},
		{26},
		{30},
		{34},
		{6, 22, 38},//7 ~ 6
		{6, 24, 42},
		{6, 26, 46},
		{6, 28, 50},
		{6, 30, 54},
		{6, 32, 58},
		{6, 34, 62},
		{6, 26, 46, 66},//14 ~ 13
		{6, 26, 48, 70},
		{6, 26, 50, 74},
		{6, 30, 54, 78},
		{6, 30, 56, 82},
		{6, 30, 58, 86},
		{6, 34, 62, 90},
		{6, 28, 50, 72, 94},//21 ~ 22
		{6, 26, 50, 74, 98},
		{6, 30, 54, 78, 102},
		{6, 28, 54, 80, 106},
		{6, 32, 58, 84, 110},
		{6, 30, 58, 86, 114},
		{6, 34, 62, 90, 118},
		{6, 26, 50, 74, 98, 122},//28 ~ 33
		{6, 30, 54, 78, 102, 126},
		{6, 26, 52, 78, 104, 130},
		{6, 30, 56, 82, 108, 134},
		{6, 34, 60, 86, 112, 138},
		{6, 30, 58, 86, 114, 142},
		{6, 34, 62, 90, 118, 146},
		{6, 30, 54, 78, 102, 126, 150},//35 ~ 46
		{6, 24, 50, 76, 102, 128, 154},
		{6, 28, 54, 80, 106, 132, 158},
		{6, 32, 58, 84, 110, 136, 162},
		{6, 26, 54, 82, 110, 138, 166},
		{6, 30, 58, 86, 114, 142, 170},//40 ~ 46
	};
	public static int getSize(int version) {
		if (version < 1 || version > 40) {
			throw new IllegalArgumentException("version is not in range [1,40]");
		}
		return version * 4 + 17;
	}
	public static boolean [][] generateMask(int version, boolean fill) {
		int d = getSize(version);
		boolean [][] mask = new boolean[d][d];
		if (fill) {//by default java fills boolean arrays with false
			for (boolean[] roc : mask) {
				Arrays.fill(roc, fill);
			}
		}
		return mask;
	}
	public static boolean [][] getDataMask(int version) {
		boolean [][] mask = generateMask(version, true);
		for (Point p: getFindingPatternLocations(version)) {
			clearSquare(mask, Constants.FINDING_PATTERN_SIZE, p.x, p.y);
		}
		for (Point p: getAlignmentPatternLocations(version)) {
			clearSquare(mask, Constants.ALIGNMENT_PATTERN_SIZE, p.x + Constants.ALIGNMENT_PATTERN_OFFSET, p.y + Constants.ALIGNMENT_PATTERN_OFFSET);
		}
		//TODO: block out meta data areas and timing strips
		return mask;
	}
	/*public static List<boolean [][]> getMasks(int version) {
		List<boolean [][]> masks = ArrayList<>(9);
		boolean [][] mask = getDataMask(version);
		int d = mask.length;
		for (int i = 0; i < 8; i++) {
			boolean [][] m = new boolean [d][d];
			m = 
		}
		return masks;
	}*/
	public static Point[] getFindingPatternLocations(int version) {
		int d = getSize(version);
		Point [] blah = {new Point(0,0), new Point(0,d - Constants.FINDING_PATTERN_SIZE), new Point(d - Constants.FINDING_PATTERN_SIZE,0)};
		return blah;
	}
	public static Point[] getAlignmentPatternLocations(int version) {
		int [] positions = getAlignmentCompressedLocations(version);
		int w = positions.length;//2 + (version / 7)
		if (w == 0) {
			return new Point[0];
		}
		if (w == 1) {
			Point [] blah = {new Point(positions[0], positions[0])};
			return blah;
		}
		int [] skips = {0, w - 1, w * w - w, -1};//Do not overlap with Finding Patterns
		Point [] ret = new Point[w * w - skips.length + 1];
		for (int i = 0, ij = 0, skip = 0, count = 0; i < w; i++) {
			for (int j = 0; j < w; j++, ij++) {
				if (skips[skip] != ij) {
					ret[count++] = new Point(positions[i], positions[j]);
				} else {
					skip++;
				}
			}
		}
		return ret;
	}
	private static void clearSquare(boolean [][] mask, int size, int oi, int oj){
		for (int i = 0; i < size; i++) {
			boolean[] roc = mask[i + oi];
			for (int j = oj, ej = size + oj; j < ej; j++) {
				roc[j] = false;
			}
		}
	}
	private static int[] getAlignmentCompressedLocations(int version) {
		if (version < 1 || version > 40) {
			throw new IllegalArgumentException("version is not in range [1,40]");
		}
		return alignmentLocations[version - 1];
	}
}