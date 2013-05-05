package edu.mccc.cos210.qrks;

import edu.mccc.cos210.qrks.qrcode.ErrorCorrectionLevel;
import edu.mccc.cos210.qrks.qrcode.Mask;
import edu.mccc.cos210.qrks.qrcode.Version;
import edu.mccc.cos210.qrks.util.BitBuffer;

public class Decoder {

	public static byte[] decode(boolean[][] cleanMatrix) {
		byte[] message = null;
		int version = versionInfo(cleanMatrix);
		int formatInfo = formatInfo(cleanMatrix, version); 
		int ec = getECFromFormatInfo(formatInfo);
		int mask = getMaskFromFormatInfo(formatInfo);
		//unmask
		boolean[][] maskMatrix = Mask.generateFinalMask(mask, version);
		cleanMatrix = cleanMatrix ^ maskMatrix;
		byte[] unsortedData = getDataStream(cleanMatrix, version);
		byte[][] dataBlocks= sortDataStream(unsortedData, version, ec);
		message = dealWithData(dataBlocks, version);
		return message;
	}
	private static int versionInfo(boolean[][] cleanMatrix) {
		int assumedVersion = (cleanMatrix.length - 21) / 4;
		int version = 0;
		if (assumedVersion >= 7) {
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
			
			for (int y = 8; y >= 0; y--) {
				for (int x = size - 8; x >= size - 10; x--) {
					boolean b = cleanMatrix[x][y];
					bf.write(b);		//TODO: need a write(bit) method for bit buffer.
				}
			}
			
			//horiz
			for (int y = size - 8; y >= size - 10; y--) {
				for (int x = 8; x >= 0; x--) {
					boolean b = cleanMatrix[x][y];
					bf2.write(b);
				}
			}
			//TODO: write a BitBuffer.toInt(bitBuffer)
			int ibf = bf.toInt();
			int ibf2 = bf.toInt();
			// see if all is well
			//check one both version cleanMatrixs and see if they match (trying to NAND)
			if (~(ibf & ibf2) == 0 && ibf == Version.getVersionInfoBitStream(assumedVersion - 7)) { //TODO: write a BitBuffer.toInt(bitBuffer)
				return assumedVersion;
			}
			//if not so good, then match to the nearest value from the table
			for (int i = 0; i <= 33; i++) {
				int versionInfo = Version.getVersionInfoBitStream(i);
				int comp = ~(ibf & versionInfo);
				//TODO:see how may 1's comp has (to see how much we deviate from what's given to us	
					
			}
		}
		return version;
	
	}
	private static int formatInfo(boolean[][] cleanMatrix, int version) {
		BitBuffer bf = new BitBuffer(15);
		final int size = Version.getSize(version);
		for (int y = 0; y <= 6; y++) {	//most significant 14-8
			boolean b = cleanMatrix[8][size - y];
			bf.write(b);
		}
		for (int x = 7; x <= 0; x--) {	//least significant 7-0
			boolean b = cleanMatrix[size - x][8]; 
			bf.write(b);
		}
		
		BitBuffer bf2 = new BitBuffer(15);
		//left side (angle)
		for (int x = 0; x < 6; x++) {	//14-9
			boolean b = cleanMatrix[x][8];
			bf2.write(b);
		}
		bf2.write(cleanMatrix [7][8]);//8
		bf2.write(cleanMatrix [8][8]);//7
		bf2.write(cleanMatrix [8][7]);//6
		for (int y = 5; y <= 0; y--) {
			boolean b = cleanMatrix [8][y];
			bf2.write(b);
		}
		
		int ibf = bf.toInt();
		int ibf2 = bf2.toInt();
		//must release mask 101010000010010
		ibf = ibf ^ (0b101010000010010);
		ibf2 = ibf2 ^ (0b101010000010010);
		// see if all is well
		if (~(ibf & ibf) == 0) {
			int ecInfo = 0b00 & ((bf.getBitAndIncrementPosition()? 1 : 0) << 1);
			ecInfo = ecInfo & (bf.getBitAndIncrementPosition() ? 1 : 0);
			int maskInfo = 0b000 & ((bf.getBitAndIncrementPosition() ? 1 : 0) << 2);
			maskInfo = maskInfo & ((bf.getBitAndIncrementPosition() ? 1 : 0) << 1);
			maskInfo = maskInfo & ((bf.getBitAndIncrementPosition() ? 1 : 0) << 2);
			//???how to return two values from one method?
		}
		//TODO: if all is not well, check differences from table values (3 at most)
		return ibf; //???return happy int
	}
	private static int getECFromFormatInfo(int formatInfo) {
		int ec = 0;
		return ec;
	}
	private static int getMaskFromFormatInfo(int formatInfo) {
		int mask = 0;
		return mask;
	}
	
	private static byte[] getDataStream(boolean[][] cleanMatrix, int version) {
		boolean[][] mask = Version.getDataMask(version);
		boolean direction = false; //0 = up; 1 = down;
		boolean lastlocation = false; //0 = going right; 1 = going left
		int size = cleanMatrix.length;
		BitBuffer bf = new BitBuffer(size * size); //too large, but who cares.
		int x = size - 1;
		int y = size - 1;
		for (int i = 0; i < bf.getSize(); i++) {
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
					if(y == size) {
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
		Version.ErrorCorrectionCharacteristic ecc = Version.getErrorCorrectionCharacteristic(version, ec);
		int numberDataBlocks = ecc.errorCorrectionRows[0].ecBlocks;//#ecBlocks = #dataBlocks
		if (ecc.errorCorrectionRows.length > 1) {	//if there are blocks of different lengths
			numberDataBlocks = numberDataBlocks + ecc.errorCorrectionRows[1].ecBlocks;
		}
		int shortDataBlockLength = ecc.errorCorrectionRows[0].k;
		int longDataBlockLength = ecc.errorCorrectionRows[1].k; // might not exist
		//create block[][]
		byte[][] dataBlocks = new byte[numberDataBlocks][longDataBlockLength];
		int index = 0;
		int i = 0;
		int numberShortDataBlocks = ecc.errorCorrectionRows[0].ecBlocks;
		int totalNumberDataCodeWords = ecc.errorCorrectionRows[0].ecBlocks * ecc.errorCorrectionRows[0].k;
		if (ecc.errorCorrectionRows.length > 1) {	//if there are blocks of different lengths
			totalNumberDataCodeWords = totalNumberDataCodeWords + ecc.errorCorrectionRows[1].ecBlocks * ecc.errorCorrectionRows[1].k;
		}
		while (index < totalNumberDataCodeWords){		//???double check this crap
			for (int j = 0; j < numberDataBlocks; j++) {
				if (i >= numberShortDataBlocks  && j == shortDataBlockLength) {i++;}
				dataBlocks[i][j] = unsortedData[index];
				if (j == numberDataBlocks - 1) {i++;}
				index++;
			}
		}
		return dataBlocks;
	}
	private static byte[][] sortECStream(byte[] unsortedData, int version, int ec) {
		//create ecBlocks
		Version.ErrorCorrectionCharacteristic ecc = Version.getErrorCorrectionCharacteristic(version, ec);
		int numberECBlocks = ecc.errorCorrectionRows[0].ecBlocks;//#ecBlocks = #dataBlocks
		int ecBlockLength = ecc.errorCorrectionRows[0].c - ecc.errorCorrectionRows[0].k; //always same, regardless of dataBlock length
		//create block[][]
		byte[][] ecBlocks = new byte[numberECBlocks][ecBlockLength];
		int index = 0;
		int i = 0;
		int totalNumberDataCodeWords = ecc.errorCorrectionRows[0].ecBlocks * ecc.errorCorrectionRows[0].k;
		if (ecc.errorCorrectionRows.length > 1) {	//if there are blocks of different lengths
			totalNumberDataCodeWords = totalNumberDataCodeWords + ecc.errorCorrectionRows[1].ecBlocks * ecc.errorCorrectionRows[1].k;
		}
		int totalNumberECCodeWords = numberECBlocks * ecBlockLength;
		while (index >= totalNumberDataCodeWords && index < totalNumberECCodeWords){		//???double check this crap
			for (int j = 0; j < numberECBlocks; j++) {
				ecBlocks[i][j] = unsortedData[index];
				if (j == numberECBlocks - 1) {i++;}
				index++;
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
				bf.write((byte)dataBlocks[i][j], 8);
			}
		}
		//determine length of data from info
		//Encoding Mode (4 bit), Data Size (version 0-9 8 bits, version 10-40 16 bits), Terminator (4 bits), and Padding (to fill DataCapacity) into a BitBuffer
		int em = 0;
		for (int i = 4; i>=0; i++){
			em = em & ((bf.getBitAndIncrementPosition()? 1 : 0) << i);
		}
		int dataSize = 0;	
		int dataStartPosition = 0; //where actual dataStarts after prefixes
		if (0 < version && version < 10) {
			for (int i = 8; i>=0; i++){
				dataSize = dataSize & ((bf.getBitAndIncrementPosition()? 1 : 0) << i);
			}
			dataStartPosition = 12; //???am i off by 1 
		}
		if (9 < version && version < 41) {
			for (int i = 16; i>=0; i++){
				dataSize = dataSize & ((bf.getBitAndIncrementPosition()? 1 : 0) << i);
			}
			dataStartPosition = 16; //???am i off by 1 
		}
		//create another data stream - only  message (ignore prefixes, ignore padding)
		BitBuffer messageBuffer = new BitBuffer(dataSize);
		for (int i = dataStartPosition; i <= dataStartPosition + dataSize; i++) {
			messageBuffer.write(bf.getBitAndIncrementPosition());
		}
		message = messageBuffer.getData();
		return message;
	}
	
}
