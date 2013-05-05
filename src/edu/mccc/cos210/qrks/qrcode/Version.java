package edu.mccc.cos210.qrks.qrcode;
import java.util.*;
import java.awt.Point;

public final class Version {
	public static class Info {
		public final int errors;
		public final int original;
		public final int corrected;
		private Info(int errors, int original, int corrected) {
			this.errors = errors;
			this.original = original;
			this.corrected = corrected;
		}
	}
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
	private static void NOSC4(int version, //{
                         int Ld, int Md, int Qd, int Hd,
	                     int Ln, int Mn, int Qn, int Hn,
						 int La, int Ma, int Qa, int Ha,
						 int Lb, int Mb, int Qb, int Hb,
						 int Lk, int Mk, int Qk, int Hk) {
		nosc[0][version - 1] = new SymbolCharacterInfo(Ld, Ln, La, Lb, Lk);
		nosc[1][version - 1] = new SymbolCharacterInfo(Md, Mn, Ma, Mb, Mk);
		nosc[2][version - 1] = new SymbolCharacterInfo(Qd, Qn, Qa, Qb, Qk);
		nosc[3][version - 1] = new SymbolCharacterInfo(Hd, Hn, Ha, Hb, Hk);
	} //}
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
		//timing
		for (int x = 0; x < version; x++) {
			mask[x][6] = false;
		}
		for (int y = 0; y < version; y++) {
			mask[6][y] = false;
		}
		//metaData
			//format info
		for (int y = 0; y < 8; y++) {
			mask[8][y] = false;
			mask[8][version - y] = false;
		}
		for (int x = 0; x < 8; x++) {
			mask[x][8] = false;
			mask[version - x][8] = false;
		}
			//version info (only for version 7 and up)
		if (version >= 7) {
			for (int y = 0; y < 8; y++) {
				mask[version - 8][y] = false;
				mask[version - 9][y] = false;
				mask[version - 10][y] = false;
			}
			for (int x = 0; x < 8; x++) {
				mask[x][version - 8] = false;
				mask[x][version - 9] = false;
				mask[x][version - 10] = false;
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
	public static class ErrorCorrectionRow {
		public final int p;
		public final int ecBlocks;
		public final int c;
		public final int k;
		public final int r;
		public ErrorCorrectionRow(int p, int ecBlocks, int c, int k, int r) {
			this.p = p;
			this.ecBlocks = ecBlocks;
			this.c = c;
			this.k = k;
			this.r = r;
		}
	}
	public static class ErrorCorrectionCharacteristic {
		public final ErrorCorrectionRow [] errorCorrectionRows;
		public final int ecCodewords;
		public ErrorCorrectionCharacteristic(int ecCodewords, ErrorCorrectionRow primary) {
			this.ecCodewords = ecCodewords;
			ErrorCorrectionRow [] t = {primary};
			this.errorCorrectionRows = t;
		}
		public ErrorCorrectionCharacteristic(int ecCodewords, ErrorCorrectionRow primary, ErrorCorrectionRow secondary) {
			this.ecCodewords = ecCodewords;
			ErrorCorrectionRow [] t = {primary, secondary};
			this.errorCorrectionRows = t;
		}
	}
	public static ErrorCorrectionCharacteristic getErrorCorrectionCharacteristic(int version, ErrorCorrectionLevel ec){
		return ecc[version -1][ec.index];
	}
	private static ErrorCorrectionCharacteristic [][] ecc = {
		/*1, 26*/ { new ErrorCorrectionCharacteristic(/*L, */7, new ErrorCorrectionRow(3, 1, 26, 19, 2)), new ErrorCorrectionCharacteristic(/*M, */10, new ErrorCorrectionRow(2, 1, 26, 16, 4)), new ErrorCorrectionCharacteristic(/*Q, */13, new ErrorCorrectionRow(1, 1, 26, 13, 6)), new ErrorCorrectionCharacteristic(/*H, */17, new ErrorCorrectionRow(1, 1, 26, 9, 8)), },
		/*2, 44*/ { new ErrorCorrectionCharacteristic(/*L, */10, new ErrorCorrectionRow(2, 1, 44, 34, 4)), new ErrorCorrectionCharacteristic(/*M, */16, new ErrorCorrectionRow(0, 1, 44, 28, 8)), new ErrorCorrectionCharacteristic(/*Q, */22, new ErrorCorrectionRow(0, 1, 44, 22, 11)), new ErrorCorrectionCharacteristic(/*H, */28, new ErrorCorrectionRow(0, 1, 44, 16, 14)), },
		/*3, 70*/ { new ErrorCorrectionCharacteristic(/*L, */15, new ErrorCorrectionRow(1, 1, 70, 55, 7)), new ErrorCorrectionCharacteristic(/*M, */26, new ErrorCorrectionRow(0, 1, 70, 44, 13)), new ErrorCorrectionCharacteristic(/*Q, */36, new ErrorCorrectionRow(0, 2, 35, 17, 9)), new ErrorCorrectionCharacteristic(/*H, */44, new ErrorCorrectionRow(0, 2, 35, 13, 11)), },
		/*4, 100*/ { new ErrorCorrectionCharacteristic(/*L, */20, new ErrorCorrectionRow(0, 1, 100, 80, 10)), new ErrorCorrectionCharacteristic(/*M, */36, new ErrorCorrectionRow(0, 2, 50, 32, 9)), new ErrorCorrectionCharacteristic(/*Q, */52, new ErrorCorrectionRow(0, 2, 50, 24, 13)), new ErrorCorrectionCharacteristic(/*H, */64, new ErrorCorrectionRow(0, 4, 25, 9, 8)), },
		/*5, 134*/ { new ErrorCorrectionCharacteristic(/*L, */26, new ErrorCorrectionRow(0, 1, 134, 108, 13)), new ErrorCorrectionCharacteristic(/*M, */48, new ErrorCorrectionRow(0, 2, 67, 43, 12)), new ErrorCorrectionCharacteristic(/*Q, */72, new ErrorCorrectionRow(0, 2, 33, 15, 9), new ErrorCorrectionRow(0, 2, 34, 16, 9)), new ErrorCorrectionCharacteristic(/*H, */88, new ErrorCorrectionRow(0, 2, 33, 11, 11), new ErrorCorrectionRow(0, 2, 34, 12, 11)), },
		/*6, 172*/ { new ErrorCorrectionCharacteristic(/*L, */36, new ErrorCorrectionRow(0, 2, 86, 68, 9)), new ErrorCorrectionCharacteristic(/*M, */64, new ErrorCorrectionRow(0, 4, 43, 27, 8)), new ErrorCorrectionCharacteristic(/*Q, */96, new ErrorCorrectionRow(0, 4, 43, 19, 12)), new ErrorCorrectionCharacteristic(/*H, */112, new ErrorCorrectionRow(0, 4, 43, 15, 14)), },
		/*7, 196*/ { new ErrorCorrectionCharacteristic(/*L, */40, new ErrorCorrectionRow(0, 2, 98, 78, 10)), new ErrorCorrectionCharacteristic(/*M, */72, new ErrorCorrectionRow(0, 4, 49, 31, 9)), new ErrorCorrectionCharacteristic(/*Q, */108, new ErrorCorrectionRow(0, 2, 32, 14, 9), new ErrorCorrectionRow(0, 4, 33, 15, 9)), new ErrorCorrectionCharacteristic(/*H, */130, new ErrorCorrectionRow(0, 4, 39, 13, 13), new ErrorCorrectionRow(0, 1, 40, 14, 13)), },
		/*8, 242*/ { new ErrorCorrectionCharacteristic(/*L, */48, new ErrorCorrectionRow(0, 2, 121, 97, 12)), new ErrorCorrectionCharacteristic(/*M, */88, new ErrorCorrectionRow(0, 2, 60, 38, 11), new ErrorCorrectionRow(0, 2, 61, 39, 11)), new ErrorCorrectionCharacteristic(/*Q, */132, new ErrorCorrectionRow(0, 4, 40, 18, 11), new ErrorCorrectionRow(0, 2, 41, 19, 11)), new ErrorCorrectionCharacteristic(/*H, */156, new ErrorCorrectionRow(0, 4, 40, 14, 13), new ErrorCorrectionRow(0, 2, 41, 15, 13)), },
		/*9, 292*/ { new ErrorCorrectionCharacteristic(/*L, */60, new ErrorCorrectionRow(0, 2, 146, 116, 15)), new ErrorCorrectionCharacteristic(/*M, */110, new ErrorCorrectionRow(0, 3, 58, 36, 11), new ErrorCorrectionRow(0, 2, 59, 37, 11)), new ErrorCorrectionCharacteristic(/*Q, */160, new ErrorCorrectionRow(0, 4, 36, 16, 10), new ErrorCorrectionRow(0, 4, 37, 17, 10)), new ErrorCorrectionCharacteristic(/*H, */192, new ErrorCorrectionRow(0, 4, 36, 12, 12), new ErrorCorrectionRow(0, 4, 37, 13, 12)), },
		/*10, 346*/ { new ErrorCorrectionCharacteristic(/*L, */72, new ErrorCorrectionRow(0, 2, 86, 68, 9), new ErrorCorrectionRow(0, 2, 87, 69, 9)), new ErrorCorrectionCharacteristic(/*M, */130, new ErrorCorrectionRow(0, 4, 69, 43, 13), new ErrorCorrectionRow(0, 1, 70, 44, 13)), new ErrorCorrectionCharacteristic(/*Q, */192, new ErrorCorrectionRow(0, 6, 43, 19, 12), new ErrorCorrectionRow(0, 2, 44, 20, 12)), new ErrorCorrectionCharacteristic(/*H, */224, new ErrorCorrectionRow(0, 6, 43, 15, 14), new ErrorCorrectionRow(0, 2, 44, 16, 14)), },
		/*11, 404*/ { new ErrorCorrectionCharacteristic(/*L, */80, new ErrorCorrectionRow(0, 4, 101, 81, 10)), new ErrorCorrectionCharacteristic(/*M, */150, new ErrorCorrectionRow(0, 1, 80, 50, 15), new ErrorCorrectionRow(0, 4, 81, 51, 15)), new ErrorCorrectionCharacteristic(/*Q, */224, new ErrorCorrectionRow(0, 4, 50, 22, 14), new ErrorCorrectionRow(0, 4, 51, 23, 14)), new ErrorCorrectionCharacteristic(/*H, */264, new ErrorCorrectionRow(0, 3, 36, 12, 12), new ErrorCorrectionRow(0, 8, 37, 13, 12)), },
		/*12, 466*/ { new ErrorCorrectionCharacteristic(/*L, */96, new ErrorCorrectionRow(0, 2, 116, 92, 12), new ErrorCorrectionRow(0, 2, 117, 93, 12)), new ErrorCorrectionCharacteristic(/*M, */176, new ErrorCorrectionRow(0, 6, 58, 36, 11), new ErrorCorrectionRow(0, 2, 59, 37, 11)), new ErrorCorrectionCharacteristic(/*Q, */260, new ErrorCorrectionRow(0, 4, 46, 20, 13), new ErrorCorrectionRow(0, 6, 47, 21, 13)), new ErrorCorrectionCharacteristic(/*H, */308, new ErrorCorrectionRow(0, 7, 42, 14, 14), new ErrorCorrectionRow(0, 4, 43, 15, 14)), },
		/*13, 532*/ { new ErrorCorrectionCharacteristic(/*L, */104, new ErrorCorrectionRow(0, 4, 133, 107, 13)), new ErrorCorrectionCharacteristic(/*M, */198, new ErrorCorrectionRow(0, 8, 59, 37, 11), new ErrorCorrectionRow(0, 1, 60, 38, 11)), new ErrorCorrectionCharacteristic(/*Q, */288, new ErrorCorrectionRow(0, 8, 44, 20, 12), new ErrorCorrectionRow(0, 4, 45, 21, 12)), new ErrorCorrectionCharacteristic(/*H, */352, new ErrorCorrectionRow(0, 12, 33, 11, 11), new ErrorCorrectionRow(0, 4, 34, 12, 11)), },
		/*14, 581*/ { new ErrorCorrectionCharacteristic(/*L, */120, new ErrorCorrectionRow(0, 3, 145, 115, 15), new ErrorCorrectionRow(0, 1, 146, 116, 15)), new ErrorCorrectionCharacteristic(/*M, */216, new ErrorCorrectionRow(0, 4, 64, 40, 12), new ErrorCorrectionRow(0, 5, 65, 41, 12)), new ErrorCorrectionCharacteristic(/*Q, */320, new ErrorCorrectionRow(0, 11, 36, 16, 10), new ErrorCorrectionRow(0, 5, 37, 17, 10)), new ErrorCorrectionCharacteristic(/*H, */384, new ErrorCorrectionRow(0, 11, 36, 12, 12), new ErrorCorrectionRow(0, 5, 37, 13, 12)), },
		/*15, 655*/ { new ErrorCorrectionCharacteristic(/*L, */132, new ErrorCorrectionRow(0, 5, 109, 87, 11), new ErrorCorrectionRow(0, 1, 110, 88, 11)), new ErrorCorrectionCharacteristic(/*M, */240, new ErrorCorrectionRow(0, 5, 65, 41, 12), new ErrorCorrectionRow(0, 5, 66, 42, 12)), new ErrorCorrectionCharacteristic(/*Q, */360, new ErrorCorrectionRow(0, 5, 54, 24, 15), new ErrorCorrectionRow(0, 7, 55, 25, 15)), new ErrorCorrectionCharacteristic(/*H, */432, new ErrorCorrectionRow(0, 11, 36, 12, 12), new ErrorCorrectionRow(0, 7, 37, 13, 12)), },
		/*16, 733*/ { new ErrorCorrectionCharacteristic(/*L, */144, new ErrorCorrectionRow(0, 5, 122, 98, 12), new ErrorCorrectionRow(0, 1, 123, 99, 12)), new ErrorCorrectionCharacteristic(/*M, */280, new ErrorCorrectionRow(0, 7, 73, 45, 14), new ErrorCorrectionRow(0, 3, 74, 46, 14)), new ErrorCorrectionCharacteristic(/*Q, */408, new ErrorCorrectionRow(0, 15, 43, 19, 12), new ErrorCorrectionRow(0, 2, 44, 20, 12)), new ErrorCorrectionCharacteristic(/*H, */480, new ErrorCorrectionRow(0, 3, 45, 15, 15), new ErrorCorrectionRow(0, 13, 46, 16, 15)), },
		/*17, 815*/ { new ErrorCorrectionCharacteristic(/*L, */168, new ErrorCorrectionRow(0, 1, 135, 107, 14), new ErrorCorrectionRow(0, 5, 136, 108, 14)), new ErrorCorrectionCharacteristic(/*M, */308, new ErrorCorrectionRow(0, 10, 74, 46, 14), new ErrorCorrectionRow(0, 1, 75, 47, 14)), new ErrorCorrectionCharacteristic(/*Q, */448, new ErrorCorrectionRow(0, 1, 50, 22, 14), new ErrorCorrectionRow(0, 15, 51, 23, 14)), new ErrorCorrectionCharacteristic(/*H, */532, new ErrorCorrectionRow(0, 2, 42, 14, 14), new ErrorCorrectionRow(0, 17, 43, 15, 14)), },
		/*18, 901*/ { new ErrorCorrectionCharacteristic(/*L, */180, new ErrorCorrectionRow(0, 5, 150, 120, 15), new ErrorCorrectionRow(0, 1, 151, 121, 15)), new ErrorCorrectionCharacteristic(/*M, */338, new ErrorCorrectionRow(0, 9, 69, 43, 13), new ErrorCorrectionRow(0, 4, 70, 44, 13)), new ErrorCorrectionCharacteristic(/*Q, */504, new ErrorCorrectionRow(0, 17, 50, 22, 14), new ErrorCorrectionRow(0, 1, 51, 23, 14)), new ErrorCorrectionCharacteristic(/*H, */588, new ErrorCorrectionRow(0, 2, 42, 14, 14), new ErrorCorrectionRow(0, 19, 43, 15, 14)), },
		/*19, 991*/ { new ErrorCorrectionCharacteristic(/*L, */196, new ErrorCorrectionRow(0, 3, 141, 113, 14), new ErrorCorrectionRow(0, 4, 142, 114, 14)), new ErrorCorrectionCharacteristic(/*M, */364, new ErrorCorrectionRow(0, 3, 70, 44, 13), new ErrorCorrectionRow(0, 11, 71, 45, 13)), new ErrorCorrectionCharacteristic(/*Q, */546, new ErrorCorrectionRow(0, 17, 47, 21, 13), new ErrorCorrectionRow(0, 4, 48, 22, 13)), new ErrorCorrectionCharacteristic(/*H, */650, new ErrorCorrectionRow(0, 9, 39, 13, 13), new ErrorCorrectionRow(0, 16, 40, 14, 13)), },
		/*20, 1085*/ { new ErrorCorrectionCharacteristic(/*L, */224, new ErrorCorrectionRow(0, 3, 135, 107, 14), new ErrorCorrectionRow(0, 5, 136, 108, 14)), new ErrorCorrectionCharacteristic(/*M, */416, new ErrorCorrectionRow(0, 3, 67, 41, 13), new ErrorCorrectionRow(0, 13, 68, 42, 13)), new ErrorCorrectionCharacteristic(/*Q, */600, new ErrorCorrectionRow(0, 15, 54, 24, 15), new ErrorCorrectionRow(0, 5, 55, 25, 15)), new ErrorCorrectionCharacteristic(/*H, */700, new ErrorCorrectionRow(0, 15, 43, 15, 14), new ErrorCorrectionRow(0, 10, 44, 16, 14)), },
		/*21, 1156*/ { new ErrorCorrectionCharacteristic(/*L, */224, new ErrorCorrectionRow(0, 4, 144, 116, 14)), new ErrorCorrectionCharacteristic(/*M, */442, new ErrorCorrectionRow(0, 17, 68, 42, 13)), new ErrorCorrectionCharacteristic(/*Q, */644, new ErrorCorrectionRow(0, 17, 50, 22, 14)), new ErrorCorrectionCharacteristic(/*H, */750, new ErrorCorrectionRow(0, 19, 46, 16, 15)), },
		/*22, 1258*/ { new ErrorCorrectionCharacteristic(/*L, */252, new ErrorCorrectionRow(0, 2, 139, 111, 14), new ErrorCorrectionRow(0, 7, 140, 112, 14)), new ErrorCorrectionCharacteristic(/*M, */476, new ErrorCorrectionRow(0, 17, 74, 46, 14)), new ErrorCorrectionCharacteristic(/*Q, */690, new ErrorCorrectionRow(0, 7, 54, 24, 15), new ErrorCorrectionRow(0, 16, 55, 25, 15)), new ErrorCorrectionCharacteristic(/*H, */816, new ErrorCorrectionRow(0, 34, 37, 13, 12)), },
		/*23, 1364*/ { new ErrorCorrectionCharacteristic(/*L, */270, new ErrorCorrectionRow(0, 4, 151, 121, 15), new ErrorCorrectionRow(0, 5, 152, 122, 15)), new ErrorCorrectionCharacteristic(/*M, */504, new ErrorCorrectionRow(0, 4, 75, 47, 14), new ErrorCorrectionRow(0, 14, 76, 48, 14)), new ErrorCorrectionCharacteristic(/*Q, */750, new ErrorCorrectionRow(0, 11, 54, 24, 15), new ErrorCorrectionRow(0, 14, 55, 25, 15)), new ErrorCorrectionCharacteristic(/*H, */900, new ErrorCorrectionRow(0, 16, 45, 15, 15), new ErrorCorrectionRow(0, 14, 46, 16, 15)), },
		/*24, 1474*/ { new ErrorCorrectionCharacteristic(/*L, */300, new ErrorCorrectionRow(0, 6, 147, 117, 15), new ErrorCorrectionRow(0, 4, 148, 118, 15)), new ErrorCorrectionCharacteristic(/*M, */560, new ErrorCorrectionRow(0, 6, 73, 45, 14), new ErrorCorrectionRow(0, 14, 74, 46, 14)), new ErrorCorrectionCharacteristic(/*Q, */810, new ErrorCorrectionRow(0, 11, 54, 24, 15), new ErrorCorrectionRow(0, 16, 55, 25, 15)), new ErrorCorrectionCharacteristic(/*H, */960, new ErrorCorrectionRow(0, 30, 46, 16, 15), new ErrorCorrectionRow(0, 2, 47, 17, 15)), },
		/*25, 1588*/ { new ErrorCorrectionCharacteristic(/*L, */312, new ErrorCorrectionRow(0, 8, 132, 106, 13), new ErrorCorrectionRow(0, 4, 133, 107, 13)), new ErrorCorrectionCharacteristic(/*M, */588, new ErrorCorrectionRow(0, 8, 75, 47, 14), new ErrorCorrectionRow(0, 13, 76, 48, 14)), new ErrorCorrectionCharacteristic(/*Q, */870, new ErrorCorrectionRow(0, 7, 54, 24, 15), new ErrorCorrectionRow(0, 22, 55, 25, 15)), new ErrorCorrectionCharacteristic(/*H, */1050, new ErrorCorrectionRow(0, 22, 45, 15, 15), new ErrorCorrectionRow(0, 13, 46, 16, 15)), },
		/*26, 1706*/ { new ErrorCorrectionCharacteristic(/*L, */336, new ErrorCorrectionRow(0, 10, 142, 114, 14), new ErrorCorrectionRow(0, 2, 143, 115, 14)), new ErrorCorrectionCharacteristic(/*M, */644, new ErrorCorrectionRow(0, 19, 74, 46, 14), new ErrorCorrectionRow(0, 4, 75, 47, 14)), new ErrorCorrectionCharacteristic(/*Q, */952, new ErrorCorrectionRow(0, 28, 50, 22, 14), new ErrorCorrectionRow(0, 6, 51, 23, 14)), new ErrorCorrectionCharacteristic(/*H, */1110, new ErrorCorrectionRow(0, 33, 46, 16, 15), new ErrorCorrectionRow(0, 4, 47, 17, 15)), },
		/*27, 1828*/ { new ErrorCorrectionCharacteristic(/*L, */360, new ErrorCorrectionRow(0, 8, 152, 122, 15), new ErrorCorrectionRow(0, 4, 153, 123, 15)), new ErrorCorrectionCharacteristic(/*M, */700, new ErrorCorrectionRow(0, 22, 73, 45, 14), new ErrorCorrectionRow(0, 3, 74, 46, 14)), new ErrorCorrectionCharacteristic(/*Q, */1020, new ErrorCorrectionRow(0, 8, 53, 23, 15), new ErrorCorrectionRow(0, 26, 54, 24, 15)), new ErrorCorrectionCharacteristic(/*H, */1200, new ErrorCorrectionRow(0, 12, 45, 15, 15), new ErrorCorrectionRow(0, 28, 46, 16, 15)), },
		/*28, 1921*/ { new ErrorCorrectionCharacteristic(/*L, */390, new ErrorCorrectionRow(0, 3, 147, 117, 15), new ErrorCorrectionRow(0, 10, 148, 118, 15)), new ErrorCorrectionCharacteristic(/*M, */728, new ErrorCorrectionRow(0, 3, 73, 45, 14), new ErrorCorrectionRow(0, 23, 74, 46, 14)), new ErrorCorrectionCharacteristic(/*Q, */1050, new ErrorCorrectionRow(0, 4, 54, 24, 15), new ErrorCorrectionRow(0, 31, 55, 25, 15)), new ErrorCorrectionCharacteristic(/*H, */1260, new ErrorCorrectionRow(0, 11, 45, 15, 15), new ErrorCorrectionRow(0, 31, 46, 16, 15)), },
		/*29, 2051*/ { new ErrorCorrectionCharacteristic(/*L, */420, new ErrorCorrectionRow(0, 7, 146, 116, 15), new ErrorCorrectionRow(0, 7, 147, 117, 15)), new ErrorCorrectionCharacteristic(/*M, */784, new ErrorCorrectionRow(0, 21, 73, 45, 14), new ErrorCorrectionRow(0, 7, 74, 46, 14)), new ErrorCorrectionCharacteristic(/*Q, */1140, new ErrorCorrectionRow(0, 1, 53, 23, 15), new ErrorCorrectionRow(0, 37, 54, 24, 15)), new ErrorCorrectionCharacteristic(/*H, */1350, new ErrorCorrectionRow(0, 19, 45, 15, 15), new ErrorCorrectionRow(0, 26, 46, 16, 15)), },
		/*30, 2185*/ { new ErrorCorrectionCharacteristic(/*L, */450, new ErrorCorrectionRow(0, 5, 145, 115, 15), new ErrorCorrectionRow(0, 10, 146, 116, 15)), new ErrorCorrectionCharacteristic(/*M, */812, new ErrorCorrectionRow(0, 19, 75, 47, 14), new ErrorCorrectionRow(0, 10, 76, 48, 14)), new ErrorCorrectionCharacteristic(/*Q, */1200, new ErrorCorrectionRow(0, 15, 54, 24, 15), new ErrorCorrectionRow(0, 25, 55, 25, 15)), new ErrorCorrectionCharacteristic(/*H, */1440, new ErrorCorrectionRow(0, 23, 45, 15, 15), new ErrorCorrectionRow(0, 25, 46, 16, 15)), },
		/*31, 2323*/ { new ErrorCorrectionCharacteristic(/*L, */480, new ErrorCorrectionRow(0, 13, 145, 115, 15), new ErrorCorrectionRow(0, 3, 146, 116, 15)), new ErrorCorrectionCharacteristic(/*M, */868, new ErrorCorrectionRow(0, 2, 74, 46, 14), new ErrorCorrectionRow(0, 29, 75, 47, 14)), new ErrorCorrectionCharacteristic(/*Q, */1290, new ErrorCorrectionRow(0, 42, 54, 24, 15), new ErrorCorrectionRow(0, 1, 55, 25, 15)), new ErrorCorrectionCharacteristic(/*H, */1530, new ErrorCorrectionRow(0, 23, 45, 15, 15), new ErrorCorrectionRow(0, 28, 46, 16, 15)), },
		/*32, 2465*/ { new ErrorCorrectionCharacteristic(/*L, */510, new ErrorCorrectionRow(0, 17, 145, 115, 15)), new ErrorCorrectionCharacteristic(/*M, */924, new ErrorCorrectionRow(0, 10, 74, 46, 14), new ErrorCorrectionRow(0, 23, 75, 47, 14)), new ErrorCorrectionCharacteristic(/*Q, */1350, new ErrorCorrectionRow(0, 10, 54, 24, 15), new ErrorCorrectionRow(0, 35, 55, 25, 15)), new ErrorCorrectionCharacteristic(/*H, */1620, new ErrorCorrectionRow(0, 19, 45, 15, 15), new ErrorCorrectionRow(0, 35, 46, 16, 15)), },
		/*33, 2611*/ { new ErrorCorrectionCharacteristic(/*L, */540, new ErrorCorrectionRow(0, 17, 145, 115, 15), new ErrorCorrectionRow(0, 1, 146, 116, 15)), new ErrorCorrectionCharacteristic(/*M, */980, new ErrorCorrectionRow(0, 14, 74, 46, 14), new ErrorCorrectionRow(0, 21, 75, 47, 14)), new ErrorCorrectionCharacteristic(/*Q, */1440, new ErrorCorrectionRow(0, 29, 54, 24, 15), new ErrorCorrectionRow(0, 19, 55, 25, 15)), new ErrorCorrectionCharacteristic(/*H, */1710, new ErrorCorrectionRow(0, 11, 45, 15, 15), new ErrorCorrectionRow(0, 46, 46, 16, 15)), },
		/*34, 2761*/ { new ErrorCorrectionCharacteristic(/*L, */570, new ErrorCorrectionRow(0, 13, 145, 115, 15), new ErrorCorrectionRow(0, 6, 146, 116, 15)), new ErrorCorrectionCharacteristic(/*M, */1036, new ErrorCorrectionRow(0, 14, 74, 46, 14), new ErrorCorrectionRow(0, 23, 75, 47, 14)), new ErrorCorrectionCharacteristic(/*Q, */1530, new ErrorCorrectionRow(0, 44, 54, 24, 15), new ErrorCorrectionRow(0, 7, 55, 25, 15)), new ErrorCorrectionCharacteristic(/*H, */1800, new ErrorCorrectionRow(0, 59, 46, 16, 15), new ErrorCorrectionRow(0, 1, 47, 17, 15)), },
		/*35, 2876*/ { new ErrorCorrectionCharacteristic(/*L, */570, new ErrorCorrectionRow(0, 12, 151, 121, 15), new ErrorCorrectionRow(0, 7, 152, 122, 15)), new ErrorCorrectionCharacteristic(/*M, */1064, new ErrorCorrectionRow(0, 12, 75, 47, 14), new ErrorCorrectionRow(0, 26, 76, 48, 14)), new ErrorCorrectionCharacteristic(/*Q, */1590, new ErrorCorrectionRow(0, 39, 54, 24, 15), new ErrorCorrectionRow(0, 14, 55, 25, 15)), new ErrorCorrectionCharacteristic(/*H, */1890, new ErrorCorrectionRow(0, 22, 45, 15, 15), new ErrorCorrectionRow(0, 41, 46, 16, 15)), },
		/*36, 3034*/ { new ErrorCorrectionCharacteristic(/*L, */600, new ErrorCorrectionRow(0, 6, 151, 121, 15), new ErrorCorrectionRow(0, 14, 152, 122, 15)), new ErrorCorrectionCharacteristic(/*M, */1120, new ErrorCorrectionRow(0, 6, 75, 47, 14), new ErrorCorrectionRow(0, 34, 76, 48, 14)), new ErrorCorrectionCharacteristic(/*Q, */1680, new ErrorCorrectionRow(0, 46, 54, 24, 15), new ErrorCorrectionRow(0, 10, 55, 25, 15)), new ErrorCorrectionCharacteristic(/*H, */1980, new ErrorCorrectionRow(0, 2, 45, 15, 15), new ErrorCorrectionRow(0, 64, 46, 16, 15)), },
		/*37, 3196*/ { new ErrorCorrectionCharacteristic(/*L, */630, new ErrorCorrectionRow(0, 17, 152, 122, 15), new ErrorCorrectionRow(0, 4, 153, 123, 15)), new ErrorCorrectionCharacteristic(/*M, */1204, new ErrorCorrectionRow(0, 29, 74, 46, 14), new ErrorCorrectionRow(0, 14, 75, 47, 14)), new ErrorCorrectionCharacteristic(/*Q, */1770, new ErrorCorrectionRow(0, 49, 54, 24, 15), new ErrorCorrectionRow(0, 10, 55, 25, 15)), new ErrorCorrectionCharacteristic(/*H, */2100, new ErrorCorrectionRow(0, 24, 45, 15, 15), new ErrorCorrectionRow(0, 46, 46, 16, 15)), },
		/*38, 3362*/ { new ErrorCorrectionCharacteristic(/*L, */660, new ErrorCorrectionRow(0, 4, 152, 122, 15), new ErrorCorrectionRow(0, 18, 153, 123, 15)), new ErrorCorrectionCharacteristic(/*M, */1260, new ErrorCorrectionRow(0, 13, 74, 46, 14), new ErrorCorrectionRow(0, 32, 75, 47, 14)), new ErrorCorrectionCharacteristic(/*Q, */1860, new ErrorCorrectionRow(0, 48, 54, 24, 15), new ErrorCorrectionRow(0, 14, 55, 25, 15)), new ErrorCorrectionCharacteristic(/*H, */2220, new ErrorCorrectionRow(0, 42, 45, 15, 15), new ErrorCorrectionRow(0, 32, 46, 16, 15)), },
		/*39, 3532*/ { new ErrorCorrectionCharacteristic(/*L, */720, new ErrorCorrectionRow(0, 20, 147, 117, 15), new ErrorCorrectionRow(0, 4, 148, 118, 15)), new ErrorCorrectionCharacteristic(/*M, */1316, new ErrorCorrectionRow(0, 40, 75, 47, 14), new ErrorCorrectionRow(0, 7, 76, 48, 14)), new ErrorCorrectionCharacteristic(/*Q, */1950, new ErrorCorrectionRow(0, 43, 54, 24, 15), new ErrorCorrectionRow(0, 22, 55, 25, 15)), new ErrorCorrectionCharacteristic(/*H, */2310, new ErrorCorrectionRow(0, 10, 45, 15, 15), new ErrorCorrectionRow(0, 67, 46, 16, 15)), },
		/*40, 3706*/ { new ErrorCorrectionCharacteristic(/*L, */750, new ErrorCorrectionRow(0, 19, 148, 118, 15), new ErrorCorrectionRow(0, 6, 149, 119, 15)), new ErrorCorrectionCharacteristic(/*M, */1372, new ErrorCorrectionRow(0, 18, 75, 47, 14), new ErrorCorrectionRow(0, 31, 76, 48, 14)), new ErrorCorrectionCharacteristic(/*Q, */2040, new ErrorCorrectionRow(0, 34, 54, 24, 15), new ErrorCorrectionRow(0, 34, 55, 25, 15)), new ErrorCorrectionCharacteristic(/*H, */2430, new ErrorCorrectionRow(0, 20, 45, 15, 15), new ErrorCorrectionRow(0, 61, 46, 16, 15)), },
	};
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
	public static Integer getVersionInfoBitStream(int version) {
		if (version < 7 || version > 40) {
			return null;
		}
		return vibs[version - 7];
	}
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