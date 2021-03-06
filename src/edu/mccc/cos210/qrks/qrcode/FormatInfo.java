package edu.mccc.cos210.qrks.qrcode;
/**
 * Provides BCH bits for Format Information.
 *
 */
public final class FormatInfo
{
	private FormatInfo() {
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
	/**
	 * Returns BCH bits based on dataBits
	 * @param databits 5 bits - 2 bit Encoding Mode and 3 bit finalMask number
	 * @return BCH bits from Table (Annex C in QRCode Specs)
	 */
	public static int getErrorCorrectionBits(int databits) {
		return DataBitsToErrorCorrectionBits[databits];
	}
	/**
	 * Runs Error Correction on Format Information (based on BCH bits);
	 * @param sequence Read-in Format Information
	 * @return Error Corrected Format Information
	 */
	public static CorrectedInfo getCorrectedFormatInfo(int sequence) {
		for(int i = 0; i < DataBitsToErrorCorrectionBits.length; i++){
			int data = (i << 10) | DataBitsToErrorCorrectionBits[i];
			int xor = data ^ sequence;
			int count = Integer.bitCount(xor);
			if (count < 4) {
				return new CorrectedInfo(count, sequence, data);
			}
		}
		return null;
	}
}