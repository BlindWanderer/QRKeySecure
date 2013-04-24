package edu.mccc.cos210.qrks;
import java.util.*;
import javax.swing.*;
import java.awt.*;

class QRSecureBuilder extends QRBuilder {
	@Override
	public JPanel generateGUI() {
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