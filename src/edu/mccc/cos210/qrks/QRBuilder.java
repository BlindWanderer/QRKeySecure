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
			final EncodingMode em = getEncoding(text);
			final byte [] data = encode(text, em);
			final int version = getVersion(data, ec, em);
			final BitBuffer memory = getMemorySpace(version);
			writeToMemory(memory, data, em, version, ec);
			final boolean [][] field = getBasicQRCode(version);
			final byte[][] dataBlocks = makeDataBlocks(memory, version);
			final byte[][] ecBlocks = makeECBlocks(dataBlocks, version, ec);

			Mask mask = getPreferredMask(version, field);
			writeMetaData(field, version, ec);
			writeDataToField(field, dataBlocks, ecBlocks);
			applyMask(version, field, mask);
			return new Item<BufferedImage>(){
				private BufferedImage img = null;
				public BufferedImage save() {
					if (img == null) {
						img = makeImage(field, ppu, true);
					}
					return img;
				}
				public JPanel generateGUI() {
					final BufferedImage si = makeImage(field, 3, false);
					JPanel gui = new JPanel();
					gui.add(new ImagePanel(si));
					//add some other elements with stats and text.
					return gui;
				}
			};
		}
	}
	private static int getVersion(byte [] data, ErrorCorrectionLevel ec, EncodingMode em) {
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
	private static EncodingMode getEncoding(String text){
		//TODO: Write me LATER
		return EncodingMode.BYTE;
	}
	private static byte [] encode(String text, EncodingMode es) {
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
		memory.write(em.value, 4);	//???? what is the second # doing?
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
				memory.write(0b0000);
				//PADDING
				//PADDING - if too few data, Pad with 11101100 and 00010001 alternatingly to get to the right # of codewords
				int memSize = memory.getSize();
				int dataSize = data.length;
				if (0 < version && version < 10) {
					while (memSize > dataSize + 16) {
						memory.write(0b11101100);
						if  (memSize > dataSize + 16) {
							memory.write(0b00010001);
						}
					}
				}
				if (9 < version && version < 41) {
					while (memSize > dataSize + 24) {
						memory.write(0b11101100);
						if  (memSize > dataSize + 24) {
							memory.write(0b00010001);
						}
					}
				}
				return;
			default:
				throw new UnsupportedOperationException();	
		}
	}
	private static byte[][] makeDataBlocks (BitBuffer memory, int version) {
		//Subdivide Data Codewords into Blocks, according to each block. 
		byte[] dataCodeWords = memory.getData();
		//determine from table how many data blocks and how many code words in each.
		//make sub arrays for each "block"
		byte[][] dataBlocks = null;
		return dataBlocks;
	}
	private static byte[][] makeECBlocks (byte[][] dataBlocks, int version, ErrorCorrectionLevel ec) {
		//Creates a ErrorCorrection Block for each Codeword Block
		byte[][] ecBlocks = null;
		return ecBlocks;
	}
	private static boolean [][] getBasicQRCode(int version) {
		//Contains the various finding patters, timing patterns, alignment patterns
		final int size = Version.getSize(version);
		boolean[][] qr = new boolean[size][size]; 
		Point[] findingArray = Version.getFindingPatternLocations(version);
		for (Point p : findingArray) {
			//create a finding pattern at the location of the point;
			for (int x = 0; x < 7; x ++) {
				qr[p.x + x][p.y] = true;
			}
			for (int x = 0; x < 7; x ++) {
				qr[p.x + x][p.y + 6] = true;
			}
			qr[p.x][p.y + 1] = true;
			qr[p.x + 6][p.y + 4] = true;
			qr[p.x][p.y + 1] = true;
			qr[p.x + 6][p.y + 4] = true;
			for (int y = 2; y < 5; y++) {
				qr[p.x][p.y] = true;
				qr[p.x + 2][p.y] = true;
				qr[p.x + 3][p.y] = true;
				qr[p.x + 4][p.y] = true;
				qr[p.x + 6][p.y] = true;
			}
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
		//TODO: draw metainfo
		return qr;
	}
	private static Mask getPreferredMask(int version, boolean [][] field) {
		//TODO: Write me
		//get masks, evaluate
		//N1=3, N2=3, N3=40, N4=10
		//is the amount by which the number of
		//adjacent modules of the same color exceeds 5 and k is the rating of the deviation of the proportion of dark
		//modules in the symbol from 50% in steps of 5%. Although the data masking operation is only performed on
		//the encoding region of the symbol excluding the format information, the area to be evaluated is the complete
		//symbol.
		Mask mask = null;
		return mask;
	}
	private static void writeMetaData(boolean [][] field, int version, ErrorCorrectionLevel ec) {
		//Write metadata to field
		/*2 bit ec
		 3 bit mask 
		10 BCH bits from Annex c
		Mask pattern for XOR operation: 101010000010010
		*/
		int format = 0b000000000000000; //some random value 15 bits
		int error = ec.index;
		format = format & (error << 13);
		//TODO: need to put in preferred mask type
		int xorMask = 0b101010000010010;
		int fi = format ^ xorMask;
		//format info:
		int size = Version.getSize(version);
		//TODO check theses row/column values. I think some may be one off.
		for (int x = 0; x <=8; x++) {	//lease significant 0-7
			field[size - x][8] = (fi >>> x) !=0;
		}
		field[8][size - 8] = true; 
		for (int y = 0; y <=8; y++) {	//most significant 8-14
			field[8][size - 7 + y] = (fi >>> (y + 8)) !=0;
		}
		//left side (angle)
		for (int y = 0; y <= 6; y++) {
			field [8][y] = fi >>> y != 0;
		}
		field [8][7] = (fi >>> 6) != 0;
		field [8][8] = (fi >>> 7) != 0;
		field [7][8] = (fi >>> 8) != 0;
		for (int x = 5; x >= 0; x--) {
			field[x][8] = (fi >>> (15 - x)) !=0;
		}
		if (version >= 7) {
			int dataBits = version; //??? how do i get a binary representation of this #?
			//write version info 
			//It consists of an 18-bit
			//sequence containing 6 data bits, with 12 error correction bits calculated using the (18, 6) Golay code. For
			//details of the error correction calculation for the version information, refer to Annex D. The six data bits contain
			//the Version of the symbol, most significant bit first.
			//??? doesNOT get masked (if version<7 - is the space blank of filled?
			//B: The space is available for data and error correction codewords. This means the dataMask changes between version 6 and 7.
		}
		//TODO: Write me
	}
	private static void writeDataToField(boolean [][] field, byte[][] dataBlocks, byte[][] ecBlocks) {
		//??? convert to bitBuffer???
		//Write memory into field
		//TODO: Write me
	}
	private static void applyMask(int version, boolean [][] field, Mask mask) {
		//xor datafield with preferredmask
	}
	private static BufferedImage makeImage (boolean [][] field, int ppu, boolean quietZone) {
		BufferedImage bi = new BufferedImage(field.length * ppu, field.length * ppu, BufferedImage.TYPE_INT_ARGB);
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
					for (int k = 0; k <= ppu; k++) {
						for (int l = 0; l <= ppu; l++) {
							bi.setRGB(k,0,0x000000);
							bi.setRGB(0,l,0x000000);
						}
					}
				}
			}
		}
		if (quietZone) { //4 units wide
			BufferedImage biqz = new BufferedImage(bi.getWidth() + 8 * ppu, bi.getHeight() + 8 * ppu, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = biqz.createGraphics();  
			g.drawImage(bi, null, ppu * 4, ppu * 4);
			g.dispose(); 
			return biqz;
		}
		return bi;
	}
}
