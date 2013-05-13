package edu.mccc.cos210.qrks.qrcode;
import java.util.*;
import java.awt.Point;

/**
 *  
 * Version: an encoding of tables from QRCode specs
 * Provides alignment pattern locations, finding pattern locations, and dataMasks for each Version
 *
 */
public final class Version {
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
	/**
	 * Determines dimensions for a particular version.
	 * @param version: Version number of this QRCode
	 * @return integer horizontal / vertical dimension (in unit modules) for a QRCode of  particular version
	 */
	public static int getSize(int version) {
		if (version < 1 || version > 40) {
			throw new IllegalArgumentException("version is not in range [1,40]");
		}
		return version * 4 + 17;
	}
	/**
	 * Determines Version based on Size ( in unit modules)
	 * @param size of a QRCode
	 * @return Version number
	 */
	public static int getClosestVersion(int size){
		return (size - 15) / 4;
	}
	/**
	 * Generates a bit field that's either all true or all false.
	 * @param version of this QRcode
	 * @param fill
	 * @return boolean[][] a mask
	 */
	public static boolean [][] generateMask(int version, boolean fill) {
		int d = getSize(version);
		boolean [][] mask = new boolean[d][d];
		for (boolean[] roc : mask) {
			Arrays.fill(roc, fill);
		}
		return mask;
	}
	/**
	  * Generates a data Mask that contains finding patterns, alignment patters, timing patterns, version info, and format info.
	 * @param version of this QRcode
	 * @return boolean[][] a mask
	 */
	public static boolean [][] getDataMask(int version) {
		boolean [][] mask = generateMask(version, true);
		int d = mask.length;
//		for (Point p: getFindingPatternLocations(version)) {
//			clearSquare(mask, Constants.FINDING_PATTERN_SIZE, p.x, p.y);
//		}
		for (Point p: getAlignmentPatternLocations(version)) {
			clearSquare(mask, Constants.ALIGNMENT_PATTERN_SIZE, p.x + Constants.ALIGNMENT_PATTERN_OFFSET, p.y + Constants.ALIGNMENT_PATTERN_OFFSET);
		}
		// add white border around finding patterns
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				mask[i][j] = false;
			}
		}
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				mask[d - i - 1][j] = false;
			}
		}
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				mask[i][d - j - 1] = false;
			}
		}
		//timing
		for (int x = 0; x < d; x++) {
			mask[x][6] = false;
		}
		for (int y = 0; y < d; y++) {
			mask[6][y] = false;
		}
		//metaData
		//format info
		for (int y = 0; y < 8; y++) {
			mask[8][y] = false;
			mask[8][d - y - 1] = false;
		}
		for (int x = 0; x < 8; x++) {
			mask[x][8] = false;
			mask[d - x - 1][8] = false;
		}
		mask[8][8] = false;//top left has an extra bit in format.
		//version info (only for version 7 and up)
		if (version >= 7) {
			for (int y = 0; y < 8; y++) {
				mask[d - 9][y] = false;
				mask[d - 10][y] = false;
				mask[d - 11][y] = false;
			}
			for (int x = 0; x < 8; x++) {
				mask[x][d - 9] = false;
				mask[x][d - 10] = false;
				mask[x][d - 11] = false;
			}
		}
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
	/**
	 * Provides information for top left corners of Finding Patterns for a particular version of a QRCode
	 * @param version
	 * @return An Array of points for top left corner of Finding Patterns
	 */
	public static Point[] getFindingPatternLocations(int version) {
		int d = getSize(version);
		Point [] blah = {new Point(0,0), new Point(0,d - Constants.FINDING_PATTERN_SIZE), new Point(d - Constants.FINDING_PATTERN_SIZE,0)};
		return blah;
	}
	/**
	 * Provides information for centers of Alignment Patterns for a particular version of a QRCode
	 * @param version
	 * @return An Array of points for centers of Alignment Patterns
	 */
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
	/**
	 * Provides information about the maximum number of modules (Data and ErrorCorrection) for a particular verison of a QRCode
	 * @param version
	 * @return Maximum number of modules for a particular version of a QRCode
	 */
	public static int getDataCapacity(int version) {
		int size = getSize(version);
		int apc = alignmentLocations[version - 1].length;
		int available = size * size;
		available -= 3 * 8 * 8;//finding patterns
		available -= 2 * (size - 16);//timing (16 offsets with the timing patterns)
		available -= 5 * 5 * ((apc * apc) - ((apc > 1)?3:0));//alignment patterns
		available -= (version < 7)?31:67;//format and version info modules
		return available;
	}
	private Version() throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}
	private static int [] vibs = {
		0x07C94, //7
		0x085BC, //8
		0x09A99, //9
		0x0A4D3, //10
		0x0BBF6, //11
		0x0C762, //12
		0x0D847, //13
		0x0E60D, //14
		0x0F928, //15
		0x10B78, //16
		0x1145D, //17
		0x12A17, //18
		0x13532, //19
		0x149A6, //20
		0x15683, //21
		0x168C9, //22
		0x177EC, //23
		0x18EC4, //24
		0x191E1, //25
		0x1AFAB, //26
		0x1B08E, //27
		0x1CC1A, //28
		0x1D33F, //29
		0x1ED75, //30
		0x1F250, //31
		0x209D5, //32
		0x216F0, //33
		0x228BA, //34
		0x2379F, //35
		0x24B0B, //36
		0x2542E, //37
		0x26A64, //38
		0x27541, //39
		0x28C69, //40
	};
	/**
	 * Returns Version info in bit form for versions above 7.
	 * @param version
	 * @return version info
	 */
	public static Integer getVersionInfoBitStream(int version) {
		if (version < 7 || version > 40) {
			return null;
		}
		return vibs[version - 7];
	}
	/**
	 * Runs error correction on Version Information (BCH)
	 * @param sequence
	 * @return
	 */
	public static CorrectedInfo getCorrectedVersionInfo(int sequence) {
		for(int i = 0; i < vibs.length; i++){
			int data = vibs[i];
			int xor = data ^ sequence;
			int count = Integer.bitCount(xor);
			if (count < 4) {
				return new CorrectedInfo(count, sequence, data);
			}
		}
		return null;
	}

}