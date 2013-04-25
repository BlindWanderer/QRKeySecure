package edu.mccc.cos210.qrks;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.filechooser.*;
import java.io.*;
import java.awt.image.*;
import javax.imageio.*;

public abstract class JBuilderPanel<T> extends JPanel {
	public abstract Generator<BufferedImage> getGenerator();
}