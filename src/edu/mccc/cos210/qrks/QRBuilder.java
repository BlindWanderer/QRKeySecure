package edu.mccc.cos210.qrks;
import edu.mccc.cos210.qrks.qrcode.*;
import edu.mccc.cos210.qrks.util.*;
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
			final EncodingMode es = getEncoding(text);
			final byte [] data = encode(text, es);
			final int version = getVersion(data, ec, es);
			final BitBuffer memory = getMemorySpace(version);
			writeToMemory(memory, data, es, version, ec);
			writeErrorCorrection(version, memory, ec);
			final boolean [][] field = getBasicQRCode(version);
			writeMetaData(field, version, es, memory);
			writeDataToField(version, field, memory);
			applyMasks(version, field);
			return new Item<BufferedImage>(){
				private BufferedImage img = null;
				public BufferedImage save() {
					if (img == null) {
						img = makeImage(version, field, ppu, true);
					}
					return img;
				}
				public JPanel generateGUI() {
					final BufferedImage si = makeImage(version, field, 3, false);
					JPanel gui = new JPanel();
					gui.add(new ImagePanel(si));
					//add some other elements with stats and text.
					return gui;
				}
			};
		}
	}
	private static int getVersion(byte [] data, ErrorCorrectionLevel ec, EncodingMode em){
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
	private static EncodingMode getEncoding(String text) {
		//TODO: Write me LATER
		return EncodingMode.BYTE;
	}
	private static void writeToMemory(BitBuffer memory, byte [] data, EncodingMode em, int version, ErrorCorrectionLevel ec) {
		//WARNING! We need a BitBuffer of some sort, QRCodes are not Byte Aligned!
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
				return;
			default:
				throw new UnsupportedOperationException();			
		}
	}
	private static void writeErrorCorrection(int version, BitBuffer memory, ErrorCorrectionLevel ec) {
	/*
	memory.seek(ecStartPosition);
	byte [] data = memory.getData();
	//calculate correction data
	byte [] ecd = new byte[0];
	memory.write(ecd);
	*/
	//Writes Error Correction bytes to memory
	//TODO: Write me LATER
	}
	private static boolean [][] getBasicQRCode(int version) {
		//Contains the various finding patters, timing patters, 
		//TODO: Write me
		int size = Version.getSize(version);
		boolean[][] qr = new boolean[size][size]; 
		Point[] findingArray = Version.getFindingPatternLocations(version);
		for (Point p : findingArray) {
			//create a finding pattern at the location of the point;
			for (int x = 0; x < 7; x ++) {qr[p.x + x][p.y] = true;}
			for (int x = 0; x < 7; x ++) {qr[p.x + x][p.y + 6] = true;}
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
		
		Point[] allignArray = Version.getAlignmentPatternLocations(version);
		for (Point p : allignArray) {
			
		}
		return null;
	}
	private static void writeMetaData(boolean [][] field, int version, EncodingMode es, BitBuffer memory) {
		//Write metadata to field
		//TODO: Write me
	}
	private static void writeDataToField(int version, boolean [][] field, BitBuffer memory) {
		//Write memory into field
		//TODO: Write me
	}
	private static BufferedImage makeImage(int version, boolean [][] field, int ppu, boolean quietZone) {
		return null;
	}
	private static void applyMasks(int version, boolean [][] field) {
		//TODO: Write me
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
}
