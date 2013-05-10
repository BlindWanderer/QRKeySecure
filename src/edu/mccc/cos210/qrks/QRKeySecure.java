package edu.mccc.cos210.qrks;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;
import java.security.*;
import java.io.*;
import java.security.spec.*;
/**
 * QRKeySecure: Main entry point for the program. Continues execution via the EventQueue.
 */
public final class QRKeySecure {
	private QRKeySecure() {
	}
	public static PublicKey getPublicKey(String file){
		try {
			FileInputStream keyfis = new FileInputStream(file);
			byte[] encKey = new byte[keyfis.available()];  
			keyfis.read(encKey);
			keyfis.close();
			X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(encKey);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			return keyFactory.generatePublic(pubKeySpec);
		} catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e ){
			e.printStackTrace();
			return null;
		}
	}
	public static void main(final String[] sa) {
		final PublicKey pk = sa.length > 0 ? getPublicKey(sa[0]) : null;
		EventQueue.invokeLater(
			new Runnable() {
				@Override
				public void run() {
					new Viewer(pk);
				}
			}
		);
	}
}
