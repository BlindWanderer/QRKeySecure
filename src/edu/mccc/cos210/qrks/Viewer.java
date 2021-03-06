package edu.mccc.cos210.qrks;
import edu.mccc.cos210.qrks.util.*;
import javax.swing.*;
import javax.swing.filechooser.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
import java.security.*;
/**
 * Viewer: A JFrame that handles the construction of entire program.
 */
public class Viewer extends JFrame {
	Font buttonFont = new Font("Dialog", Font.BOLD, 14);
	static final byte[] SEED;
	static final int DIGEST_SIZE = 128;
	static final String ALGORITHM = "MD5withRSA";
	static {
		byte [] seed = null;
		try {
			seed = "Musicians love baubles but not sacks of cod.".getBytes("UTF-8");
		} catch (Exception e) {
			seed = null;
		}
		SEED = seed;
	}
	private static final long serialVersionUID = 1L;
	private Builder<BufferedImage> builder = new QRSecureBuilder();
	private Reader<BufferedImage, BufferedImage> [] readers;
	public JTabbedPane tabbedPane;
	private static final String DEFAULT_NAME = "QRKeySecure";
	/**
	 * Creates Tabs in the main window.
	 * @param pk public key, used to verify security (if applicable)
	 */
	public Viewer(PublicKey pk) {
		super(DEFAULT_NAME);
		readers = Utilities.newGenericArray(new QRSecureReader(pk));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel builderPanel = generateBuilderPanel();
		JPanel readerPanel = new QRReaderPanel(this, readers);
		tabbedPane = new JTabbedPane();
		tabbedPane.setFont(new Font("Dialog", Font.BOLD, 14));
		tabbedPane.addTab("READ", readerPanel);	//can add a custom icon later
		tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);
		tabbedPane.addTab("CREATE", builderPanel);
		tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);
		
		add(tabbedPane, BorderLayout.CENTER);
		pack();
		setVisible(true);
	}
	/**
	 * Sets the Title for the main window.
	 */
	public void setTitle(String title) {
		if(title == null || title == ""){
			super.setTitle(DEFAULT_NAME);
			super.setFont(new Font("Dialog", Font.ITALIC, 14));
		} else {
			super.setTitle(DEFAULT_NAME + " - " + title);
		}
	}
	private JPanel generateBuilderPanel() {
		JPanel builderPanel = new JPanel(new BorderLayout());
		final BuilderPanel<BufferedImage> builderGeneratedPanel = builder.generateGUI();
		JPanel fun = new JPanel(new GridLayout(0, 1));
		JPanel blah = new JPanel();
		final ImagePanel imageBox = new ImagePanel();
		imageBox.setPreferredSize(new Dimension(400, 500));
		final JButton generateImage = new JButton("Preview");
		generateImage.setFont(buttonFont);
		generateImage.setMnemonic(KeyEvent.VK_P);
		generateImage.addActionListener(new ActionListener() {
			SwingWorker sw;
			public void actionPerformed(final ActionEvent e) {
				final Factory<Item<BufferedImage>> factory = builderGeneratedPanel.getFactory();
				if (factory != null) {
					if (sw != null && !sw.isDone() && !sw.isCancelled()) {
						sw.cancel(true);
					}
					sw = new SwingWorker() {
						Item<BufferedImage> item;
						BufferedImage image;
						public Object doInBackground() {
							item = factory.runFactory();
							return image = item.save();
						}
						public void done() {
							imageBox.setImage(image);
						}
					};
					sw.execute();
				}
			}
		});
		final JButton saveImage = new JButton("Save Image");
		saveImage.setFont(buttonFont);
		saveImage.setMnemonic(KeyEvent.VK_S);
		saveImage.addActionListener(new ActionListener() {
			FileNameExtensionFilter[] exts = {
				new FileNameExtensionFilter("jpeg", "jpg", "jpeg"),
				new FileNameExtensionFilter("png", "png"),
				new FileNameExtensionFilter("gif", "gif"),
				new FileNameExtensionFilter("bmp", "bmp"),
				new FileNameExtensionFilter("tiff", "tif", "tiff"),
			};
			public void actionPerformed(final ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				fc.setAcceptAllFileFilterUsed(false);
				for (FileNameExtensionFilter ext : exts) {
					fc.addChoosableFileFilter(ext);
				}
				int returnVal = fc.showSaveDialog(Viewer.this);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					try {
						//TODO: figure out the type from the extension.
						ImageIO.write(imageBox.getImage(), "png", file);
					}
					catch (IOException ex) {	
					}
				} 
				//Reset the file chooser for the next time it's shown.
				fc.setSelectedFile(null);
			}
		});
		fun.add(builderGeneratedPanel); //, BorderLayout.CENTER);
		blah.add(generateImage); 
		blah.add(saveImage); 
		fun.add(blah); //, BorderLayout.SOUTH);
		
		builderPanel.add(imageBox, BorderLayout.CENTER);
		builderPanel.add(fun, BorderLayout.SOUTH);
		return builderPanel;
	}
}
