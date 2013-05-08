package edu.mccc.cos210.qrks;
import edu.mccc.cos210.qrks.qrcode.*;
import edu.mccc.cos210.qrks.util.*;
import java.awt.Image;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.*;
import java.util.*;

public class Decoder {
	public static final int VISUALIZE_MATRIX_PADDING = 1;
	public static BufferedImage visualizeMatrix(boolean [][] matrix){
		final int size = matrix.length;
		final int larger = size + (VISUALIZE_MATRIX_PADDING * 2);
		BufferedImage bi = new BufferedImage(larger, larger, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = bi.createGraphics();
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, larger, larger);
		g.dispose();
		for(int i = 0; i < size; i++){
			for(int j = 0; j < size; j++){
				if(matrix[i][j]){
					bi.setRGB(i+VISUALIZE_MATRIX_PADDING, j+VISUALIZE_MATRIX_PADDING, 0xFF000000);
				}
			}
		}
		return bi;
	}
	public static Object decode(boolean[][] cleanMatrix, SwingWorkerProtected<?, BufferedImage> swp) {
//		swp.publish(visualizeMatrix(cleanMatrix));
		
		int version = versionInfo(cleanMatrix);
		int formatInfo = formatInfo(cleanMatrix, version); 
		if (version < 1 || version > 40 || formatInfo == -1) {
			System.out.println("<no good>");
			return null;
		}
		System.out.println("version: "+version);
		ErrorCorrectionLevel ec = getECFromFormatInfo(formatInfo);
		System.out.println("error correction: "+ec);
		if (ec == null) {
			System.out.println("<no good>");
			return null;
		}

		int maskNum = getMaskFromFormatInfo(formatInfo);
		System.out.println("mask: "+maskNum);

		if(maskNum == 1){
		BufferedImage img = Utilities.rescaleImage(visualizeMatrix(Mask.generateFinalMask(maskNum, version)), 5);
		Graphics2D g = img.createGraphics();
		g.setColor(Color.GREEN);
		g.setFont(new Font("Dialog", Font.PLAIN, 12));
		g.drawString("Mask "+maskNum, 5, 20);
		g.dispose();
		swp.publish(img);
		}

		//unmask
		boolean [][] maskedMatrix = applyMask(version, cleanMatrix, maskNum);
		System.out.println("unmasked");

		byte[] unsortedData = getDataStream(maskedMatrix, version, swp);
		System.out.println(Arrays.toString(unsortedData));

		byte[][] dataBlocks= sortDataStream(unsortedData, version, ec);
		System.out.println("sorted data stream");
		System.out.println(Arrays.toString(unsortedData));

		byte[][] ecBlocks = sortECStream(unsortedData, version, ec); 
		System.out.println(Arrays.toString(unsortedData));

		Object message = dealWithData(dataBlocks, version, ec);

		System.out.println("dealt with data");
		return message;
	}
	private static int versionInfo(boolean[][] cleanMatrix) {
		int assumedVersion = Version.getClosestVersion(cleanMatrix.length);
		try {
			if(Version.getSize(assumedVersion) > cleanMatrix.length){
				return -1;
			}
		} catch (IllegalArgumentException e) {
			return -1;
		}
		if (assumedVersion < 7) {
			return assumedVersion;
		} else { // assuming version based on size 
			//18-bit
			//6 data bits
			//12 error correction bits calculated using the (18, 6) Golay code. Table D.1 for all 18 bits.
			 
			//vert
			int size = cleanMatrix.length;
			BitBuffer bfh = new BitBuffer(18);
			BitBuffer bfv = new BitBuffer(18);
			/*
			for (int y = 6; y >= 0; y--) {
				for (int x = size - 9; x >= size - 11; x--) {
					boolean b = cleanMatrix[x][y];
					bfh.write(b);		
				}
			}
			
			//horiz
			 	for (int x = 5; x >= 0; x--) {
					for (int y = size - 9; y >= size - 11; y--) {
					boolean b = cleanMatrix[x][y];
					bfv.write(b);
				}
			}*/
			//horiz
			for (int x = 5; x >= 0; x--) {
				for (int y = 9; y <= 11; y ++) {
					boolean b = cleanMatrix[x][size - y];
					bfh.write(b);
				}	
			}
			//vert
			for (int y = 5; y >= 0; y--) {
				for (int x = 9; x <= 11; x++) {
					boolean b = cleanMatrix[size - x][y];
					bfv.write(b);
				}
			}
			bfh.seek(0);
			bfv.seek(0);
			int ibfh = bfh.getIntAndIncrementPosition(18);
			int ibfv = bfv.getIntAndIncrementPosition(18);
			// see if all is well
			CorrectedInfo ci1 = Version.getCorrectedVersionInfo(ibfh);
			CorrectedInfo ci2 = Version.getCorrectedVersionInfo(ibfv);

			if (ci1 == null || ci2 == null) {
				return -1;
			}
			ibfh = ci1.corrected;
			ibfv = ci2.corrected;
			int ibfhVersion = (ibfh >>> 12);
			int ibfvVersion = (ibfv >>> 12);
			if (ibfhVersion == assumedVersion || ibfvVersion == assumedVersion) {
				return assumedVersion;
			}
		}
		return -1;
	}
	private static int formatInfo(boolean[][] cleanMatrix, int version) {
		BitBuffer bf = new BitBuffer(15);
		final int size = cleanMatrix.length;
		for (int y = 1; y <= 7; y++) {	//most significant 14-8
			boolean b = cleanMatrix[8][size - y];
			bf.write(b);
		}
		for (int x = 8; x >= 1; x--) {	//least significant 7-0
			boolean b = cleanMatrix[size - x][8]; 
			bf.write(b);
		}
		
		BitBuffer bf2 = new BitBuffer(15);
		//left side (angle)
		for (int x = 0; x <= 5; x++) {	//14-9
			boolean b = cleanMatrix[x][8];
			bf2.write(b);
		}
		bf2.write(cleanMatrix [7][8]);//8
		bf2.write(cleanMatrix [8][8]);//7
		bf2.write(cleanMatrix [8][7]);//6
		for (int y = 5; y >= 0; y--) {
			boolean b = cleanMatrix [8][y];
			bf2.write(b);
		}
		bf.seek(0);
		bf2.seek(0);
		int ibf = bf.getIntAndIncrementPosition(15);
		int ibf2 = bf2.getIntAndIncrementPosition(15);
		//must release mask 101010000010010
		ibf = ibf ^ (0b101010000010010);
		ibf2 = ibf2 ^ (0b101010000010010);
		// see if all is well
		CorrectedInfo ci1= FormatInfo.getCorrectedFormatInfo(ibf);
		CorrectedInfo ci2 = FormatInfo.getCorrectedFormatInfo(ibf2);
		if (ci1 == null) {
			if (ci2 == null) {
				return -1;
			} else {
				return ci2.corrected;
			}
		} else { 
			if (ci2 == null) {
				return ci1.corrected;
			} else {
				if (ci1.corrected == ci2.corrected) {
					return ci1.corrected;
				}
			}
		}
		return -1;	
	}
	private static ErrorCorrectionLevel getECFromFormatInfo(int formatInfo) {
		return ErrorCorrectionLevel.parseIndex(formatInfo >>> 13);
	}
	private static int getMaskFromFormatInfo(int formatInfo) {
		int mask = 0;
		formatInfo = formatInfo & (0b001110000000000);
		mask = mask | (formatInfo >>> 10);	//doublecheck
		return mask;
	}
	private static boolean[][] applyMask(int version, boolean [][] cleanMatrix, int maskNum) {
		//xor datacleanMatrix with preferredmask
		boolean[][] maskMatrix = Mask.generateFinalMask(maskNum, version);
		for (int i = 0; i < cleanMatrix.length; i++) {
			for (int j = 0; j < cleanMatrix[i].length; j++) {
				maskMatrix[i][j] = cleanMatrix[i][j] ^ maskMatrix[i][j];
			}
		}
		return maskMatrix;
	}
	
	private static byte[] getDataStream(boolean[][] maskedMatrix, int version, SwingWorkerProtected<?, BufferedImage> swp) {
		final int SCALE = 24;
//		BufferedImage img = Utilities.rescaleImage(visualizeMatrix(maskedMatrix), SCALE);
//		Graphics2D g = img.createGraphics();
//		g.setColor(Color.RED);
//		g.setFont(new Font("Dialog", Font.PLAIN, 12));
		boolean[][] mask = Version.getDataMask(version);
		boolean direction = false; //0 = up; 1 = down;
		boolean lastlocation = false; //0 = going right; 1 = going left
		int size = mask.length;
		BitBuffer bf = new BitBuffer(Version.getDataCapacity(version));
		int x = size - 1;
		int y = size - 1;
		for (int i = 0; x >= 0; i++) {
			if (shouldRead(x , y, mask)) {
				boolean b = maskedMatrix[x][y];
				bf.write(b);
//				g.setColor(Color.RED);
//				g.drawString(""+(bf.getPosition() % 8), (x + VISUALIZE_MATRIX_PADDING) * SCALE, (y + VISUALIZE_MATRIX_PADDING + 1) * SCALE);
//				g.setColor(Color.GREEN);
//				g.drawString(""+(bf.getPosition() / 8), (x + VISUALIZE_MATRIX_PADDING) * SCALE, (int)((y + VISUALIZE_MATRIX_PADDING + 0.5) * SCALE));
			}
			if (!direction) {//up
				if (!lastlocation) {//going right
					x--;
				} else {//going left
					if(y == 0) {//next columns
						x--;
						direction = !direction;
						if (x == 6) {//skip column 6 as if it does not exist
							x--;
						}
					} else {
						x++;
						y--;
					}
				}
			} else { //down
				if (!lastlocation) { //going right
					x--;
				} else { //going left
					if(y == size - 1) { //next columns
						x--;
						direction = !direction;
						if (x == 6) {//skip column 6 as if it does not exist
							x--;
						}
					} else {
						x++;
						y++;
					}
				}
			}	
			lastlocation = !lastlocation;
		}
		byte[] unsortedData = bf.getData();
//		swp.publish(img);
		return unsortedData;
	}
	private static boolean shouldRead(int x, int y, boolean[][] dataMask) {
		if (dataMask[x][y]) {//true is good, we write on true
			return true;
		}
		return false;
	}
	
	private static byte[][] sortDataStream(byte[] unsortedData, int version, ErrorCorrectionLevel ec) {
		//need to determine number / length of DataBlocks & number of ECBlocks
		Version.ErrorCorrectionCharacteristic ecc = Version.getErrorCorrectionCharacteristic(version, ec);
		int numberDataBlocks = 0;
		int maxLength = 0;
		int minLength = Integer.MAX_VALUE;
		//get the total number of blocks
		for (int group = 0; group < ecc.errorCorrectionRows.length; group++) {
			numberDataBlocks += ecc.errorCorrectionRows[group].ecBlocks;
		}
		int offset = 0;
		int [] offsets = new int[numberDataBlocks];//for debugging
		byte[][] dataBlocks = new byte[numberDataBlocks][];
		//now set each to the proper length as described in ecc.
		for (int r = 0, row = 0; r < ecc.errorCorrectionRows.length; r++) {
			Version.ErrorCorrectionRow ecr = ecc.errorCorrectionRows[r];
			maxLength = Math.max(maxLength, ecr.k);
			minLength = Math.min(minLength, ecr.k);
			for (int block = 0; block < ecr.ecBlocks; block++) {
				dataBlocks[row] = new byte[ecr.k];
				offsets[row] = offset;//for debugging
				offset += ecr.k;//for debugging
				row++;
			}
		}
		int used = 0;
		for (int k = 0; k < minLength; k++) {
			for (int block = 0; block < numberDataBlocks; block++) {
				dataBlocks[block][k] = unsortedData[used];
				unsortedData[used] = (byte)((offsets[block] + k) & 0xff);//for debugging
				used++;
			}
		}
		for (int k = minLength; k < maxLength; k++) {
			for (int block = 0; block < numberDataBlocks; block++) {
				if (k < dataBlocks[block].length) {
					dataBlocks[block][k] = unsortedData[used];
					unsortedData[used] = (byte)((offsets[block] + k) & 0xff);//for debugging
					used++;
				}
			}
		}
		return dataBlocks;
	}
	private static byte[][] sortECStream(byte[] unsortedData, int version, ErrorCorrectionLevel ec) {
		//create ecBlocks
		Version.ErrorCorrectionCharacteristic ecc = Version.getErrorCorrectionCharacteristic(version, ec);
		int numberECBlocks = ecc.errorCorrectionRows[0].ecBlocks;//#ecBlocks = #dataBlocks
		if (ecc.errorCorrectionRows.length > 1) {
			numberECBlocks = numberECBlocks + ecc.errorCorrectionRows[1].ecBlocks;
		}
		int ecBlockLength = ecc.errorCorrectionRows[0].c - ecc.errorCorrectionRows[0].k; //always same, regardless of dataBlock length
		//create block[][]
		byte[][] ecBlocks = new byte[numberECBlocks][ecBlockLength];
		int totalNumberDataCodeWords = ecc.errorCorrectionRows[0].ecBlocks * ecc.errorCorrectionRows[0].k;
		if (ecc.errorCorrectionRows.length > 1) {	//if there are blocks of different lengths
			totalNumberDataCodeWords = totalNumberDataCodeWords + ecc.errorCorrectionRows[1].ecBlocks * ecc.errorCorrectionRows[1].k;
		}
		int totalNumberECCodeWords = numberECBlocks * ecBlockLength;
		for (int j = 0, k = totalNumberDataCodeWords; j < ecBlockLength; j++) {
			for(int i = 0; i < numberECBlocks; i++) {
				ecBlocks[i][j] = unsortedData[k++];
			}
		}
		return ecBlocks;
	}
	private static Object dealWithData(byte[][] dataBlocks, int version, ErrorCorrectionLevel ec) {
		// create ordered data stream (bitBuffer)
		int size = Version.getDataCapacity(version);
		BitBuffer bf = new BitBuffer(size);
		//put data in buffer
		for (int i = 0; i < dataBlocks.length; i++) {
			for (int j = 0; j < dataBlocks[i].length; j++) {
					bf.write(dataBlocks[i][j], 8);
			}
		}
		//determine length of data from info
		//Encoding Mode (4 bit), Data Size (version 0-9 8 bits, version 10-40 16 bits), Terminator (4 bits), and Padding (to fill DataCapacity) into a BitBuffer
		bf.seek(0);
		/*for (int i = 0; i <= 4; i++){
			em = em | ((bf.getBitAndIncrementPosition()? 1 : 0) << i);
		}
		int dataSize = 0;	
		int dataStartPosition = 0; //where actual dataStarts after prefixes
		if (0 < version && version < 10) {
			for (int i = 8; i>=0; i++){
				dataSize = dataSize | ((bf.getBitAndIncrementPosition()? 1 : 0) << i);
			}
			dataStartPosition = 12; 
		}
		if (9 < version && version < 41) {
			for (int i = 16; i>=0; i++){
				dataSize = dataSize | ((bf.getBitAndIncrementPosition()? 1 : 0) << i);
			}
			dataStartPosition = 16;
		}
		//create another data stream - only  message (ignore prefixes, ignore padding)
		BitBuffer messageBuffer = new BitBuffer(dataSize);
		for (int i = dataStartPosition; i <= dataStartPosition + dataSize; i++) {
			messageBuffer.write(bf.getBitAndIncrementPosition());
		}
		*/
		int emi = bf.getIntAndIncrementPosition(4);
		EncodingMode em = EncodingMode.parseValue(emi);
		System.out.println("raw encoding mode:" + emi + "  parsed: " + em);
		if (em == null) {
			return null;
		}
		int dataCapacity = Version.getSymbolCharacterInfo(ec, version).getDataCapacity(em) * 8;
		switch(em) {
				case BYTE: {
					int dataSize = 0;	
					if (version < 10) {
						dataSize = bf.getIntAndIncrementPosition(8) * 8;
					} else if (version < 41) {
						dataSize = bf.getIntAndIncrementPosition(16) * 8;
					}
					System.out.println("capacity:" + dataCapacity + "  desired: " + dataSize + "  buffer: " + size + "  position: " + bf.getPosition());
					if (dataCapacity < dataSize) {
						return null;
					}
					//create another data stream - only  message (ignore prefixes, ignore padding)
					BitBuffer messageBuffer = new BitBuffer(dataSize);
					for (int i = 0; i < dataSize; i+=8) {
						messageBuffer.write(bf.getBitAndIncrementPosition());
					}
					return messageBuffer.getData();
				}
				case ALPHANUMERIC: {
					int dataSize = 0;	
					if (version < 10) {
						dataSize = bf.getIntAndIncrementPosition(9);
					} else if (version < 27) {
						dataSize = bf.getIntAndIncrementPosition(11);
					} else if (version < 41) {
						dataSize = bf.getIntAndIncrementPosition(13);
					}
					System.out.println("capacity:" + dataCapacity + "  desired: " + dataSize + "  buffer: " + size + "  position: " + bf.getPosition());
					if (dataCapacity < dataSize) {
						return null;
					}
					int p = 0;
					int i = 0;
					char [] chars = new char[dataSize];
					int [] raw = new int[dataSize];
					for (i = 0; i < dataSize; i+=11, p+=2) {
						int e = bf.getIntAndIncrementPosition(11);
						chars[p] = AlphanumericMode.getChar(raw[p] = (e / 45));
						chars[p+1] = AlphanumericMode.getChar(raw[p+1] = (e % 45));
					}
					if(dataSize - i > 5) {
						chars[p] = AlphanumericMode.getChar(raw[p] = bf.getIntAndIncrementPosition(6));
						p++;
					}
					System.out.println(Arrays.toString(raw));
					System.out.println(Arrays.toString(chars));
					return new String(chars);
				}
				case NUMERIC:
					int dataSize = 0;	
					if (version < 10) {
						dataSize = bf.getIntAndIncrementPosition(10);
					} else if (version < 27) {
						dataSize = bf.getIntAndIncrementPosition(12);
					} else if (version < 41) {
						dataSize = bf.getIntAndIncrementPosition(14);
					}
					System.out.println("capacity:" + dataCapacity + "  desired: " + dataSize + "  buffer: " + size + "  position: " + bf.getPosition());
					if (dataCapacity < dataSize) {
						return null;
					}
					int p = 0;
					char [] chars = new char[dataSize];
					for (int i = 0; i < dataSize; i+=10) {
						int e = bf.getIntAndIncrementPosition(10);
						chars[p++] = (char)(((e / 100) % 10) + '0');
						chars[p++] = (char)(((e / 10) % 10) + '0');
						chars[p++] = (char)((e % 10) + '0');
					}
					switch(dataSize % 10) {
						case 9:
						case 8:
						case 7: {
							int e = bf.getIntAndIncrementPosition(7);
							chars[p++] = (char)(((e / 10) % 10) + '0');
							chars[p++] = (char)((e % 10) + '0');
							break;
						}
						case 6:
						case 5:
						case 4:
							chars[p++] = (char)((bf.getIntAndIncrementPosition(4) % 10) + '0');
							break;
					}
					return new String(chars);
				case ECI:
				case KANJI:
				case STRUCTURED_APPEND:
				case FNC1_FIRST:
				case FNC1_SECOND:
				case TERMINATOR:
//					throw new UnsupportedOperationException(em + " mode is not supported.");
				default:
//					throw new UnsupportedOperationException("Unrecognizable mode: "+emi);
		}
		return null;
	}
}
