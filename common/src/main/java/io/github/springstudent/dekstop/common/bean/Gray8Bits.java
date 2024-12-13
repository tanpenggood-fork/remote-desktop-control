package io.github.springstudent.dekstop.common.bean;

public enum Gray8Bits {
	X_256(256),
	X_128(128),
	X_64(64),
	X_32(32),
	X_16(16),
	X_8(8),
	X_4(4);

	private final int levels;

	Gray8Bits(int levels) {
		this.levels = levels;
	}

	public static Gray8Bits toGrayLevel(int value) {
		switch (value) {
			case 6:
				return Gray8Bits.X_256;
			case 5:
				return Gray8Bits.X_128;
			case 4:
				return Gray8Bits.X_64;
			case 3:
				return Gray8Bits.X_32;
			case 2:
				return Gray8Bits.X_16;
			case 1:
				return Gray8Bits.X_8;
			default:
				return Gray8Bits.X_4;
		}
	}

	public int getLevels() {
		return levels;
	}


}
