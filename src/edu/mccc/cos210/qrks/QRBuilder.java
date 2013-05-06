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
	public class QRFactory implements Factory<Item<BufferedImage>> {
		private final String text;
		private final ErrorCorrectionLevel ec;
		private final int ppu;
		/**
		 * Cashes the user-input values from the Viewer. 
		 * @param text User-input message to be encoded in the QR code.
		 * @param ec Limited-choice error correction level selected by the user.
		 * @param ppu Pixels per unit (module) selected by the user.
		 */
		public QRFactory(final String text, final ErrorCorrectionLevel ec, final int ppu) {
			this.text = text;
			this.ec = ec;
			this.ppu = ppu;
		}
		/**
		 * QRBuilder: A Builder<BufferedImage> that knows how to make QRCodes.
		 * @return The QRCode generated from user inputs.
		 */
		public Item<BufferedImage> runFactory() {
			try {
				final EncodingMode em = getEncoding(text);			
				final byte [] data = encode(text, em);
				final int version = getVersion(data, ec, em);
				final BitBuffer memory = getMemorySpace(version);
				writeToMemory(memory, data, em, version, ec);
				final boolean [][] field = getBasicQRCode(version);
				final byte[][] dataBlocks = makeDataBlocks(memory, version, ec);
				final byte[][] ecBlocks = makeECBlocks(dataBlocks, version, ec);
	
				int mask = getPreferredMask(version, field);
				writeMetaData(field, version, ec, mask);
				writeDataToField(field, dataBlocks, ecBlocks, version);
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
		Version.SymbolCharacterInfo [] scis = Version.nosc[ec.index];
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
					return text.getBytes("US-ASCII");//TODO: Make this better!
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
		Version.ErrorCorrectionCharacteristic ecc = Version.getErrorCorrectionCharacteristic(version, ec);
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
		//Creates a ErrorCorrection Block for each Codeword Block
		Version.ErrorCorrectionCharacteristic ecc = Version.getErrorCorrectionCharacteristic(version, ec);
		int numberECBlocks = ecc.errorCorrectionRows[0].ecBlocks;//#ecBlocks = #dataBlocks
		if (ecc.errorCorrectionRows.length > 1) {	//if there are blocks of different lengths
			numberECBlocks = numberECBlocks + ecc.errorCorrectionRows[1].ecBlocks;
		}
		byte[][] ecBlocks = new byte[numberECBlocks][ecc.ecCodewords / numberECBlocks];
		return ecBlocks;
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
		int error = ec.index;
		format = format | (error << 13); 
		int dataBits = 0b00000;
		dataBits = dataBits | (error << 3);
		//TODO: need to put in preferred mask type
		format = format | (mask << 10); 
		dataBits = dataBits | mask;
		//get BCH bits from table 9
		int[] array = FormatInfo.DataBitsToErrorCorrectionBits;
		int bch = array[dataBits];
		format = format | bch;
		int xorMask = 0b101010000010010;
		final int fi = format ^ xorMask;
		System.out.print(Integer.toBinaryString(fi));
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
	private static void writeDataToField(boolean [][] field, byte[][] dataBlocks, byte[][] ecBlocks, int version) {
		BitBuffer bf = new BitBuffer(Version.getSize(version) * Version.getSize(version));
		int first = dataBlocks.length; 
		int second = dataBlocks[first - 1].length;
		for (int j = 0; j < second; j++) {
			for (int i = 0; i < first; i++) {
				if(j < dataBlocks[i].length) {
					bf.write(dataBlocks[i][j], 8);
				}
			}
		}
		int firstEC = ecBlocks.length;
		int secondEC = ecBlocks[firstEC - 1].length;

		for (int j = 0; j < secondEC; j++) {
			for (int i = 0; i < firstEC; i++) {
				if(j < ecBlocks[i].length) {
					bf.write(ecBlocks[i][j], 8);  //???TODO: BUG sometimes has indexoutof bound issue.
				}
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
	}
	private static boolean shouldWrite (int x, int y, boolean[][] dataMask) {
		if (dataMask[x][y]) {//true is good, we write on true
			return true;
		}
		return false;
	}
	private static int getPreferredMask(int version, boolean [][] field) {
		//TODO: Write me
		int N1 = 3;
		int N2 = 3;
		int N3 = 40;
		int N4 = 10;
		int[] penalty = new int[8];
		int penaltyValue = 0;
		for (int i = 0; i < 8; i++) {
			boolean[][] trialMask = Mask.generateFinalMask(i, version);
			penalty[i] = penaltyValue;
		}
		//get least penalty
		int minPenalty = 1000000000;
		for (int i = 0; i < penalty.length; i++) {
			minPenalty = Math.min(minPenalty, penalty[i]);
		}
		//rule1 = module strings of same color (bad if more than 5 in a row / column)
			//horiz scan:
		
		
		//get masks, evaluate
		//N1=3, N2=3, N3=40, N4=10
		//is the amount by which the number of
		//adjacent modules of the same color exceeds 5 and k is the rating of the deviation of the proportion of dark
		//modules in the symbol from 50% in steps of 5%. Although the data masking operation is only performed on
		//the encoding region of the symbol excluding the format information, the area to be evaluated is the complete
		//symbol.
		return 0;
	}
	private static boolean[][] applyMask(int version, boolean [][] field, int maskNum) {
		//xor datafield with preferredmask
		boolean[][] maskMatrix = Mask.generateFinalMask(maskNum, version);
		for (int i = 0; i < field.length; i++) {
			for (int j = 0; j < field[i].length; j++) {
				field[i][j] = field[i][j] ^ maskMatrix[i][j];
			}
		}
		return field;
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
}
