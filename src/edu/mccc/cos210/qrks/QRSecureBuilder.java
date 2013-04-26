package edu.mccc.cos210.qrks;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
/**
 * QRSecureBuilder: A Builder<BufferedImage> that knows how to make Signed QRCodes.
 */
public class QRSecureBuilder extends QRBuilder {
	@Override
	public JBuilderPanel<BufferedImage> generateGUI() {
		return new QRSecureBuilderPanel(this);
	}
	@Override
	public String getName() {
		return "Signed QRCode";
	}
	@Override
	public void reset() {
	}
}