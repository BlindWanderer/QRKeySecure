package edu.mccc.cos210.qrks.qrcode;
import java.util.*;

public enum EncodingMode {
	BYTE(0b0100),
	ECI(0b0111),
	NUMERIC(0b0001),
	ALPHANUMERIC(0b0010),
	KANJI(0b1000),
	STRUCTURED_APPEND(0b0011),
	FNC1_FIRST(0b0101),
	FNC1_SECOND(0b1001),
	TERMINATOR(0b0000);
	public final byte value;
	EncodingMode(int value) {
		this.value = (byte)value;
	}
	private static EncodingMode[] ems;
	static {
		EncodingMode[] t = EncodingMode.values();
		ems = new EncodingMode[16];
		Arrays.fill(ems, null);
		for (EncodingMode em : t) {
			ems[em.value] = em;
		}
	}
	public static EncodingMode parseValue(final int value) {
		return ems[value];
	}
}