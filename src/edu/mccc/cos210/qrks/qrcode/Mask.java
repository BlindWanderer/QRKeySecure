package edu.mccc.cos210.qrks.qrcode;
/**
 * Generates masks for a QRCode to avoid large areas of same color and objects that look like finding patterns.
 *
 */
public class Mask { 
	static public boolean[][] generateSubMask (int maskNum, int version) {
		int size = Version.getSize(version);
		boolean[][] mask = new boolean[size][size];
		switch(maskNum) {
			case 0b000:
				for (int i = 0; i < Version.getSize(version); i++) {
					for (int j = 0; j < Version.getSize(version); j++) {
						mask[j][i] = (i+j) % 2 == 0;
					}
				}
				return mask;
				
			case 0b001:
				for (int i = 0; i < Version.getSize(version); i++) {
					for (int j = 0; j < Version.getSize(version); j++) {
						mask[j][i] = i % 2 == 0;
					}
				}
				return mask;
				
			case 0b010:
				for (int i = 0; i < Version.getSize(version); i++) {
					for (int j = 0; j < Version.getSize(version); j++) {
						mask[j][i] = j % 3 == 0;
					}
				}
				return mask;
				
			case 0b011:
				for (int i = 0; i < Version.getSize(version); i++) {
					for (int j = 0; j < Version.getSize(version); j++) {
						mask[j][i] = (i+j) % 3 == 0;
					}
				}
				return mask;
				
			case 0b100:
				for (int i = 0; i < Version.getSize(version); i++) {
					for (int j = 0; j < Version.getSize(version); j++) {
						mask[j][i] = ((i / 2) + (j / 3)) % 2 == 0;
					}
				}
				return mask;
				
			case 0b101:
				for (int i = 0; i < Version.getSize(version); i++) {
					for (int j = 0; j < Version.getSize(version); j++) {
						mask[j][i] = (i * j) % 2 + (i * j) % 3 == 0;
					}
				}
				return mask;
				
			case 0b110:
				for (int i = 0; i < Version.getSize(version); i++) {
					for (int j = 0; j < Version.getSize(version); j++) {
						mask[j][i] = ((i* j) % 2 + (i *j) % 3) % 2 == 0;
					}
				}
				return mask;
			
			case 0b111:
				for (int i = 0; i < Version.getSize(version); i++) {
					for (int j = 0; j < Version.getSize(version); j++) {
						mask[j][i] = ((i* j) % 3 + (i+j) % 2) % 2 == 0;
					}
				}
				return mask;
			default:
				return null;
		}
	}
	/**
	 * Bit-wise AND of two boolean[][] of same size.
	 * @param left DataField
	 * @param right Mask
	 * @return new Boolean[][] where the each value is left value ANDed with right value.
	 */
	public static boolean[][] and(boolean [][] left, boolean [][] right) {
		boolean[][] ret = new boolean[left.length][left[0].length];
		for (int j = 0; j < ret.length; j++) {
			for (int i = 0; i < ret[j].length; i++) {
				ret[j][i] = left[j][i] & right[j][i];
			}
		}
		return ret;
	}
	/**
	 * Bit-wise XOR of two boolean[][] of same size.
	 * @param left DataField
	 * @param right Mask
	 * @return new Boolean[][] where the each value is left value XORed with right value.
	 */
	public static boolean[][] xor(boolean [][] left, boolean [][] right) {
		boolean[][] ret = new boolean[left.length][left[0].length];
		for (int j = 0; j < ret.length; j++) {
			for (int i = 0; i < ret[j].length; i++) {
				ret[j][i] = left[j][i] ^ right[j][i];
			}
		}
		return ret;
	}
	/**
	 * Bit-wise NOT of two boolean[][] of same size.
	 * @param left DataField
	 * @param right Mask
	 * @return new Boolean[][] where the each value is left value NOTed with right value.
	 */
	public static boolean[][] not(boolean [][] right) {
		boolean[][] ret = new boolean[right.length][right[0].length];
		for (int j = 0; j < ret.length; j++) {
			for (int i = 0; i < ret[j].length; i++) {
				ret[j][i] = !right[j][i];
			}
		}
		return ret;
	}
	/**
	 * Bit-wise OR of two boolean[][] of same size.
	 * @param left DataField
	 * @param right Mask
	 * @return new Boolean[][] where the each value is left value ORed with right value.
	 */
	public static boolean[][] or(boolean [][] left, boolean [][] right) {
		boolean[][] ret = new boolean[left.length][left[0].length];
		for (int j = 0; j < ret.length; j++) {
			for (int i = 0; i < ret[j].length; i++) {
				ret[j][i] = left[j][i] | right[j][i];
			}
		}
		return ret;
	}
	/**
	 * Bit-wise NOT of two boolean[][] of same size; overwrites the boolean[][] with resulting values.
	 * @param overwrite DataField
	 * @return new Boolean[][] where the each value is left value NOTed with right value.
	 */
	public static boolean[][] notOverwrite(boolean [][] overwrite) {
		for (int j = 0; j < overwrite.length; j++) {
			for (int i = 0; i < overwrite[j].length; i++) {
				overwrite[j][i] = !overwrite[j][i];
			}
		}
		return overwrite;
	}
	/**
	 * Bit-wise AND of two boolean[][] of same size; overwrites the left boolean[][] with resulting values.
	 * @param overwrite DataField
	 * @param right Mask
	 * @return left DataField, where the each value is left value ANDed with right value.
	 */
	public static boolean[][] andOverwrite(boolean [][] overwrite, boolean [][] right) {
		for (int j = 0; j < overwrite.length; j++) {
			for (int i = 0; i < overwrite[j].length; i++) {
				overwrite[j][i] = overwrite[j][i] & right[j][i];
			}
		}
		return overwrite;
	}
	/**
	 * Bit-wise OR of two boolean[][] of same size; overwrites the left boolean[][] with resulting values.
	 * @param overwrite DataField
	 * @param right Mask
	 * @return left DataField, where the each value is left value ORed with right value.
	 */
	public static boolean[][] orOverwrite(boolean [][] overwrite, boolean [][] right) {
		for (int j = 0; j < overwrite.length; j++) {
			for (int i = 0; i < overwrite[j].length; i++) {
				overwrite[j][i] = overwrite[j][i] | right[j][i];
			}
		}
		return overwrite;
	}
	/**
	 * Bit-wise XOR of two boolean[][] of same size; overwrites the left boolean[][] with resulting values.
	 * @param overwrite DataField
	 * @param right Mask
	 * @return left DataField, where the each value is left value XORed with right value.
	 */
	public static boolean[][] xorOverwrite(boolean [][] overwrite, boolean [][] right) {
		for (int j = 0; j < overwrite.length; j++) {
			for (int i = 0; i < overwrite[j].length; i++) {
				overwrite[j][i] = overwrite[j][i] ^ right[j][i];
			}
		}
		return overwrite;
	}
	/**
	 * Generates a uniform pattern mask to visually breakup data on a generated QRCode.
	 * @param num number of the mask (0-8)
	 * @param version of thie QRCode
	 * @return boolean[][] a mask
	 */
	public static boolean[][] generateFinalMask(int num, int version) {
		return andOverwrite(generateSubMask(num, version), Version.getDataMask(version));
	}
}