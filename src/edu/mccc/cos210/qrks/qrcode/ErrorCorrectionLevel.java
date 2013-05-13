package edu.mccc.cos210.qrks.qrcode;
import java.util.*;
import javax.swing.*;
import java.awt.*;

public enum ErrorCorrectionLevel {
	L(0b01, 7),
	M(0b00, 15),
	Q(0b11, 25),
	H(0b10, 30);
	private static ErrorCorrectionLevel[] ecls;
	static {
		ErrorCorrectionLevel[] t = ErrorCorrectionLevel.values();
		ecls = new ErrorCorrectionLevel[t.length];
		for (ErrorCorrectionLevel ecl : t) {
			ecls[ecl.value] = ecl;
		}
	}
	private final int value;
	private final int percentage;
	private ErrorCorrectionLevel(final int value, final int percentage) {
		this.value = value;
		this.percentage = percentage;
	}
	public int getValue() {
		return value;
	}
	public int getPercentage() {
		return percentage;
	}
	public String toString() {
		return getName() + " ~ " + percentage + "%";
	}
	public String getName() {
		return super.toString();
	}
	public static ErrorCorrectionLevel parseValue(final int value) {
		return ecls[value];
	}
	public Characteristic getCharacteristic(int version){
		return Characteristic.ecc[version -1][this.value ^ 1];
	}
	public SymbolCharacterInfo getSymbolCharacterInfo(int version) {
		return getSymbolCharacterInfos()[version - 1];
	}
	public SymbolCharacterInfo[] getSymbolCharacterInfos() {
		return SymbolCharacterInfo.nosc[this.value ^ 1];//we mapped ErrorCorrectionLevel wrong initially
	}
	public static class Characteristic {
		public final Row [] errorCorrectionRows;
		public final int ecCodewords;
		public Characteristic(int ecCodewords, Characteristic.Row ... ecrs) {
			this.ecCodewords = ecCodewords;
			this.errorCorrectionRows = ecrs;
		}
		public static class Row {
			public final int p;
			public final int ecBlocks;
			public final int c;	//total # of codewords
			public final int k;	//# dataCodewords
			public final int r; //error correction capacity
			public Row(int p, int ecBlocks, int c, int k, int r) {
				this.p = p;
				this.ecBlocks = ecBlocks;
				this.c = c;
				this.k = k;
				this.r = r;
			}
		}
		private static Characteristic [][] ecc = {
			/*1, 26*/ { new Characteristic(/*L, */7, new Row(3, 1, 26, 19, 2)), new Characteristic(/*M, */10, new Row(2, 1, 26, 16, 4)), new Characteristic(/*Q, */13, new Row(1, 1, 26, 13, 6)), new Characteristic(/*H, */17, new Row(1, 1, 26, 9, 8)), },
			/*2, 44*/ { new Characteristic(/*L, */10, new Row(2, 1, 44, 34, 4)), new Characteristic(/*M, */16, new Row(0, 1, 44, 28, 8)), new Characteristic(/*Q, */22, new Row(0, 1, 44, 22, 11)), new Characteristic(/*H, */28, new Row(0, 1, 44, 16, 14)), },
			/*3, 70*/ { new Characteristic(/*L, */15, new Row(1, 1, 70, 55, 7)), new Characteristic(/*M, */26, new Row(0, 1, 70, 44, 13)), new Characteristic(/*Q, */36, new Row(0, 2, 35, 17, 9)), new Characteristic(/*H, */44, new Row(0, 2, 35, 13, 11)), },
			/*4, 100*/ { new Characteristic(/*L, */20, new Row(0, 1, 100, 80, 10)), new Characteristic(/*M, */36, new Row(0, 2, 50, 32, 9)), new Characteristic(/*Q, */52, new Row(0, 2, 50, 24, 13)), new Characteristic(/*H, */64, new Row(0, 4, 25, 9, 8)), },
			/*5, 134*/ { new Characteristic(/*L, */26, new Row(0, 1, 134, 108, 13)), new Characteristic(/*M, */48, new Row(0, 2, 67, 43, 12)), new Characteristic(/*Q, */72, new Row(0, 2, 33, 15, 9), new Row(0, 2, 34, 16, 9)), new Characteristic(/*H, */88, new Row(0, 2, 33, 11, 11), new Row(0, 2, 34, 12, 11)), },
			/*6, 172*/ { new Characteristic(/*L, */36, new Row(0, 2, 86, 68, 9)), new Characteristic(/*M, */64, new Row(0, 4, 43, 27, 8)), new Characteristic(/*Q, */96, new Row(0, 4, 43, 19, 12)), new Characteristic(/*H, */112, new Row(0, 4, 43, 15, 14)), },
			/*7, 196*/ { new Characteristic(/*L, */40, new Row(0, 2, 98, 78, 10)), new Characteristic(/*M, */72, new Row(0, 4, 49, 31, 9)), new Characteristic(/*Q, */108, new Row(0, 2, 32, 14, 9), new Row(0, 4, 33, 15, 9)), new Characteristic(/*H, */130, new Row(0, 4, 39, 13, 13), new Row(0, 1, 40, 14, 13)), },
			/*8, 242*/ { new Characteristic(/*L, */48, new Row(0, 2, 121, 97, 12)), new Characteristic(/*M, */88, new Row(0, 2, 60, 38, 11), new Row(0, 2, 61, 39, 11)), new Characteristic(/*Q, */132, new Row(0, 4, 40, 18, 11), new Row(0, 2, 41, 19, 11)), new Characteristic(/*H, */156, new Row(0, 4, 40, 14, 13), new Row(0, 2, 41, 15, 13)), },
			/*9, 292*/ { new Characteristic(/*L, */60, new Row(0, 2, 146, 116, 15)), new Characteristic(/*M, */110, new Row(0, 3, 58, 36, 11), new Row(0, 2, 59, 37, 11)), new Characteristic(/*Q, */160, new Row(0, 4, 36, 16, 10), new Row(0, 4, 37, 17, 10)), new Characteristic(/*H, */192, new Row(0, 4, 36, 12, 12), new Row(0, 4, 37, 13, 12)), },
			/*10, 346*/ { new Characteristic(/*L, */72, new Row(0, 2, 86, 68, 9), new Row(0, 2, 87, 69, 9)), new Characteristic(/*M, */130, new Row(0, 4, 69, 43, 13), new Row(0, 1, 70, 44, 13)), new Characteristic(/*Q, */192, new Row(0, 6, 43, 19, 12), new Row(0, 2, 44, 20, 12)), new Characteristic(/*H, */224, new Row(0, 6, 43, 15, 14), new Row(0, 2, 44, 16, 14)), },
			/*11, 404*/ { new Characteristic(/*L, */80, new Row(0, 4, 101, 81, 10)), new Characteristic(/*M, */150, new Row(0, 1, 80, 50, 15), new Row(0, 4, 81, 51, 15)), new Characteristic(/*Q, */224, new Row(0, 4, 50, 22, 14), new Row(0, 4, 51, 23, 14)), new Characteristic(/*H, */264, new Row(0, 3, 36, 12, 12), new Row(0, 8, 37, 13, 12)), },
			/*12, 466*/ { new Characteristic(/*L, */96, new Row(0, 2, 116, 92, 12), new Row(0, 2, 117, 93, 12)), new Characteristic(/*M, */176, new Row(0, 6, 58, 36, 11), new Row(0, 2, 59, 37, 11)), new Characteristic(/*Q, */260, new Row(0, 4, 46, 20, 13), new Row(0, 6, 47, 21, 13)), new Characteristic(/*H, */308, new Row(0, 7, 42, 14, 14), new Row(0, 4, 43, 15, 14)), },
			/*13, 532*/ { new Characteristic(/*L, */104, new Row(0, 4, 133, 107, 13)), new Characteristic(/*M, */198, new Row(0, 8, 59, 37, 11), new Row(0, 1, 60, 38, 11)), new Characteristic(/*Q, */288, new Row(0, 8, 44, 20, 12), new Row(0, 4, 45, 21, 12)), new Characteristic(/*H, */352, new Row(0, 12, 33, 11, 11), new Row(0, 4, 34, 12, 11)), },
			/*14, 581*/ { new Characteristic(/*L, */120, new Row(0, 3, 145, 115, 15), new Row(0, 1, 146, 116, 15)), new Characteristic(/*M, */216, new Row(0, 4, 64, 40, 12), new Row(0, 5, 65, 41, 12)), new Characteristic(/*Q, */320, new Row(0, 11, 36, 16, 10), new Row(0, 5, 37, 17, 10)), new Characteristic(/*H, */384, new Row(0, 11, 36, 12, 12), new Row(0, 5, 37, 13, 12)), },
			/*15, 655*/ { new Characteristic(/*L, */132, new Row(0, 5, 109, 87, 11), new Row(0, 1, 110, 88, 11)), new Characteristic(/*M, */240, new Row(0, 5, 65, 41, 12), new Row(0, 5, 66, 42, 12)), new Characteristic(/*Q, */360, new Row(0, 5, 54, 24, 15), new Row(0, 7, 55, 25, 15)), new Characteristic(/*H, */432, new Row(0, 11, 36, 12, 12), new Row(0, 7, 37, 13, 12)), },
			/*16, 733*/ { new Characteristic(/*L, */144, new Row(0, 5, 122, 98, 12), new Row(0, 1, 123, 99, 12)), new Characteristic(/*M, */280, new Row(0, 7, 73, 45, 14), new Row(0, 3, 74, 46, 14)), new Characteristic(/*Q, */408, new Row(0, 15, 43, 19, 12), new Row(0, 2, 44, 20, 12)), new Characteristic(/*H, */480, new Row(0, 3, 45, 15, 15), new Row(0, 13, 46, 16, 15)), },
			/*17, 815*/ { new Characteristic(/*L, */168, new Row(0, 1, 135, 107, 14), new Row(0, 5, 136, 108, 14)), new Characteristic(/*M, */308, new Row(0, 10, 74, 46, 14), new Row(0, 1, 75, 47, 14)), new Characteristic(/*Q, */448, new Row(0, 1, 50, 22, 14), new Row(0, 15, 51, 23, 14)), new Characteristic(/*H, */532, new Row(0, 2, 42, 14, 14), new Row(0, 17, 43, 15, 14)), },
			/*18, 901*/ { new Characteristic(/*L, */180, new Row(0, 5, 150, 120, 15), new Row(0, 1, 151, 121, 15)), new Characteristic(/*M, */338, new Row(0, 9, 69, 43, 13), new Row(0, 4, 70, 44, 13)), new Characteristic(/*Q, */504, new Row(0, 17, 50, 22, 14), new Row(0, 1, 51, 23, 14)), new Characteristic(/*H, */588, new Row(0, 2, 42, 14, 14), new Row(0, 19, 43, 15, 14)), },
			/*19, 991*/ { new Characteristic(/*L, */196, new Row(0, 3, 141, 113, 14), new Row(0, 4, 142, 114, 14)), new Characteristic(/*M, */364, new Row(0, 3, 70, 44, 13), new Row(0, 11, 71, 45, 13)), new Characteristic(/*Q, */546, new Row(0, 17, 47, 21, 13), new Row(0, 4, 48, 22, 13)), new Characteristic(/*H, */650, new Row(0, 9, 39, 13, 13), new Row(0, 16, 40, 14, 13)), },
			/*20, 1085*/ { new Characteristic(/*L, */224, new Row(0, 3, 135, 107, 14), new Row(0, 5, 136, 108, 14)), new Characteristic(/*M, */416, new Row(0, 3, 67, 41, 13), new Row(0, 13, 68, 42, 13)), new Characteristic(/*Q, */600, new Row(0, 15, 54, 24, 15), new Row(0, 5, 55, 25, 15)), new Characteristic(/*H, */700, new Row(0, 15, 43, 15, 14), new Row(0, 10, 44, 16, 14)), },
			/*21, 1156*/ { new Characteristic(/*L, */224, new Row(0, 4, 144, 116, 14)), new Characteristic(/*M, */442, new Row(0, 17, 68, 42, 13)), new Characteristic(/*Q, */644, new Row(0, 17, 50, 22, 14)), new Characteristic(/*H, */750, new Row(0, 19, 46, 16, 15)), },
			/*22, 1258*/ { new Characteristic(/*L, */252, new Row(0, 2, 139, 111, 14), new Row(0, 7, 140, 112, 14)), new Characteristic(/*M, */476, new Row(0, 17, 74, 46, 14)), new Characteristic(/*Q, */690, new Row(0, 7, 54, 24, 15), new Row(0, 16, 55, 25, 15)), new Characteristic(/*H, */816, new Row(0, 34, 37, 13, 12)), },
			/*23, 1364*/ { new Characteristic(/*L, */270, new Row(0, 4, 151, 121, 15), new Row(0, 5, 152, 122, 15)), new Characteristic(/*M, */504, new Row(0, 4, 75, 47, 14), new Row(0, 14, 76, 48, 14)), new Characteristic(/*Q, */750, new Row(0, 11, 54, 24, 15), new Row(0, 14, 55, 25, 15)), new Characteristic(/*H, */900, new Row(0, 16, 45, 15, 15), new Row(0, 14, 46, 16, 15)), },
			/*24, 1474*/ { new Characteristic(/*L, */300, new Row(0, 6, 147, 117, 15), new Row(0, 4, 148, 118, 15)), new Characteristic(/*M, */560, new Row(0, 6, 73, 45, 14), new Row(0, 14, 74, 46, 14)), new Characteristic(/*Q, */810, new Row(0, 11, 54, 24, 15), new Row(0, 16, 55, 25, 15)), new Characteristic(/*H, */960, new Row(0, 30, 46, 16, 15), new Row(0, 2, 47, 17, 15)), },
			/*25, 1588*/ { new Characteristic(/*L, */312, new Row(0, 8, 132, 106, 13), new Row(0, 4, 133, 107, 13)), new Characteristic(/*M, */588, new Row(0, 8, 75, 47, 14), new Row(0, 13, 76, 48, 14)), new Characteristic(/*Q, */870, new Row(0, 7, 54, 24, 15), new Row(0, 22, 55, 25, 15)), new Characteristic(/*H, */1050, new Row(0, 22, 45, 15, 15), new Row(0, 13, 46, 16, 15)), },
			/*26, 1706*/ { new Characteristic(/*L, */336, new Row(0, 10, 142, 114, 14), new Row(0, 2, 143, 115, 14)), new Characteristic(/*M, */644, new Row(0, 19, 74, 46, 14), new Row(0, 4, 75, 47, 14)), new Characteristic(/*Q, */952, new Row(0, 28, 50, 22, 14), new Row(0, 6, 51, 23, 14)), new Characteristic(/*H, */1110, new Row(0, 33, 46, 16, 15), new Row(0, 4, 47, 17, 15)), },
			/*27, 1828*/ { new Characteristic(/*L, */360, new Row(0, 8, 152, 122, 15), new Row(0, 4, 153, 123, 15)), new Characteristic(/*M, */700, new Row(0, 22, 73, 45, 14), new Row(0, 3, 74, 46, 14)), new Characteristic(/*Q, */1020, new Row(0, 8, 53, 23, 15), new Row(0, 26, 54, 24, 15)), new Characteristic(/*H, */1200, new Row(0, 12, 45, 15, 15), new Row(0, 28, 46, 16, 15)), },
			/*28, 1921*/ { new Characteristic(/*L, */390, new Row(0, 3, 147, 117, 15), new Row(0, 10, 148, 118, 15)), new Characteristic(/*M, */728, new Row(0, 3, 73, 45, 14), new Row(0, 23, 74, 46, 14)), new Characteristic(/*Q, */1050, new Row(0, 4, 54, 24, 15), new Row(0, 31, 55, 25, 15)), new Characteristic(/*H, */1260, new Row(0, 11, 45, 15, 15), new Row(0, 31, 46, 16, 15)), },
			/*29, 2051*/ { new Characteristic(/*L, */420, new Row(0, 7, 146, 116, 15), new Row(0, 7, 147, 117, 15)), new Characteristic(/*M, */784, new Row(0, 21, 73, 45, 14), new Row(0, 7, 74, 46, 14)), new Characteristic(/*Q, */1140, new Row(0, 1, 53, 23, 15), new Row(0, 37, 54, 24, 15)), new Characteristic(/*H, */1350, new Row(0, 19, 45, 15, 15), new Row(0, 26, 46, 16, 15)), },
			/*30, 2185*/ { new Characteristic(/*L, */450, new Row(0, 5, 145, 115, 15), new Row(0, 10, 146, 116, 15)), new Characteristic(/*M, */812, new Row(0, 19, 75, 47, 14), new Row(0, 10, 76, 48, 14)), new Characteristic(/*Q, */1200, new Row(0, 15, 54, 24, 15), new Row(0, 25, 55, 25, 15)), new Characteristic(/*H, */1440, new Row(0, 23, 45, 15, 15), new Row(0, 25, 46, 16, 15)), },
			/*31, 2323*/ { new Characteristic(/*L, */480, new Row(0, 13, 145, 115, 15), new Row(0, 3, 146, 116, 15)), new Characteristic(/*M, */868, new Row(0, 2, 74, 46, 14), new Row(0, 29, 75, 47, 14)), new Characteristic(/*Q, */1290, new Row(0, 42, 54, 24, 15), new Row(0, 1, 55, 25, 15)), new Characteristic(/*H, */1530, new Row(0, 23, 45, 15, 15), new Row(0, 28, 46, 16, 15)), },
			/*32, 2465*/ { new Characteristic(/*L, */510, new Row(0, 17, 145, 115, 15)), new Characteristic(/*M, */924, new Row(0, 10, 74, 46, 14), new Row(0, 23, 75, 47, 14)), new Characteristic(/*Q, */1350, new Row(0, 10, 54, 24, 15), new Row(0, 35, 55, 25, 15)), new Characteristic(/*H, */1620, new Row(0, 19, 45, 15, 15), new Row(0, 35, 46, 16, 15)), },
			/*33, 2611*/ { new Characteristic(/*L, */540, new Row(0, 17, 145, 115, 15), new Row(0, 1, 146, 116, 15)), new Characteristic(/*M, */980, new Row(0, 14, 74, 46, 14), new Row(0, 21, 75, 47, 14)), new Characteristic(/*Q, */1440, new Row(0, 29, 54, 24, 15), new Row(0, 19, 55, 25, 15)), new Characteristic(/*H, */1710, new Row(0, 11, 45, 15, 15), new Row(0, 46, 46, 16, 15)), },
			/*34, 2761*/ { new Characteristic(/*L, */570, new Row(0, 13, 145, 115, 15), new Row(0, 6, 146, 116, 15)), new Characteristic(/*M, */1036, new Row(0, 14, 74, 46, 14), new Row(0, 23, 75, 47, 14)), new Characteristic(/*Q, */1530, new Row(0, 44, 54, 24, 15), new Row(0, 7, 55, 25, 15)), new Characteristic(/*H, */1800, new Row(0, 59, 46, 16, 15), new Row(0, 1, 47, 17, 15)), },
			/*35, 2876*/ { new Characteristic(/*L, */570, new Row(0, 12, 151, 121, 15), new Row(0, 7, 152, 122, 15)), new Characteristic(/*M, */1064, new Row(0, 12, 75, 47, 14), new Row(0, 26, 76, 48, 14)), new Characteristic(/*Q, */1590, new Row(0, 39, 54, 24, 15), new Row(0, 14, 55, 25, 15)), new Characteristic(/*H, */1890, new Row(0, 22, 45, 15, 15), new Row(0, 41, 46, 16, 15)), },
			/*36, 3034*/ { new Characteristic(/*L, */600, new Row(0, 6, 151, 121, 15), new Row(0, 14, 152, 122, 15)), new Characteristic(/*M, */1120, new Row(0, 6, 75, 47, 14), new Row(0, 34, 76, 48, 14)), new Characteristic(/*Q, */1680, new Row(0, 46, 54, 24, 15), new Row(0, 10, 55, 25, 15)), new Characteristic(/*H, */1980, new Row(0, 2, 45, 15, 15), new Row(0, 64, 46, 16, 15)), },
			/*37, 3196*/ { new Characteristic(/*L, */630, new Row(0, 17, 152, 122, 15), new Row(0, 4, 153, 123, 15)), new Characteristic(/*M, */1204, new Row(0, 29, 74, 46, 14), new Row(0, 14, 75, 47, 14)), new Characteristic(/*Q, */1770, new Row(0, 49, 54, 24, 15), new Row(0, 10, 55, 25, 15)), new Characteristic(/*H, */2100, new Row(0, 24, 45, 15, 15), new Row(0, 46, 46, 16, 15)), },
			/*38, 3362*/ { new Characteristic(/*L, */660, new Row(0, 4, 152, 122, 15), new Row(0, 18, 153, 123, 15)), new Characteristic(/*M, */1260, new Row(0, 13, 74, 46, 14), new Row(0, 32, 75, 47, 14)), new Characteristic(/*Q, */1860, new Row(0, 48, 54, 24, 15), new Row(0, 14, 55, 25, 15)), new Characteristic(/*H, */2220, new Row(0, 42, 45, 15, 15), new Row(0, 32, 46, 16, 15)), },
			/*39, 3532*/ { new Characteristic(/*L, */720, new Row(0, 20, 147, 117, 15), new Row(0, 4, 148, 118, 15)), new Characteristic(/*M, */1316, new Row(0, 40, 75, 47, 14), new Row(0, 7, 76, 48, 14)), new Characteristic(/*Q, */1950, new Row(0, 43, 54, 24, 15), new Row(0, 22, 55, 25, 15)), new Characteristic(/*H, */2310, new Row(0, 10, 45, 15, 15), new Row(0, 67, 46, 16, 15)), },
			/*40, 3706*/ { new Characteristic(/*L, */750, new Row(0, 19, 148, 118, 15), new Row(0, 6, 149, 119, 15)), new Characteristic(/*M, */1372, new Row(0, 18, 75, 47, 14), new Row(0, 31, 76, 48, 14)), new Characteristic(/*Q, */2040, new Row(0, 34, 54, 24, 15), new Row(0, 34, 55, 25, 15)), new Characteristic(/*H, */2430, new Row(0, 20, 45, 15, 15), new Row(0, 61, 46, 16, 15)), },
		};

	}
}