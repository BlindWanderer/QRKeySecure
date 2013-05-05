package edu.mccc.cos210.qrks.qrcode;

public class FormatInfo
{
	public final int errors;
	public final int original;
	public final int corrected;
	private FormatInfo(int errors, int original, int corrected) {
		this.errors = errors;
		this.original = original;
		this.corrected = corrected;
	}
	public static final int [] DataBitsToErrorCorrectionBits = {
		0b0000000000,
		0b0100110111,
		0b1001101110,
		0b1101011001,
		0b0111101011,
		0b0011011100,
		0b1110000101,
		0b1010110010,
		0b1111010110,
		0b1011100001,
		0b0110111000,
		0b0010001111,
		0b1000111101,
		0b1100001010,
		0b0001010011,
		0b0101100100,
		0b1010011011,
		0b1110101100,
		0b0011110101,
		0b0111000010,
		0b1101110000,
		0b1001000111,
		0b0100011110,
		0b0000101001,
		0b0101001101,
		0b0001111010,
		0b1100100011,
		0b1000010100,
		0b0010100110,
		0b0110010001,
		0b1011001000,
		0b1111111111,
	};
	public static int getErrorCorrectionBits(int databits) {
		return DataBitsToErrorCorrectionBits[databits];
	}
	public static FormatInfo getFormatInfo(int sequence) {
		for(int i = 0; i < 32; i++){
			int data = (i << 10) | DataBitsToErrorCorrectionBits[0];
			int xor = data ^ sequence;
			if(xor == 0) {
				return new FormatInfo(0, sequence, data);
			} else {
				xor = xor & (xor - 1);
				if(xor == 0) {
					return new FormatInfo(1, sequence, data);
				} else {
					xor = xor & (xor - 1);
					if(xor == 0) {
						return new FormatInfo(2, sequence, data);
					} else {
						xor = xor & (xor - 1);
						if(xor == 0) {
							return new FormatInfo(3, sequence, data);
						}
					}
				}
			}
		}
		return null;
	}
}