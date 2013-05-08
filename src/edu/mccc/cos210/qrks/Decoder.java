package edu.mccc.cos210.qrks;

import edu.mccc.cos210.qrks.qrcode.*;
import edu.mccc.cos210.qrks.util.*;
import java.awt.*;
import java.awt.image.*;

public class Decoder {
	public static BufferedImage visualizeMatrix(boolean [][] matrix){
		final int buffer = 3;
		final int size = matrix.length;
		final int larger = size + (buffer * 2);
		BufferedImage bi = new BufferedImage(larger, larger, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = bi.createGraphics();
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, larger, larger);
		g.dispose();
		for(int i = 0; i < size; i++){
			for(int j = 0; j < size; j++){
				if(matrix[i][j]){
					bi.setRGB(i+buffer, j+buffer, 0xFF000000);
				}
			}
		}
		return bi;
	}
	public static byte[] decode(boolean[][] cleanMatrix, SwingWorkerProtected<?, BufferedImage> swp) {

		swp.publish(visualizeMatrix(cleanMatrix));
		
		byte[] message = null;
		int version = versionInfo(cleanMatrix);
		int formatInfo = formatInfo(cleanMatrix, version); 
		if (version == -1 || formatInfo == -1) {
			return null;
		}
		int ec = getECFromFormatInfo(formatInfo);
		System.out.println(ec);
		int maskNum = getMaskFromFormatInfo(formatInfo);
		System.out.println(maskNum);
		//unmask
		applyMask(version, cleanMatrix, maskNum);
		System.out.println("unmasked");
		byte[] unsortedData = getDataStream(cleanMatrix, version);
		System.out.println(unsortedData);
		byte[][] dataBlocks= sortDataStream(unsortedData, version, ec);
		System.out.println("sorted data stream");
		byte[][] ecBlocks = sortECStream(unsortedData, version, ec); 
		message = dealWithData(dataBlocks, version);
		System.out.println("dealt with data");
		return message;
	}
	private static int versionInfo(boolean[][] cleanMatrix) {
		int assumedVersion = Version.getClosestVersion(cleanMatrix.length);
		int version = 0;
		if (assumedVersion < 7) {
			return assumedVersion;
		}
		if (assumedVersion >= 7) { // assuming version based on size 
			//18-bit
			//6 data bits
			//12 error correction bits calculated using the (18, 6) Golay code. Table D.1 for all 18 bits.
			 
			//vert
			int size = cleanMatrix.length;
			BitBuffer bf = new BitBuffer(18);
			BitBuffer bf2 = new BitBuffer(18);
			/*
			for (int y = 6; y >= 0; y--) {
				for (int x = size - 9; x >= size - 11; x--) {
					boolean b = cleanMatrix[x][y];
					bf.write(b);		
				}
			}
			
			//horiz
			 	for (int x = 5; x >= 0; x--) {
					for (int y = size - 9; y >= size - 11; y--) {
					boolean b = cleanMatrix[x][y];
					bf2.write(b);
				}
			}*/
			//horiz
			for (int x = 5; x >= 0; x--) {
				for (int y = 9; y <= 11; y ++) {
					boolean b = cleanMatrix[x][size - y];
					bf.write(b);
				}	
			}
			//vert
			bf.seek(0);
			for (int y = 5; y >= 0; y--) {
				for (int x = 9; x <= 11; x++) {
					boolean b = cleanMatrix[size - x][y];
					bf2.write(b);
				}
			}
			bf.seek(0);
			bf2.seek(0);
			int ibf = bf.getIntAndIncrementPosition(18);
			int ibf2 = bf.getIntAndIncrementPosition(18);
			// see if all is well
			CorrectedInfo ci1 = Version.getCorrectedVersionInfo(ibf);
			CorrectedInfo ci2 = Version.getCorrectedVersionInfo(ibf2);

			if (ci1 == null) {
				if (ci2 == null) {
					return -1;
				} else {
					ibf = ibf2 = ci2.corrected;
				}
			} else { 
				if (ci2 == null) {
					ibf = ibf2 = ci1.corrected;
				} else {
					ibf = ci1.corrected;
					ibf2 = ci2.corrected;
				}
			}
			int ibfVersion = 0 | (ibf >>> 12);
			int ibf2Version = 0 | (ibf2 >>> 12);
			if (ibf == ibf2) {
				version = ibfVersion;
			} 
			if (ibfVersion != assumedVersion) {
				return -1;
			}
			if (ibf != ibf2) {
				if (ibfVersion == assumedVersion || ibf2Version == assumedVersion) {
					version = assumedVersion;
				}
				if (ibfVersion != assumedVersion && ibf2Version != assumedVersion) {
					return -1; 
				}
			}
		}
		return version;
	}
	private static int formatInfo(boolean[][] cleanMatrix, int version) {
		BitBuffer bf = new BitBuffer(15);
		final int size = Version.getSize(version);
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
	private static int getECFromFormatInfo(int formatInfo) {
		int ec = 0;
		ec = ec | (formatInfo >>> 13);	//doublecheck
		return ec;
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
				cleanMatrix[i][j] = cleanMatrix[i][j] ^ maskMatrix[i][j];
			}
		}
		return cleanMatrix;
	}
	
	private static byte[] getDataStream(boolean[][] cleanMatrix, int version) {
		boolean[][] mask = Version.getDataMask(version);
		boolean direction = false; //0 = up; 1 = down;
		boolean lastlocation = false; //0 = going right; 1 = going left
		int size = cleanMatrix.length;
		BitBuffer bf = new BitBuffer(size * size); //too large, but who cares.
		int x = size - 1;
		int y = size - 1;
		for (int i = 0; x >= 0; i++) {
			if (shouldRead(x , y, mask)) {
				boolean b = cleanMatrix[x][y];
				bf.write(b);
			}
			if (!direction) {//up
				if (!lastlocation) {//going right
					x--;
				} else
				if (lastlocation) {//going left
					if(y == 0) {
						x--;
						direction = !direction;
					} else {
						x++;
						y--;
					}
				}
			} else 
			if (direction) { //down
				if (!lastlocation) { //going right
					x--;
				} else
				if (lastlocation) { //going left
					if(y == size - 1) {	
						x--;
						direction = !direction;
					} else {
						x++;
						y++;
					}
				}
			}	
			lastlocation = !lastlocation;
		}
		byte[] unsortedData = bf.getData();
		return unsortedData;
	}
	private static boolean shouldRead(int x, int y, boolean[][] dataMask) {
		if (dataMask[x][y]) {//true is good, we write on true
			return true;
		}
		return false;
	}
	
	private static byte[][] sortDataStream(byte[] unsortedData, int version, int ec) {
		//need to determine number / length of DataBlocks & number of ECBlocks
		Version.ErrorCorrectionCharacteristic ecc = Version.getErrorCorrectionCharacteristic(version, ErrorCorrectionLevel.parseIndex(ec));
		int numberShortDataBlocks = ecc.errorCorrectionRows[0].ecBlocks;
		int numberDataBlocks = numberShortDataBlocks;//#ecBlocks = #dataBlocks
		int longDataBlockLength = 0;
		int shortDataBlockLength = ecc.errorCorrectionRows[0].k;
		byte[][] dataBlocks = new byte[numberDataBlocks][shortDataBlockLength];
		if (ecc.errorCorrectionRows.length > 1) {	//if there are blocks of different lengths
			numberDataBlocks = numberDataBlocks + ecc.errorCorrectionRows[1].ecBlocks;
			longDataBlockLength = ecc.errorCorrectionRows[1].k; // might not exist
			for (int i = 0; i < numberShortDataBlocks; i++) {		//???doubelcheck
				dataBlocks = new byte[i][shortDataBlockLength]; 
			}
			for (int i = numberShortDataBlocks; i < numberDataBlocks; i++) {
				dataBlocks = new byte[i][longDataBlockLength]; 
			}
		}
		
		int totalNumberDataCodeWords = ecc.errorCorrectionRows[0].ecBlocks * ecc.errorCorrectionRows[0].k;
		if (ecc.errorCorrectionRows.length > 1) {	//if there are blocks of different lengths
			totalNumberDataCodeWords = totalNumberDataCodeWords + ecc.errorCorrectionRows[1].ecBlocks * ecc.errorCorrectionRows[1].k;
			for (int j = 0, k = 0; j < shortDataBlockLength; j++) {
				for(int i = 0; i < numberDataBlocks; i++) {
					dataBlocks[i][j] = unsortedData[k++];
					if (i < numberShortDataBlocks && j >= shortDataBlockLength) {
						i++;
						continue;
					}
				}
			}
		} else {
			for (int j = 0, k = 0; j < shortDataBlockLength; j++) {
				for(int i = 0; i < numberDataBlocks; i++) {
					dataBlocks[i][j] = unsortedData[k++];
				}
			}
		}
		return dataBlocks;
	}
	private static byte[][] sortECStream(byte[] unsortedData, int version, int ec) {
		//create ecBlocks
		Version.ErrorCorrectionCharacteristic ecc = Version.getErrorCorrectionCharacteristic(version, ErrorCorrectionLevel.parseIndex(ec));
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
	private static byte[] dealWithData(byte[][] dataBlocks, int version) {
		byte[] message = null;
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
		int em = 0;
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
		em = bf.getIntAndIncrementPosition(4);
		int dataSize = 0;	
		if (0 < version && version < 10) {
			dataSize = bf.getIntAndIncrementPosition(8);
		}
		if (9 < version && version < 41) {
			dataSize = bf.getIntAndIncrementPosition(16);
		}
		//create another data stream - only  message (ignore prefixes, ignore padding)
		BitBuffer messageBuffer = new BitBuffer(dataSize);
		for (int i = 0; i <= dataSize; i++) {
			messageBuffer.write(bf.getBitAndIncrementPosition());
		}
		message = messageBuffer.getData();
		return message;
	}
	
	
}
