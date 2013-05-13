package edu.mccc.cos210.qrks;
import edu.mccc.cos210.qrks.qrcode.*;
import edu.mccc.cos210.qrks.util.BitBuffer;

import java.util.*;

import javax.swing.*;

import java.awt.*;
import java.awt.image.*;
/**
 * A Builder<BufferedImage> that knows how to make QRCodes.
 */
public class QRBuilder implements Builder<BufferedImage> {
	/**
	 * Generates a JBuilderPanel that contains information necessary to create QRCode; will be placed in Viewer.
	 */
	@Override
	public BuilderPanel<BufferedImage> generateGUI() {
		return new QRBuilderPanel(this);
	}
	/**
	 * Returns this Builder's name.
	 * @return This Builder's name
	 */

	@Override
	public String getName() {
		return "QRCode";
	}
	@Override
	public void reset() {
	}
	/**
	 * Generates a QRCode based on user-input.
	 */
	public class QREncodingFactory implements Factory<Item<BufferedImage>> {
		private final String text;
		private final ErrorCorrectionLevel ec;
		private final int ppu;
		public QREncodingFactory(final String text, final ErrorCorrectionLevel ec, final int ppu) {
			this.text = text;
			this.ec = ec;
			this.ppu = ppu;
		}
		public Item<BufferedImage> runFactory() {
			final EncodingMode em = getEncoding(text);			
			final byte [] data = encode(text, em);
			return new QRFactory(data, em, ec, ppu).runFactory();
		}
	}
	public class QRFactory implements Factory<Item<BufferedImage>> {
		private final byte[] data;
		private final ErrorCorrectionLevel ec;
		private final int ppu;
		private final EncodingMode em;
		/**
		 * Cashes the user-input values from the Viewer. 
		 * @param text User-input message to be encoded in the QR code.
		 * @param ec Limited-choice error correction level selected by the user.
		 * @param ppu Pixels per unit (module) selected by the user.
		 */
		public QRFactory(final byte [] encodedData, final EncodingMode em, final ErrorCorrectionLevel ec, final int ppu) {
			this.ec = ec;
			this.ppu = ppu;
			this.data = encodedData;
			this.em = em;
		}
		/**
		 * QRBuilder: A Builder<BufferedImage> that knows how to make QRCodes.
		 * @return The QRCode generated from user inputs.
		 */
		public Item<BufferedImage> runFactory() {
			try {
				final int version = getVersion(data, ec, em);
				final BitBuffer memory = getMemorySpace(version);
				writeToMemory(memory, data, em, version, ec);
				final boolean [][] field = getBasicQRCode(version);
				final byte[][] dataBlocks = makeDataBlocks(memory, version, ec);
				final byte[][] ecBlocks = makeECBlocks(dataBlocks, version, ec);
				writeMetaData(field, version, ec, 0); //we don't know the mask number yet, so we put 0 (we need to have version info written before mask penalty evaluation)
				writeDataToField(field, dataBlocks, ecBlocks, version, ec);
				int mask = getPreferredMask(version, field);
				writeMetaData(field, version, ec, mask); //overwrite mask info with preferred mask
				final boolean[][] finalField = applyMask(version, field, mask);
				return new Item<BufferedImage>(){
					private BufferedImage img = null;
					public BufferedImage save() {
						if (img == null) {
							try{
							img = makeImage(finalField, ppu, true); //FinalField
							} catch (Exception e){
								e.printStackTrace();
							}
						}
						return img;
					}
					public JPanel generateGUI() {
						try{
						final BufferedImage si = makeImage(finalField, ppu, false); //FinalField
						JPanel gui = new JPanel();
						gui.add(new ImagePanel(si));
						//add some other elements with stats and text.
						return gui;
						} catch (Exception e){
							e.printStackTrace();
						}
						return null;
					}
				};
			} catch (Exception e) {
				e.printStackTrace(); return null;
			}
		}
	}
	public static int getVersion(byte [] data, ErrorCorrectionLevel ec, EncodingMode em) {
		int dataCharCount= data.length;
		SymbolCharacterInfo [] scis = ec.getSymbolCharacterInfos();
		for (int i = 1; i < 41; i++) {
			int maxChar = scis[i - 1].getDataCapacity(em);
			if (dataCharCount <= maxChar) {
				return i;
			}
		}
		return -1;
	}
	private static BitBuffer getMemorySpace(int version) {
		int size = Version.getDataCapacity(version);
		return new BitBuffer(size);
	}
	public static EncodingMode getEncoding(String text){
		//TODO: Write me LATER
		return EncodingMode.BYTE;
	}
	public static byte [] encode(String text, EncodingMode es) {
		switch(es) {
			case BYTE:
				try {
					return text.getBytes("ISO-8859-1");//TODO: Make this better!
				} catch (Exception e) {
					return null;
				}
			default:
				throw new UnsupportedOperationException();
			//TODO: Write other handles of various encoding schemes... LATER.
		}
	}
	private static void writeToMemory(BitBuffer memory, byte [] data, EncodingMode em, int version, ErrorCorrectionLevel ec) {
		//WARNING! We need a BitBuffer of some sort, QRCodes are not Byte Aligned!
		//Places Encoding Mode (4 bit), Data Size (version 0-9 8 bits, version 10-40 16 bits), Terminator (4 bits), and Padding (to fill DataCapacity) into a BitBuffer
		memory.write(em.value, 4);	
		switch (em) {
			case BYTE:
				if (0 < version && version < 10) {
					memory.write((byte)data.length);
				}
				if (9 < version && version < 41) {
					memory.write((char)data.length);
				}
				memory.write(data);
				//terminator
				memory.write(0b0000, 4);
				//PADDING
				//PADDING - if too few data, Pad with 11101100 and 00010001 alternatingly to get to the right # of codewords
				int memSize = memory.getSize();
				int dataSize = data.length;
				if (0 < version && version < 10) {
					while (memSize > dataSize * 8 + 16) {
						memory.write((byte)0b11101100);
						dataSize++;
						if  (memSize > dataSize * 8 + 16) {
							memory.write((byte)0b00010001);
							dataSize++;
						}
					}
				}
				if (9 < version && version < 41) {
					while (memSize > dataSize * 8 + 24) {
						memory.write((byte)0b11101100);
						dataSize++;
						if  (memSize > dataSize * 8 + 24) {
							memory.write((byte)0b00010001);
							dataSize++;
						}
					}
				}
				return;
			default:
				throw new UnsupportedOperationException();	
		}
	}
	private static byte[][] makeDataBlocks (BitBuffer memory, int version, ErrorCorrectionLevel ec) {
		//Subdivide Data Codewords into Blocks, according to each block. 
		byte[] dataCodeWords = memory.getData();
		//determine from table how many data blocks and how many code words in each.
		ErrorCorrectionLevel.Characteristic ecc = ec.getCharacteristic(version);
		int numberDataBlocks = ecc.errorCorrectionRows[0].ecBlocks;//#ecBlocks = #dataBlocks
		if (ecc.errorCorrectionRows.length > 1) {	//if there are blocks of different lengths
			numberDataBlocks = numberDataBlocks + ecc.errorCorrectionRows[1].ecBlocks;
		}
		byte [][] dataBlocks = new byte[numberDataBlocks][]; //row 1 has longer blocks
		//make sub arrays for each "block"
		int shortBlock = ecc.errorCorrectionRows[0].k;
		for (int i = 0; i < ecc.errorCorrectionRows[0].ecBlocks; i++) {
			dataBlocks[i] = new byte[shortBlock];
			for (int j = 0; j < shortBlock; j++) {
				dataBlocks[i][j] = dataCodeWords[(i * shortBlock) + j];
			}
		}
		if (ecc.errorCorrectionRows.length > 1) {	//if there are blocks of different lengths
			int longBlock = ecc.errorCorrectionRows[1].k;
			int offset = ecc.errorCorrectionRows[0].ecBlocks;
			int ecBlocks = ecc.errorCorrectionRows[1].ecBlocks;
			int w = offset * ecc.errorCorrectionRows[0].k;
			for (int i = 0; i < ecBlocks; i++) {
				dataBlocks[i + offset] = new byte[longBlock];
				for (int j = 0; j < longBlock; j++) {
					dataBlocks[i + offset][j] = dataCodeWords[w + (i * longBlock) + j]; 
				}
			}
		}
		return dataBlocks;
	}
	private static byte[][] makeECBlocks (byte[][] dataBlocks, int version, ErrorCorrectionLevel ec) {
		return null;
		/*
		//Creates a ErrorCorrection Block for each Codeword Block
		ErrorCorrectionLevel.Characteristic ecc = ec.getCharacteristic(version);
		int numberECBlocks = ecc.errorCorrectionRows[0].ecBlocks;//#ecBlocks = #dataBlocks
		if (ecc.errorCorrectionRows.length > 1) {	//if there are blocks of different lengths
			numberECBlocks = numberECBlocks + ecc.errorCorrectionRows[1].ecBlocks;
		}
		byte[][] ecBlocks = new byte[numberECBlocks][];
		for (int i = 0; i < dataBlocks.length; i++) {//for each dataBlock
			ErrorCorrectionLevel.Characteristic.Row row = ecc.getRowFromBlock(i);
			
			//need to reverse the order of byte in dataBlock - so that index matches x's exponent
			int[] dataPoly = new int[dataBlocks[i].length];
			for (int j = 0, k = dataBlocks.length - 1; j < dataBlocks[i].length && k >= 0; j++, k--) {
				dataPoly[k] = dataBlocks[i][j] & 0xFF;
			}
			int numECCodewords = row.c - row.k; 
			ecBlocks[i] = new byte[numECCodewords];
			//create genPoly from table
			int[] genPoly = ErrorCorrection.generatorPolynomials[numECCodewords];
			//multiply dataPoly by x^n, where n = numberErrorCorrectionCodeWords (really, shift the array
			//expand array by n and shift everything over by n
			int[] dataPolyReady = new int[dataPoly.length + numECCodewords];
			for (int l = dataPoly.length - 1, m = dataPolyReady.length - 1; l >= 0; l--, m--) {
				dataPolyReady[m] = dataPoly[l];
			}
			
			//multiply genPoly by x^q, where q = numberDataCodeWords
			int numDataCodewords = row.k;

			int[] genPolyReady = new int[genPoly.length + numDataCodewords];
			for (int l = genPoly.length - 1, m = genPolyReady.length - 1; l >= 0; l--, m--) {
				genPolyReady[m] = genPoly[l];
			}
			
			for (int v = 0; v < numDataCodewords; v++) { //repeat these steps n times (n = # dataCodeWords)
				//multiply genPolyReady by lead term (highest exponent / highest index) of dataPolyReady
				//convert dataPolyReady to Exponent
				dataPolyReady = intToExp(dataPolyReady);
				//add each coefficient value of dataPolyReady and genPolyReady - store in tempPoly
				int[] tempPoly = new int[dataPolyReady.length];
				for (int k = 0; k < dataPolyReady.length; k++) {
					tempPoly[k] = (dataPolyReady[k] + genPolyReady[k]);
					//if coefficient (exponent) > 255; modulo 255 and make that the value
					if (tempPoly[k] > 255) {
						tempPoly[k] = tempPoly[k] % 255;
					}
				}
				//convert tempPoly to Integer
				tempPoly = expToInt(tempPoly);
				//convert dataPolyReady to Integer
				dataPolyReady = expToInt(dataPolyReady);
				//XOR each coefficient in tempPoly with dataPolyReady (lead term / highest index is now zero) (store result in dataPolyReady)
				for (int y = 0; y < dataPolyReady.length; y++) {
					dataPolyReady[y] = dataPolyReady[y] ^ tempPoly[y];
				}
				//move genPolyReady one left (decrease highest index by 1)
				for (int z = 0; z < genPolyReady.length - 2; z++){
					genPolyReady[z] = genPolyReady[z + 1];
				}
				genPolyReady[genPolyReady.length - 1] = 0;
			}
			//don't forget to reverse dataPolyReady to get ecWords
			byte[] ecWords = new byte[numECCodewords];
			for (int h = 0, g = ecWords.length - 1; h < ecWords.length && g >= 0; h++, g--){
				ecWords[h] = (byte)dataPolyReady[g]; //in theory, dataPolyReady will have values in only 0-#ECWords indeces. 
			}
			//add the newly generated ecBlock[] to ecBlocks[][]
			for (int d = 0; d < ecBlocks[i].length; d++){
				ecBlocks[i][d] = ecWords[d];
			}
		}
		return ecBlocks;
		/* */
	}
	private static int[] expToInt(int[] ia) {
		for (int i = 0; i < ia.length; i++) {
			ia[i] = ErrorCorrection.expToInt[ia[i]];
		}
		return ia;
	}
	private static int[] intToExp(int[] ia) {
		for (int i = 0; i < ia.length; i++) {
			ia[i] = ErrorCorrection.intToExp[ia[i]];
		}
		return ia;
	}
	private static boolean [][] getBasicQRCode(int version) {
		//Contains the various finding patters, timing patterns, alignment patterns
		final int size = Version.getSize(version);
		boolean[][] qr = new boolean[size][size]; 
		Point[] findingArray = Version.getFindingPatternLocations(version);
		for (Point p : findingArray) {
			//create a finding pattern at the location of the point;
		
			for (int x = 0; x<=6; x++) {
				qr[p.x + x][p.y] = true;
				qr[p.x + x][p.y + 6] = true;
			}
			for (int y = 2; y <=4; y++) {
				qr[p.x][p.y + y] = true;
				qr[p.x + 2][p.y + y] = true;
				qr[p.x + 3][p.y + y] = true;
				qr[p.x + 4][p.y + y] = true;
				qr[p.x + 6][p.y + y] = true;
			}
			qr[p.x][p.y + 1] = true;
			qr[p.x][p.y + 5] = true;
			qr[p.x + 6][p.y + 1] = true;
			qr[p.x + 6][p.y + 5] = true;
		}
		//draw alignment patterns
		Point[] alignArray = Version.getAlignmentPatternLocations(version);
		for (Point p : alignArray) {
			qr[p.x][p.y] = true;
			for (int y = -2; y < 3; y++) {
				qr[p.x - 2][p.y + y] = true;
				qr[p.x + 2][p.y + y] = true;
			}
			for (int x = -2; x < 3; x++) {
				qr[p.x + x][p.y - 2] = true;
				qr[p.x + x][p.y + 2] = true;
			}
		}
		//draw timing patterns
		//horiz
		for (int x = 8; x < size - 8; x = x + 2) {
			qr[x][6] = true;
		}
		//vert
		for (int y = 8; y < size - 8; y = y + 2) {
			qr[6][y] = true;
		}
		return qr;
	}
	
	private static void writeMetaData(boolean [][] field, int version, ErrorCorrectionLevel ec, int mask) {
		//Write metadata to field
		/* FormatInfo
		 2 bit ec
		 3 bit mask 
		10 BCH bits from Annex c
		Mask pattern for XOR operation: 101010000010010
		*/
		int format = 0b000000000000000; // 15 bits
		int error = ec.getValue();
		format = format | (error << 13); 
		int dataBits = 0b00000;
		dataBits = dataBits | (error << 3);
		format = format | (mask << 10); 
		dataBits = dataBits | mask;
		//get BCH bits from table 9
		int[] array = FormatInfo.DataBitsToErrorCorrectionBits;
		int bch = array[dataBits];
		format = format | bch;
		int xorMask = 0b101010000010010;
		final int fi = format ^ xorMask;
//		System.out.print(Integer.toBinaryString(fi));
		//format info:
		final int size = Version.getSize(version);
		for (int x = 0; x < 8; x++) {	//least significant 0-7
			field[size - x - 1][8] = (fi & (1 << x)) !=0; 
		}
		field[8][size - 8] = true;
		for (int y = 6; y >= 0; y--) {	//most significant 8-14
			field[8][size - y - 1] = (fi & ((1 << 14) >> y)) !=0;
		}
		//left side (angle)
		for (int y = 0; y < 6; y++) {
			field [8][y] = ((fi >>> y) & 1) != 0;
		}
		field [8][7] = (fi & (1 << 6)) != 0;
		field [8][8] = (fi & (1 << 7)) != 0;
		field [7][8] = (fi & (1 << 8)) != 0;
		for (int x = 0; x < 6; x++) {
			field[x][8] = (fi & ((1 << 14) >> x)) !=0;
		}
		if (version >= 7) {
			//write version info 
			//18-bit
			//6 data bits
			//12 error correction bits calculated using the (18, 6) Golay code. Table D.1 for all 18 bits.
			int versionInfo = Version.getVersionInfoBitStream(version); 
			BitBuffer bf = new BitBuffer(18);
			bf.write(versionInfo, 18);
			//horiz
			bf.seek(0);
			for (int x = 5; x >= 0; x--) {
				for (int y = 9; y <= 11; y ++) {
					field[x][size - y] = bf.getBitAndIncrementPosition();
				}	
			}
			//vert
			bf.seek(0);
			for (int y = 5; y >= 0; y--) {
				for (int x = 9; x <= 11; x++) {
					field[size - x][y] = bf.getBitAndIncrementPosition();
				}
			}
			
		}
	}
	private static void writeDataToField(boolean [][] field, byte[][] dataBlocks, byte[][] ecBlocks, int version, ErrorCorrectionLevel ec) {
		BitBuffer bf = new BitBuffer(Version.getDataCapacity(version));
		if (dataBlocks != null) {
			int first = dataBlocks.length; 
			int second = dataBlocks[first - 1].length;
			for (int j = 0; j < second; j++) {
				for (int i = 0; i < first; i++) {
					if(j < dataBlocks[i].length) {
						bf.write(dataBlocks[i][j], 8);
					}
				}
			}
		}

		if (ecBlocks != null) {//This should never be null
			bf.seek(ec.getSymbolCharacterInfo(version).dataCodeWordBits);
			int firstEC = ecBlocks.length;
			int secondEC = ecBlocks[firstEC - 1].length;
			for (int j = 0; j < secondEC; j++) {
				for (int i = 0; i < firstEC; i++) {
					if(j < ecBlocks[i].length) {
						bf.write(ecBlocks[i][j], 8);  //???TODO: BUG sometimes has indexoutof bound issue.
					}
				}
			}
		} else if(dataBlocks != null) { //Error correction information does not exist. Fill it with constant garbage.
			for (int j = 0; bf.getPosition() < bf.getSize(); j++) {
				bf.write(dataBlocks[j % dataBlocks.length]);
			}
		}
		
		//Write memory into field
		boolean direction = false; //0 = up; 1 = down;
		boolean lastlocation = false; //0 = going right; 1 = going left
		int size = field.length;
		boolean[][] dataMask = Version.getDataMask(version);
		int x = size - 1;
		int y = size - 1;
		int max = Math.min(bf.getSize(), size * size);
		bf.seek(0);
		for (int i = 0; x >= 0; i++) {
			if (shouldWrite(x , y, dataMask)) {
				field[x][y] = bf.getBitAndIncrementPosition();
			}
			if (!direction) {//up
				if (!lastlocation) {//going right
					x--;
				} else {//going left
					if(y == 0) {
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
					if(y == size - 1) {
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
	}
	private static boolean shouldWrite (int x, int y, boolean[][] dataMask) {
		if (dataMask[x][y]) {//true is good, we write on true
			return true;
		}
		return false;
	}
	private static int getPreferredMask(int version, boolean [][] field) {
		int[] penalty = new int[8];
		
		for (int i = 0; i < 8; i++) {
			int penaltyValue = 0;
			boolean[][] maskedField = applyMask(version, field, i);
			penaltyValue = penalty1(maskedField) + penalty2(maskedField) + penalty3(maskedField) + penalty4(maskedField);
			penalty[i] = penaltyValue;
		}
		//get least penalty
		int minPenalty = 1000000000;
		int maskIndex = 99;
		for (int i = 0; i < penalty.length; i++) {
			minPenalty = Math.min(minPenalty, penalty[i]);
			if (minPenalty == penalty[i]) {
				maskIndex = i;
			}
		}
		return maskIndex;
	}
	private static boolean[][] applyMask(int version, boolean [][] field, int maskNum) {
		//xor datafield with preferredmask
		return Mask.xorOverwrite(Mask.generateFinalMask(maskNum, version), field);
	}
	private static BufferedImage makeImage (boolean [][] field, int ppu, boolean quietZone) {
		BufferedImage bi = new BufferedImage(field.length, field.length, BufferedImage.TYPE_INT_ARGB);
		{
			//color image white
			Graphics2D g = bi.createGraphics();  
			g.setColor(Color.WHITE);  
			g.fillRect(0, 0, bi.getWidth(), bi.getHeight());   
			g.dispose(); 
		}
		for (int i = 0; i < field.length; i++) {
			for (int j = 0; j < field[i].length; j++) {
				if (field[i][j]) {
					bi.setRGB(i,j,0xFF000000);
				}
			}
		}
		{
			BufferedImage bir = new BufferedImage(field.length * ppu, field.length * ppu, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = bir.createGraphics();
			g.drawImage(bi, 0, 0, bir.getWidth(), bir.getHeight(), 0, 0, bi.getWidth(), bi.getHeight(), null);
			g.dispose(); 
			bi = bir;
		}
		//*
		if (quietZone) { //4 units wide
			BufferedImage biqz = new BufferedImage(bi.getWidth() + 8 * ppu, bi.getHeight() + 8 * ppu, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = biqz.createGraphics();  
			g.setColor(Color.WHITE);  
			g.fillRect(0, 0, biqz.getWidth(), biqz.getHeight());   
			g.drawImage(bi, null, ppu * 4, ppu * 4);
			g.dispose(); 
			return biqz;
		}//*/
		return bi;
	}
	
	
	private static int penalty1(boolean[][] field) {
		//rule1 = module strings of same color (bad if more than 5 in a row / column)
		//horiz scan:
		int penalty1h = 0;
		for (int i = 0; i < field.length - 1; i++) {
			int count = 0;
			for (int j = 0; j < field[i].length; j++) {
				if (field[i][j] == field[i + 1][j]) {
					count++;
				} else {
					if (count >= 5) {
						int number = count - 5;
						penalty1h = penalty1h + 3 + number;
					}
					count = 0;
				}
			}
		}
		//vertical scan:
		int penalty1v = 0;
		for (int j = 0; j < field.length - 1; j++) {
			int count = 0;
			for (int i = 0; i < field[j].length; i++) {
				if (field[i][j] == field[i][j + 1]) {
					count++;
				} else {
					if (count >= 5) {
						int number = count - 5;
						penalty1v = penalty1v + 3 + number;
					}
					count = 0;
				}
			}
		}
		return penalty1v+penalty1h;
	}
	
	private static int penalty2(boolean[][] field){
		//rule2
		//count each 2x2 square
		//note: don't need two scans
		int penalty2 = 0;
		for (int i = 0; i < field.length - 1; i++){
			for (int j = 0; j < field[i].length - 1; j++) {
				if (field[i][j] == field[i][j + 1] == field[i + 1][j] == field [i + 1][j + 1]) {
					penalty2 = penalty2 + 3;
				}
			}
		}
		return penalty2;
	}
	
	private static int penalty3(boolean[][] field){
		int penalty3 = 0;
		//horiz scan 1 (wwwwbwbbbwb)
		for (int i = 0; i < field.length - 10; i++){
			for (int j = 0; j < field[i].length; j++) {
				if (field[i][j] == field [i + 1][j] == field [i + 2][j] == field [i + 3][j] == false &&
						field[i + 4][j] == true && field[i + 5][j] == false &&
						field[i + 6][j] == field[i + 7][j] == field[i + 8][j] == true &&
						field[i + 9][j] == false && field[i + 10][j] == true){
					penalty3 = penalty3 + 40;
				}
				
			}
		}
		//horiz scan 2 (bwbbbwbwwww)
		for (int i = 0; i < field.length - 10; i++){
			for (int j = 0; j < field[i].length; j++) {
				if (field[i][j] == true && field [i + 1][j] == false &&
						field [i + 2][j] == field [i + 3][j] == field[i + 4][j] == true && 
						field[i + 5][j] == false && field[i + 6][j] == true &&
						field[i + 7][j] == field[i + 8][j] == field[i + 9][j] ==  field[i + 10][j] == false){
					penalty3 = penalty3 + 40;
				}	
			}	
		}
		//vertical scan 1 (wwwwbwbbbwb)
		for (int j = 0; j < field.length - 10; j++) {
			for (int i = 0; i < field[j].length; i++) {
				if (field[i][j] == field [i][j + 1] == field [i][j + 1] == field [i][j + 3] == false &&
						field[i][j + 4] == true && field[i][j + 5] == false &&
						field[i][j + 6] == field[i][j + 7] == field[i][j + 8] == true &&
						field[i][j + 9] == false && field[i][j + 10] == true){
					penalty3 = penalty3 + 40;
				}
			}
		}
		//vertical scan 2 (bwbbbwbwwww)
		for (int j = 0; j < field.length - 10; j++) {
			for (int i = 0; i < field[j].length; i++) {
				if (field[i][j] == true && field [i][j + 1] == false &&
						field [i][j + 2] == field [i][j + 3] == field[i][j + 4] == true && 
						field[i][j + 5] == false && field[i][j + 6] == true &&
						field[i][j + 7] == field[i][j + 8] == field[i][j + 9] ==  field[i][j + 10] == false){
					penalty3 = penalty3 + 40;
				}	
			}
		}
	
		return penalty3;
	}
	private static int penalty4(boolean[][] field){
		int penalty4 = 0;
		int totalNumber = field.length * field.length; //??? how do i get the vertical size of the array?
		int blackNumber = 0;
		int whiteNumber = 0;
		for (int j = 0; j < field.length; j++) {
			for (int i = 0; i < field[j].length; i++) {
				if (field[i][j] == true) {
					blackNumber++;
				} else {
					whiteNumber++;
				}
			}
		}
		double percent = (double)blackNumber / totalNumber;
		int prevMult5 = 0;
		int nextMult5 = 5;
		while (percent < prevMult5) { //???double check this math
			prevMult5 =+ 5;
			nextMult5 =+ 5;
		}
		int num1 = Math.abs(prevMult5 - 50) / 5;
		int num2 = Math.abs(nextMult5 - 50) / 5;
		penalty4 = Math.min(num1,  num2) * 10;
		return penalty4;
	}
}
