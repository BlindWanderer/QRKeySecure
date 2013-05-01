package edu.mccc.cos210.qrks.qrcode;

public class Mask {

	public boolean[][] generateMask (int maskNum, int version) {
		boolean[][] mask = new boolean[Version.getSize(version)][Version.getSize(version)];
		switch(maskNum) {
			case 1:
				for (int i = 0; i < Version.getSize(version); i++) {
					for (int j = 0; j < Version.getSize(version); j++) {
						mask[i][j] = (i+j) % 2 == 0;
					}
				}
				return mask;
				
			case 2:
				for (int i = 0; i < Version.getSize(version); i++) {
					for (int j = 0; j < Version.getSize(version); j++) {
						mask[i][j] = i % 2 == 0;
					}
				}
				return mask;
				
			case 3:
				for (int i = 0; i < Version.getSize(version); i++) {
					for (int j = 0; j < Version.getSize(version); j++) {
						mask[i][j] = j % 3 == 0;
					}
				}
				return mask;
				
			case 4:
				for (int i = 0; i < Version.getSize(version); i++) {
					for (int j = 0; j < Version.getSize(version); j++) {
						mask[i][j] = (i+j) % 3 == 0;
					}
				}
				return mask;
				
			case 5:
				for (int i = 0; i < Version.getSize(version); i++) {
					for (int j = 0; j < Version.getSize(version); j++) {
						mask[i][j] = ((i / 2) + (j / 3)) % 2 == 0;
					}
				}
				return mask;
				
			case 6:
				for (int i = 0; i < Version.getSize(version); i++) {
					for (int j = 0; j < Version.getSize(version); j++) {
						mask[i][j] = (i * j) % 2 + (i * j) % 3 == 0;
					}
				}
				return mask;
				
			case 7:
				for (int i = 0; i < Version.getSize(version); i++) {
					for (int j = 0; j < Version.getSize(version); j++) {
						mask[i][j] = ((i* j) % 2 + (i *j) % 3) % 2 == 0;
					}
				}
				return mask;
			
			case 8:
				for (int i = 0; i < Version.getSize(version); i++) {
					for (int j = 0; j < Version.getSize(version); j++) {
						mask[i][j] = ((i* j) % 3 + (i+j) % 2) % 2 == 0;
					}
				}
				return mask;
			default:
				return null;
			
		}
	
	}

	public boolean[][] finalMask (boolean[][] patternMask, boolean[][] dataMask, int num, int version) {
		dataMask = Version.getDataMask(version);
		patternMask = generateMask(num, version);
		boolean[][] finalMask = new boolean[dataMask.length][dataMask.length];
		for (int i = 0; i < Version.getSize(version); i++) {
			for (int j = 0; j < Version.getSize(version); j++) {
				finalMask[i][j] = dataMask[i][j] ^ patternMask[i][j];
				
				
			}
		}		
		
		return finalMask;
	}
}