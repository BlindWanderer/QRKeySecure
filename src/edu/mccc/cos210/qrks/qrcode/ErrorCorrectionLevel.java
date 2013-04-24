package edu.mccc.cos210.qrks.qrcode;
import java.util.*;
import javax.swing.*;
import java.awt.*;

public enum ErrorCorrectionLevel {
	L(7),
	M(15),
	Q(25),
	H(30);
	private final int percentage;
	ErrorCorrectionLevel(int percentage) {
		this.percentage = percentage;
	}
	public String toString() {
		return getName() + " ~ " + percentage + "%";
	}
	public String getName() {
		return super.toString();
	}
/*    public static void main(String[] args) {
        for (ErrorCorrectionLevel p : ErrorCorrectionLevel.values())
           System.out.println(p);
    }*/
}