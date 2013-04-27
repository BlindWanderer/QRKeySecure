package edu.mccc.cos210.qrks;
import java.util.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import edu.mccc.cos210.qrks.qrcode.*;
/**
 * Responsible for accepting user inputs required to generate a QRCode.
 */
public class QRBuilderPanel extends JBuilderPanel<BufferedImage> {
	private static final long serialVersionUID = 1L;
	
	final private JTextArea input;
	final private JComboBox<ErrorCorrectionLevel> ec;
	final private JTextField pps;
	/**
	 * Gets the user-input text to be encoded in the QRCode.
	 * @return User-input text to be encoded in the QRCode.
	 */
	public String getText() {
		return input.getText();
	}
	/**
	 * Gets the error correction level selected by the user.
	 * @return Error correction level selected by the user.
	 */
	public ErrorCorrectionLevel getErrorCorrectionLevel() {
		return ec.getItemAt(ec.getSelectedIndex());
	}
	/**
	 * Gets the resolution (pixels per unit (module) selected by the user).
	 * @return Pixels per unit (module) selected by the user.
	 */
	public int getPixelsPerUnit() {
		return Integer.valueOf(pps.getText());
	}
	public Generator<BufferedImage> getGenerator() {
		return ((QRBuilder) getBuilder()).new QRGenerator(getText(), getErrorCorrectionLevel(), getPixelsPerUnit());
	}
	
	public QRBuilderPanel(final QRBuilder builder) {
		super(builder);

		input = new JTextArea(5, 50);
		JPanel tp = new JPanel(); //new BoxLayout(pane, BoxLayout.Y_AXIS)
		tp.setBorder(BorderFactory.createTitledBorder("QRCode Text:"));
		tp.add(input);

		ec = new JComboBox<ErrorCorrectionLevel>(ErrorCorrectionLevel.values());
		JPanel ecp = new JPanel(); //new BoxLayout(pane, BoxLayout.Y_AXIS)
		ecp.setToolTipText("Error Correction Level:");
		ecp.setBorder(BorderFactory.createTitledBorder("Error Correction Level:"));
		ecp.add(ec);

		pps = new JTextField(new JNumberFilter(), "1", 10);
		pps.addFocusListener(new FocusAdapter() {
				public void focusLost(final FocusEvent e) {
					JTextComponent c = (JTextComponent) e.getComponent();
					String text = c.getText();
					if (text == null || text == "") {
						c.setText("1");
					} else {
						long value;
						try {
							value = Long.valueOf(text);
						} catch (NumberFormatException nfe) {
							value = Long.MIN_VALUE;
						}
						if (value < 1) {
							c.setText("1");
						} else {
							if (512 < value) {
								c.setText("512");
							}
						}
					}
				}
			});
		JPanel ppsp = new JPanel(); //new BoxLayout(pane, BoxLayout.Y_AXIS)
		ppsp.setToolTipText("Pixels Per Unit:");
		ppsp.setBorder(BorderFactory.createTitledBorder("Pixels Per Unit:"));
		ppsp.add(pps);
		
		final JTextArea info = new JTextArea("Version: \nDimensions: \nNumber of Characters: 0");
		info.setEditable(false);
		//Font f = new Font(info.getFont());
		info.setOpaque(false);
		JPanel ip = new JPanel(); //new BoxLayout(pane, BoxLayout.Y_AXIS)
		ip.setToolTipText("Information:");
		ip.setBorder(BorderFactory.createTitledBorder("Information:"));
		ip.add(info);
		
		input.getDocument().addDocumentListener(new DocumentListener() {
			public void insertUpdate(final DocumentEvent e) {
				update(e);
			}
			public void removeUpdate(final DocumentEvent e) {
				update(e);
			}
			public void changedUpdate(final DocumentEvent e) {
				update(e); //Plain text components do not fire these events
			}
			public void update(final DocumentEvent e) {
				Document doc = e.getDocument();
				//TODO: calculate other values, this is trivial but implementation specific
				info.setText("Version: \nDimensions: \nNumber of Characters: " + doc.getLength());
			}
		});
		
		add(tp);
		add(ecp);
		add(ppsp);
		add(ip);
			}
}