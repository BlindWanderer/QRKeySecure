package edu.mccc.cos210.qrks.qrcode;
import java.util.*;
import java.awt.Point;

public final class Version {
	public static class SymbolCharacterInfo {
		private SymbolCharacterInfo(final int dataCodeWordBits, 
									final int dataCapacityNumeric,
									final int dataCapacityAlphanumeric,
									final int dataCapacityByte,
									final int dataCapacityKanji) {
			this.dataCodeWordBits = dataCodeWordBits;
			this.dataCapacityNumeric = dataCapacityNumeric;
			this.dataCapacityAlphanumeric = dataCapacityAlphanumeric;
			this.dataCapacityByte = dataCapacityByte;
			this.dataCapacityKanji = dataCapacityKanji;
		}
		public final int dataCodeWordBits;
		public final int dataCapacityNumeric;
		public final int dataCapacityAlphanumeric;
		public final int dataCapacityByte;
		public final int dataCapacityKanji;
		public int getDataCapacity(EncodingMode em) {
			switch (em) {
				case BYTE:
					return this.dataCapacityByte;
				case NUMERIC:
					return this.dataCapacityNumeric;
				case ALPHANUMERIC:
					return this.dataCapacityAlphanumeric;
				case KANJI:
					return this.dataCapacityKanji;
				//case ECI:
				//case STRUCTURED_APPEND:
				//case FNC1_FIRST(0b0101),
				//case FNC1_SECOND(0b1001),
				//case TERMINATOR(0b0000);
				default:
					return -1;
			}
		}
	}
	public final static SymbolCharacterInfo[][] nosc;
	private static void NOSC4(int version,
                         int Ld, int Md, int Qd, int Hd,
	                     int Ln, int Mn, int Qn, int Hn,
						 int La, int Ma, int Qa, int Ha,
						 int Lb, int Mb, int Qb, int Hb,
						 int Lk, int Mk, int Qk, int Hk) {
		nosc[0][version - 1] = new SymbolCharacterInfo(Ld, Ln, La, Lb, Lk);
		nosc[1][version - 1] = new SymbolCharacterInfo(Md, Mn, Ma, Mb, Mk);
		nosc[2][version - 1] = new SymbolCharacterInfo(Qd, Qn, Qa, Qb, Qk);
		nosc[3][version - 1] = new SymbolCharacterInfo(Hd, Hn, Ha, Hb, Hk);
	}
	static {
		nosc = new SymbolCharacterInfo[4][40];
		NOSC4(1, 152,128,104,72,41,34,27,17,25,20,16,10,17,14,11,7,10,8,7,4);
		NOSC4(2, 272,224,176,128,77,63,48,34,47,38,29,20,32,26,20,14,20,16,12,8);
		NOSC4(3, 440,352,272,208,127,101,77,58,77,61,47,35,53,42,32,24,32,26,20,15);
		NOSC4(4, 640,512,384,288,187,149,111,82,114,90,67,50,78,62,46,34,48,38,28,21);
		NOSC4(5, 864,688,496,368,255,202,144,106,154,122,87,64,106,84,60,44,65,52,37,27);
		NOSC4(6, 1088,864,608,480,322,255,178,139,195,154,108,84,134,106,74,58,82,65,45,36);
		NOSC4(7, 1248,992,704,528,370,293,207,154,224,178,125,93,154,122,86,64,95,75,53,39);
		NOSC4(8, 1552,1232,880,688,461,365,259,202,279,221,157,122,192,152,108,84,118,93,66,52);
		NOSC4(9, 1856,1456,1056,800,552,432,312,235,335,262,189,143,230,180,130,98,141,111,80,60);
		NOSC4(10, 2192,1728,1232,976,652,513,364,288,395,311,221,174,271,213,151,119,167,131,93,74);
		NOSC4(11, 2592,2032,1440,1120,772,604,427,331,468,366,259,200,321,251,177,137,198,155,109,85);
		NOSC4(12, 2960,2320,1648,1264,883,691,489,374,535,419,296,227,367,287,203,155,226,177,125,96);
		NOSC4(13, 3424,2672,1952,1440,1022,796,580,427,619,483,352,259,425,331,241,177,262,204,149,109);
		NOSC4(14, 3688,2920,2088,1576,1101,871,621,468,667,528,376,283,458,362,258,194,282,223,159,120);
		NOSC4(15, 4184,3320,2360,1784,1250,991,703,530,758,600,426,321,520,412,292,220,320,254,180,136);
		NOSC4(16, 4712,3624,2600,2024,1408,1082,775,602,854,656,470,365,586,450,322,250,361,277,198,154);
		NOSC4(17, 5176,4056,2936,2264,1548,1212,876,674,938,734,531,408,644,504,364,280,397,310,224,173);
		NOSC4(18, 5768,4504,3176,2504,1725,1346,948,746,1046,816,574,452,718,560,394,310,442,345,243,191);
		NOSC4(19, 6360,5016,3560,2728,1903,1500,1063,813,1153,909,644,493,792,624,442,338,488,384,272,208);
		NOSC4(20, 6888,5352,3880,3080,2061,1600,1159,919,1249,970,702,557,858,666,482,382,528,410,297,235);
		NOSC4(21, 7456,5712,4096,3248,2232,1708,1224,969,1352,1035,742,587,929,711,509,403,572,438,314,248);
		NOSC4(22, 8048,6256,4544,3536,2409,1872,1358,1056,1460,1134,823,640,1003,779,565,439,618,480,348,270);
		NOSC4(23, 8752,6880,4912,3712,2620,2059,1468,1108,1588,1248,890,672,1091,857,611,461,672,528,376,284);
		NOSC4(24, 9392,7312,5312,4112,2812,2188,1588,1228,1704,1326,963,744,1171,911,661,511,721,561,407,315);
		NOSC4(25, 10208,8000,5744,4304,3057,2395,1718,1286,1853,1451,1041,779,1273,997,715,535,784,614,440,330);
		NOSC4(26, 10960,8496,6032,4768,3283,2544,1804,1425,1990,1542,1094,864,1367,1059,751,593,842,652,462,365);
		NOSC4(27, 11744,9024,6464,5024,3517,2701,1933,1501,2132,1637,1172,910,1465,1125,805,625,902,692,496,385);
		NOSC4(28, 12248,9544,6968,5288,3669,2857,2085,1581,2223,1732,1263,958,1528,1190,868,658,940,732,534,405);
		NOSC4(29, 13048,10136,7288,5608,3909,3035,2181,1677,2369,1839,1322,1016,1628,1264,908,698,1002,778,559,430);
		NOSC4(30, 13880,10984,7880,5960,4158,3289,2358,1782,2520,1994,1429,1080,1732,1370,982,742,1066,843,604,457);
		NOSC4(31, 14744,11640,8264,6344,4417,3486,2473,1897,2677,2113,1499,1150,1840,1452,1030,790,1132,894,634,486);
		NOSC4(32, 15640,12328,8920,6760,4686,3693,2670,2022,2840,2238,1618,1226,1952,1538,1112,842,1201,947,684,518);
		NOSC4(33, 16568,13048,9368,7208,4965,3909,2805,2157,3009,2369,1700,1307,2068,1628,1168,898,1273,1002,719,553);
		NOSC4(34, 17528,13800,9848,7688,5253,4134,2949,2301,3183,2506,1787,1394,2188,1722,1228,958,1347,1060,756,590);
		NOSC4(35, 18448,14496,10288,7888,5529,4343,3081,2361,3351,2632,1867,1431,2303,1809,1283,983,1417,1113,790,605);
		NOSC4(36, 19472,15312,10832,8432,5836,4588,3244,2524,3537,2780,1966,1530,2431,1911,1351,1051,1496,1176,832,647);
		NOSC4(37, 20528,15936,11408,8768,6153,4775,3417,2625,3729,2894,2071,1591,2563,1989,1423,1093,1577,1224,876,673);
		NOSC4(38, 21616,16816,12016,9136,6479,5039,3599,2735,3927,3054,2181,1658,2699,2099,1499,1139,1661,1292,923,701);
		NOSC4(39, 22496,17728,12656,9776,6743,5313,3791,2927,4087,3220,2298,1774,2809,2213,1579,1219,1729,1362,972,750);
		NOSC4(40, 23648,18672,13328,10208,7089,5596,3993,3057,4296,3391,2420,1852,2953,2331,1663,1273,1817,1435,1024,784);
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
	public static int getDataCapacity(int version) {
		int apc = alignmentLocations[version - 1].length;
		int size = getSize(version);
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
}