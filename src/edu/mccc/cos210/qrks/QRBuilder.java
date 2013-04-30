package edu.mccc.cos210.qrks;
import edu.mccc.cos210.qrks.qrcode.*;

import java.nio.ByteBuffer;
import java.util.*;

import javax.swing.*;

import java.awt.*;
import java.awt.image.*;
/**
 * A Builder<BufferedImage> that knows how to make QRCodes.
 */
public abstract class QRBuilder implements Builder<BufferedImage> {
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
			final EncodingScheme es = getEncoding(text);
			 final int version = getVersion(text, ec, es);
			final byte [] memory = getMemorySpace(version);
			writeToMemory(memory, text, es, ec);
			writeErrorCorrection(version, memory);
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
	private static int getVersion(String text, ErrorCorrectionLevel ec, EncodingScheme es){
		int dataCharCount= text.length();
		int version = 0;
		for (int i = 1; i < 41; i++) {
			int maxChar = Version.nosc[ec.getPercentage()][i - 1].dataCapacityByte;
			if (dataCharCount <= maxChar) {	
				version = i;
				return version;
			}
		}
		return version;
	}
	private static byte[] getMemorySpace(int version) {
		int size = Version.getSize(version);  
		byte[] qr = new byte[size];
		return qr;
	}
	private static EncodingScheme getEncoding(String text){
		//TODO: Write me LATER
		return EncodingScheme.BYTE;
	}
	private static void writeToMemory(byte [] memory, String text, EncodingScheme es, ErrorCorrectionLevel ec) {
		int version = getVersion(text, ec, es);
		memory[0] = (byte) es.value; //??
		
		if (0 < version && version < 10) {
			memory[1] = (byte) Version.nosc[ec.getPercentage()][version - 1].dataCapacityByte; //TODO:set dataCapacity(X) elsewhere
			for (int i = 2; i < memory.length; i++) {
				//TODO
			}
		}
		if (9 < version && version < 41) {	
			ByteBuffer b = ByteBuffer.allocate(2);
			b.putInt(Version.nosc[ec.getPercentage()][version - 1].dataCapacityByte);
			byte[] result = b.array();
			memory[1] = result[0];
			memory[2] = result[1];
			for (int i = 3; i < memory.length; i++) {
				//TODO
			}
		}
		
		
		


		//TODO: Write other handles of various encoding schemes... LATER.
		throw new UnsupportedOperationException();
	}
	private enum EncodingScheme {
		ECI (7),
		NUMERIC (1),
		ALPHANUMERIC (2),
		BYTE (4),
		KANJI (8),
		STRUCTUREDAPPEND (3),
		FNC1 (5), //first position
		FNC2 (9); //second position //is FNC 8 bits???
		
		public final int value;
		EncodingScheme(int value){
			this.value = value;
		}
	}
	private static void writeErrorCorrection(int version, byte [] memory) {
		//Writes Error Correction bytes to memory
		//TODO: Write me LATER
	}
	private static boolean [][] getBasicQRCode(int version) {
		//Contains the various finding patters, correction patterns, timing patters, 
		//TODO: Write me
		return null;
	}
	private static void writeMetaData(boolean [][] field, int version, EncodingScheme es, byte [] memory) {
		//Write metadata to field
		//TODO: Write me
	}
	private static void writeDataToField(int version, boolean [][] field, byte [] memory) {
		//Write memory into field
		//TODO: Write me
	}
	private static BufferedImage makeImage(int version, boolean [][] field, int ppu, boolean quietZone) {
		return null;
	}
	private static void applyMasks(int version, boolean [][] field) {
		//TODO: Write me
	}
}
