package edu.mccc.cos210.qrks.qrcode;
import java.util.*;
import javax.swing.*;
import java.awt.*;

public enum ErrorCorrectionLevel {
	L(0, 7),
	M(1, 15),
	Q(2, 25),
	H(3, 30);
	private static ErrorCorrectionLevel[] ecls;
	static {
		ErrorCorrectionLevel[] t = ErrorCorrectionLevel.values();
		ecls = new ErrorCorrectionLevel[t.length];
		for (ErrorCorrectionLevel ecl : t) {
			ecls[ecl.index] = ecl;
		}
	}
	public final int index;
	public final int percentage;
	private ErrorCorrectionLevel(final int index, final int percentage) {
		this.index = index;
		this.percentage = percentage;
	}
	public String toString() {
		return getName() + " ~ " + percentage + "%";
	}
	public String getName() {
		return super.toString();
	}
	static ErrorCorrectionLevel parseIndex(final int index) {
		return ecls[index];
	}
	/*
	public static void main(String[] args) {
		for (ErrorCorrectionLevel p : ErrorCorrectionLevel.values()) {
			System.out.println(p);
		}
	}
	*/
}