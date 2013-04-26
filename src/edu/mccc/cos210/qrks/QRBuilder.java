package edu.mccc.cos210.qrks;
import edu.mccc.cos210.qrks.qrcode.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
/**
 * QRBuilder: A Builder<BufferedImage> that knows how to make QRCodes.
 */
public abstract class QRBuilder implements Builder<BufferedImage> {
	@Override
	public JBuilderPanel<BufferedImage> generateGUI() {
		return new QRBuilderPanel(this);
	}
	@Override
	public String getName() {
		return "QRCode";
	}
	@Override
	public void reset() {
	}
	public class QRGenerator implements Generator<BufferedImage> {
		private final String text;
		private final ErrorCorrectionLevel ec;
		private final int ppu;
		public QRGenerator(final String text, final ErrorCorrectionLevel ec, final int ppu) {
			this.text = text;
			this.ec = ec;
			this.ppu = ppu;
		}
		public Item<BufferedImage> generate() {
			//TODO: Write code that actually generates the darn thing
			return null;
		}
	}
}