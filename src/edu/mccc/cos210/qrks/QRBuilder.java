package edu.mccc.cos210.qrks;
import edu.mccc.cos210.qrks.qrcode.*;
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
		 * @return A BufferedImage of the QRCode generated from user inputs.
		 */
		public Item<BufferedImage> runFactory() {
			//TODO: Write code that actually generates the darn thing
			return null;
		}
	}
}