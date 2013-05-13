package edu.mccc.cos210.qrks;
import java.util.*;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.*;

import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;

import edu.mccc.cos210.qrks.qrcode.*;
import edu.mccc.cos210.qrks.util.JNumberFilter;
/**
 * Responsible for accepting user inputs required to generate a QRCode.
 */
public class QRBuilderPanel extends BuilderPanel<BufferedImage> {
	private static final long serialVersionUID = 1L;
	
	final private JTextArea input;
	final private JComboBox<ErrorCorrectionLevel> ec;
	final private JTextField pps;
	final private JTextArea info;
	/**
	 * Gets the user-input text to be encoded in the QRCode.
	 * @return User-input text to be encoded in the QRCode.
	 */
	protected String getText() {
		return input.getText();
	}
	/**
	 * Gets the error correction level selected by the user.
	 * @return Error correction level selected by the user.
	 */
	protected ErrorCorrectionLevel getErrorCorrectionLevel() {
		return ec.getItemAt(ec.getSelectedIndex());
	}
	protected byte[] encode(EncodingMode em) {
		return QRBuilder.encode(getText(), em);
	}
	/**
	 * Gets the resolution (pixels per unit (module) selected by the user).
	 * @return Pixels per unit (module) selected by the user.
	 */
	protected int getPixelsPerUnit() {
		return Integer.valueOf(pps.getText());
	}
	/**
	 * Allows capture parameters from user input without actually building a new QRCode.
	 */
	public Factory<Item<BufferedImage>> getFactory() {
		return ((QRBuilder) getBuilder()).new QREncodingFactory(getText(), getErrorCorrectionLevel(), getPixelsPerUnit());
	}
	protected void updateInfo() {
		EncodingMode em = QRBuilder.getEncoding(getText());
		ErrorCorrectionLevel ec = getErrorCorrectionLevel();
		byte [] ba = encode(em);
		int version = QRBuilder.getVersion(ba, ec, em);
		int max = ec.getSymbolCharacterInfo(40).dataCodeWordBits / 8;
		if (version < 0 || version > 40) {
			info.setText("Error:\nMaximum capacity exceeded.\nEncoded size: " + (ba.length + 3) + "\nMaximum: " + max);
		} else {
			int ppu = getPixelsPerUnit();
			int dimension = Version.getSize(version) * ppu;
			info.setText("Version: " + version + "\nDimensions: " + dimension + " x " + dimension  + "\nNumber of Characters: " + getText().length() + "\nEncoded size: " + (ba.length + 3) + "\nMaximum: " + max);
		}
	}
	/**
	 * Creates a GUI user-input fields for CREATE tab; Updates caluclated information in "Information" box
	 * @param builder
	 */
	public QRBuilderPanel(final QRBuilder builder) {
		super(builder);
		Font borderFont = new Font("Dialog", Font.BOLD, 14);

		input = new JTextArea(7, 25);
		input.setLineWrap(true);
		input.setWrapStyleWord(true);
		input.setTabSize(3);
		input.setFont(new Font("Dialog", Font.PLAIN, 16));
	//	JPanel tp = new JPanel(); //new BoxLayout(pane, BoxLayout.Y_AXIS)
		JScrollPane tp = new JScrollPane(input);
		TitledBorder tb3 = BorderFactory.createTitledBorder("QRCode Text:");
		tb3.setTitleFont(borderFont);
		tp.setBorder(tb3);
		tp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
	//	tp.add(input);

		ec = new JComboBox<ErrorCorrectionLevel>(ErrorCorrectionLevel.values());
		JPanel ecp = new JPanel(); //new BoxLayout(pane, BoxLayout.Y_AXIS)
		ecp.setToolTipText("Error Correction Level:");
		ec.setFont(new Font("Dialog", Font.PLAIN, 14));
		TitledBorder tb = BorderFactory.createTitledBorder("Error Correction Level:");
		tb.setTitleFont(borderFont);
		ecp.setBorder(tb);
		ecp.add(ec);

		pps = new JTextField(new JNumberFilter(), "8", 5);
		pps.setFont(new Font("Dialog", Font.PLAIN, 14));
		pps.addFocusListener(new FocusAdapter() {
				public void focusLost(final FocusEvent e) {
					JTextComponent c = (JTextComponent) e.getComponent();
					String text = c.getText();
					if (text == null || text == "") {
						c.setText("8");
					} else {
						long value;
						try {
							value = Long.valueOf(text);
						} catch (NumberFormatException nfe) {
							value = Long.MIN_VALUE;
						}
						if (value < 1) {
							c.setText("8");
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
		TitledBorder tb2 = BorderFactory.createTitledBorder("Pixels Per Unit:");
		tb2.setTitleFont(borderFont);
		ppsp.setBorder(tb2);
		ppsp.add(pps);
		
		info = new JTextArea("Version: \nDimensions: \nNumber of Characters: 0" );
		info.setEditable(false);
		//Font f = new Font(info.getFont());
		info.setOpaque(false);
		info.setFont(new Font("Dialog", Font.PLAIN, 14));
		JPanel ip = new JPanel(); //new BoxLayout(pane, BoxLayout.Y_AXIS)
		ip.setPreferredSize(new Dimension(250, 135));
		ip.setToolTipText("Information:");
		TitledBorder tb4 = BorderFactory.createTitledBorder("Information:");
		tb4.setTitleFont(borderFont);
		ip.setBorder(tb4);
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
				updateInfo();
			}
		});
		
		
		ec.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateInfo();
			}
		});
		
		pps.getDocument().addDocumentListener(new DocumentListener() {
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
				updateInfo();
			}
		});
		add(tp);
		JPanel userChoice = new JPanel(new BorderLayout());
		userChoice.setPreferredSize(new Dimension(200, 135));
		userChoice.add(ecp, BorderLayout.NORTH);
		userChoice.add(ppsp, BorderLayout.SOUTH);
		add(userChoice);
		add(ip);
		updateInfo();
	}
}