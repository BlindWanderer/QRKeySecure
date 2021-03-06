package edu.mccc.cos210.qrks;
import edu.mccc.cos210.qrks.util.*;

import javax.swing.*;

import java.awt.event.*;
import java.awt.image.*;

import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.filechooser.*;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URLEncoder;

import javax.imageio.*;

import java.util.*;
import java.util.regex.*;
import java.util.concurrent.ExecutionException;
import java.awt.dnd.*;
import java.awt.datatransfer.*;
import java.awt.Font;
//import java.awt.*;
import java.awt.Image;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.Image;

public class QRReaderPanel extends JPanel {
    private static final long serialVersionUID = 1L;
	private Image image;
	private Reader<BufferedImage, BufferedImage> [] readers;
	Font buttonFont = new Font("Dialog", Font.BOLD, 14);
	private volatile SwingWorker<java.util.List<Item<BufferedImage>>, BufferedImage> swp;
	final Viewer viewer;
	public static final List<javax.swing.filechooser.FileFilter> IMAGE_FILE_NAME_FILTERS;
	static {
		FileNameExtensionFilter[] fnef = {
				new FileNameExtensionFilter("png", "png"),
				new FileNameExtensionFilter("jpeg", "jpg", "jpeg"),
				new FileNameExtensionFilter("gif", "gif"),
				new FileNameExtensionFilter("bmp", "bmp"),
				new FileNameExtensionFilter("tiff", "tif", "tiff"),
			};
		List<String> names = new LinkedList<String>();
		for (FileNameExtensionFilter f : fnef) {
			names.addAll(Arrays.asList(f.getExtensions()));
		}
		List<javax.swing.filechooser.FileFilter> exts = new ArrayList<>(fnef.length + 1);
		exts.add(new FileNameExtensionFilter("All Images", names.toArray(new String[0])));
		exts.addAll(Arrays.asList(fnef));
		IMAGE_FILE_NAME_FILTERS = Collections.unmodifiableList(exts);
	}
	private void tabCleanup() {
		while (viewer.tabbedPane.getTabCount() > 2){
			viewer.tabbedPane.remove(2);
		}
	}
	public QRReaderPanel(final Viewer viewer, final Reader<BufferedImage, BufferedImage> [] readers){
		this.viewer = viewer;
		this.readers = readers;
		this.setPreferredSize(new Dimension(600, 800));
		this.setLayout(new BorderLayout());
		
		final CardHistoryLayout cl = new CardHistoryLayout(2);
		final JPanel stack = new JPanel(cl);

		final Camera camera = new Camera();
		this.add(camera, BorderLayout.CENTER);
		this.add(stack, BorderLayout.SOUTH);

		final DropTargetListener ddl = new DropTargetAdapter() {
			@Override
			public void dragEnter(DropTargetDragEvent event) {
				if (event.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
					event.acceptDrag(DnDConstants.ACTION_COPY);
				} else {
					event.rejectDrag();
				}
			}
			@Override
			public void drop(DropTargetDropEvent event) {
				Transferable transferable = event.getTransferable();
				boolean success = false;
				if (transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
					event.acceptDrop(DnDConstants.ACTION_COPY);
					try {
						List files = (List) transferable.getTransferData(DataFlavor.javaFileListFlavor);
						if (files.size() > 0) {
							File file = (File)files.get(0);
							BufferedImage fi = ImageIO.read(file);
							if (fi != null) {
								if (swp != null && !swp.isDone()) {
									swp.cancel(true);
								}
								tabCleanup();
								cl.show(stack, "4");
								image = camera.setImage(fi);
								viewer.setTitle(file.getName());
							}
							success = true;
						}
					} catch (UnsupportedFlavorException e) {
//						e.printStackTrace();
					} catch (IOException e) {
//						e.printStackTrace();
					}
				} else {
					event.rejectDrop();
				}
				event.dropComplete(success);
			}
		};
		new DropTarget(this, ddl);
		
		ActionListener sal = new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				camera.setImage(null);
				tabCleanup();
				cl.show(stack, "1");
				viewer.setTitle("");
			}
		};
		ActionListener pal = new ActionListener() {
				public void actionPerformed(final ActionEvent e) {
					cl.show(stack, "5");
					//TODO something about this line does not work.
					if (swp != null && !swp.isDone()) {
						swp.cancel(true);
					}
					tabCleanup();
					swp = new DelegatingSwingWorker<java.util.List<Item<BufferedImage>>, BufferedImage>() {
						volatile List<Item<BufferedImage>> out;
						@Override
						public java.util.List<Item<BufferedImage>> doInBackground() {
							SwingWorkerProtected<?, BufferedImage> p = this.getProtected();
							out = new LinkedList<Item<BufferedImage>>();
							for(Reader<BufferedImage, BufferedImage> reader : readers) {
								List<Item<BufferedImage>> t = reader.process(Utilities.convertImageToBufferedImage(image), p);
								if (t != null) {
									out.addAll(t);
								}
							}
							return out;
						}
						@Override
						public void done() {
//							camera.setImage(image);//restore the image
							if (swp == this && !isCancelled()) {
								tabCleanup();
								int number = 0;
								for (Item<BufferedImage> bi : out) {
									number++;
									QRCode code = (QRCode)bi;
									boolean isSecure = code.getSecure();
									String qr = "QR" + number;
									viewer.tabbedPane.add(qr, bi.generateGUI());
								/*	int mn = 3;
									String keyEvent = "KeyEvent.VK_" + mn;
									viewer.tabbedPane.setMnemonicAt(0, keyEvent);
									number++;
									mn++;*/
									final String url;
									String text = code.getText();
									String blah = text;
									String param = null;
									if (text != null) {
										try {
											param = URLEncoder.encode(text, "UTF-8");
										} catch (UnsupportedEncodingException e) {
											param = "UnsupportedEncodingException";
										}
										if (isSecure) {
											try {
												new java.net.URL(text);
											}
											catch (MalformedURLException ex) {
//												if (!blah.matches("(http[s]?|file)://.*")) {
													blah = "http://google.com/search?q=" + param;
//												}
											}
											url = blah;
										} else {
											url = "http://google.com/search?q=" + param;
										}
										try {
											final JEditorPane htmlPane = new JEditorPane(url);
											htmlPane.setEditable(false);
											htmlPane.addHyperlinkListener(new HyperlinkListener(){
												 public void hyperlinkUpdate(HyperlinkEvent event) {
													    if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
													      try {
													        htmlPane.setPage(event.getURL());
													       // urlField.setText(event.getURL().toExternalForm());
													      } catch(IOException ioe) {
													    	  System.err.println("Error displaying " + url);
													      }
													    }
													 }
											});
											if (isSecure) {
												viewer.tabbedPane.addTab(qr + "WEB-Secure", new JScrollPane(htmlPane));

											} else {
												viewer.tabbedPane.addTab(qr + "WEB", new JScrollPane(htmlPane));
											}
										} catch(IOException ioe) {
											System.err.println("Error displaying " + url + ": " + ioe);
										}
									}
								}
								cl.show(stack, "6");
							}
							System.out.println("done");
						}
						
						
						
						@Override
						public void process(java.util.List<BufferedImage> imgs) {
							if(swp == this){
								camera.setImage(imgs.get(imgs.size() - 1));
							}
						}
					};
					swp.execute();
				}
			};
		ActionListener tpal = new ActionListener() {
				public void actionPerformed(final ActionEvent e) {
					image = camera.takePicture();
					cl.show(stack, "3");
				}
			};
		ActionListener scal = new ActionListener() {
				public void actionPerformed(final ActionEvent e) {
					camera.showCamera();
					viewer.setTitle("<Camera>");
					cl.show(stack, "2");
				}
			};
		ActionListener oal = new ActionListener() {			
			public void actionPerformed(final ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				fc.setFileFilter(IMAGE_FILE_NAME_FILTERS.get(0));
				for (javax.swing.filechooser.FileFilter ext : IMAGE_FILE_NAME_FILTERS) {
					fc.addChoosableFileFilter(ext);
				}
				fc.setAcceptAllFileFilterUsed(true);
				ImagePreview ip = new ImagePreview(fc);
				fc.setAccessory(ip);
				
				int returnVal = fc.showDialog(viewer, "Open Image");
				
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					Image fi = ip.getImage();
					File file = fc.getSelectedFile();
					if (fi == null) { //don't reopen the file unless strictly necessary
						try {
							fi = ImageIO.read(file);
						} catch (IOException ex) {
							fi = null;
							//TODO: display error message about trouble reading file. Status bar or dialog box?
						}
					}
					if (fi != null) {
						if (swp != null && !swp.isDone()) {
							swp.cancel(true);
						}
						cl.show(stack, "4");
						image = camera.setImage(fi);
						viewer.setTitle(file.getName());
					}
				}
				//Reset the file chooser for the next time it's shown.
				fc.setSelectedFile(null);
			}
		};
		{ //STATE 1
			JPanel state1 = new JPanel(new BorderLayout());
			stack.add(state1, "1");
			final JButton openImage = new JButton("Open Image");
			openImage.setFont(buttonFont);
			openImage.setMnemonic(KeyEvent.VK_O);
			openImage.addActionListener(oal);
			state1.add(openImage, BorderLayout.LINE_START);
			final JButton startVideo = new JButton("Start Video");
			startVideo.setFont(buttonFont);
			startVideo.setMnemonic(KeyEvent.VK_S);
			state1.add(startVideo, BorderLayout.LINE_END);
			startVideo.addActionListener(scal);
			if (!camera.isCameraAvailable()) {
				startVideo.setEnabled(false);
				startVideo.setToolTipText("Camera not available at this time.");
			}
		}
		{ //STATE 2
			JPanel state2 = new JPanel(new BorderLayout());
			stack.add(state2, "2");
			final JButton openImage = new JButton("Open Image");
			openImage.setFont(buttonFont);
			openImage.setMnemonic(KeyEvent.VK_O);
			openImage.addActionListener(oal);
			state2.add(openImage, BorderLayout.LINE_START);
			final JButton takeSnapshot = new JButton("Take Snapshot");
			takeSnapshot.setFont(buttonFont);
			takeSnapshot.setMnemonic(KeyEvent.VK_T);
			state2.add(takeSnapshot, BorderLayout.LINE_END);
			takeSnapshot.addActionListener(tpal);
		}
		{ //STATE 3
			JPanel state3 = new JPanel(new BorderLayout());
			stack.add(state3, "3");
			final JButton retake = new JButton("Retake");
			retake.setFont(buttonFont);
			retake.setMnemonic(KeyEvent.VK_R);
			state3.add(retake, BorderLayout.LINE_START);
			final JButton process = new JButton("Process");
			process.setFont(buttonFont);
			process.setMnemonic(KeyEvent.VK_P);
			state3.add(process, BorderLayout.LINE_END);
			retake.addActionListener(scal);
			process.addActionListener(pal);
		}
		{ //STATE 4
			JPanel state4 = new JPanel(new BorderLayout());
			stack.add(state4, "4");
			final JButton goBack = new JButton("Go Back");
			goBack.setFont(buttonFont);
			goBack.setMnemonic(KeyEvent.VK_G);
			state4.add(goBack, BorderLayout.LINE_START);
			final JButton process = new JButton("Process");
			process.setFont(buttonFont);
			process.setMnemonic(KeyEvent.VK_P);
			state4.add(process, BorderLayout.LINE_END);
			goBack.addActionListener(sal);
			process.addActionListener(pal);
		}
		{ //STATE 5
			JPanel state5 = new JPanel(new BorderLayout());
			stack.add(state5, "5");
			final JButton stop = new JButton("Stop");
			stop.setFont(buttonFont);
			stop.setMnemonic(KeyEvent.VK_T);
			state5.add(stop, BorderLayout.CENTER);
			stop.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent e) {
					swp.cancel(true);
					cl.showPrevious(stack);
				}
			});
		}
		{ //STATE 6
			JPanel state6 = new JPanel(new BorderLayout());
			stack.add(state6, "6");
			final JButton startOver = new JButton("Start Over");
			startOver.setFont(buttonFont);
			startOver.setMnemonic(KeyEvent.VK_O);
			state6.add(startOver, BorderLayout.CENTER);
			startOver.addActionListener(sal);
		}
		
		cl.show(stack, "1");
	}
}