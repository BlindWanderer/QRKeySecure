package edu.mccc.cos210.qrks;
import edu.mccc.cos210.qrks.util.*;
import javax.swing.*;
import javax.swing.filechooser.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
/**
 * Viewer: A JFrame that handles the construction of entire program.
 */
public class Viewer extends JFrame {
	private static final long serialVersionUID = 1L;
	private Builder<BufferedImage> builder = new QRSecureBuilder();
	private Reader<BufferedImage, BufferedImage> [] readers = Utilities.newGenericArray(new QRSecureReader());
	public JTabbedPane tabbedPane;
	private static final String DEFAULT_NAME = "QRKeySecure";
	public Viewer() {
		super(DEFAULT_NAME);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel builderPanel = generateBuilderPanel();
		JPanel readerPanel = new QRReaderPanel(this, readers);
		tabbedPane = new JTabbedPane();
		tabbedPane.addTab("READ", readerPanel);	//can add a custom icon later
		tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);
		tabbedPane.addTab("CREATE", builderPanel);
		tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);
		
		add(tabbedPane, BorderLayout.CENTER);
		pack();
		setVisible(true);
	}
	public void setTitle(String title) {
		if(title == null || title == ""){
			super.setTitle(DEFAULT_NAME);
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
		final JButton generateImage = new JButton("Preview");
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
						//TODO: figure out the tyep from the extension.
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
		blah.add(generateImage); //, BorderLayout.SOUTH);
		blah.add(saveImage); //, BorderLayout.SOUTH);
		fun.add(blah); //, BorderLayout.SOUTH);
		
		builderPanel.add(imageBox, BorderLayout.CENTER);
		builderPanel.add(fun, BorderLayout.SOUTH);
		return builderPanel;
	}
}
