package edu.mccc.cos210.qrks.qrcode;

public static class CorrectedInfo {
	public final int errors;
	public final int original;
	public final int corrected;
	public CorrectedInfo(int errors, int original, int corrected) {
		this.errors = errors;
		this.original = original;
		this.corrected = corrected;
	}
}