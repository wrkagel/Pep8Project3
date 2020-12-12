package model;

import view.GUI;

/**
 * Represents an Arithmetic and Logic Unit used by a virtual computer.
 * @author Group 6: John Morton
 * @version 10/16/2020
 */
public class ALU {
	private Flag nFlag;
	private Flag zFlag;
	private Flag vFlag;
	private Flag cFlag;
	private GUI view;
	/**
	 * Initializes our ALU.
	 */
	public ALU(GUI v) {
		nFlag = Flag.N;
		zFlag = Flag.Z;
		vFlag = Flag.V;
		cFlag = Flag.C;
		view = v;
	}
	
	/**
	 * Adds two shorts together, this method breaks the shorts down to simulate the internal workings of an ALU.
	 * @param x1 The first short value.
	 * @param x2 The second short value.
	 * @return The sum of our two short values.
	 */
	public short add(short x1, short x2) {
		boolean[] boolArray1 = toBoolArray(x1);
		boolean[] boolArray2 = toBoolArray(x2);
		boolean val1 = false;
		boolean val2 = false;
		boolean carry = false;
		boolean[] booleanResult = new boolean[16];
		if(boolArray1.length == boolArray2.length) {
			for (int i = boolArray1.length - 1; i >= 0; i--) {
				val1 = boolArray1[i];
				val2 = boolArray2[i];
				if (val1 & val2) {
					if (carry) {
						booleanResult[i] = true;
						carry = true;
					} else {
						booleanResult[i] = false;
						carry = true;
					}
				} else if (val1 || val2) {
					if (carry) {
						booleanResult[i] = false;
						carry = true;
					} else {
						booleanResult[i] = true;
						carry = false;
					}
				} else {
					if (carry) {
						booleanResult[i] = true;
						carry = false;
					} else {
						booleanResult[i] = false;
						carry = false;
					}
				}
			}
		}
		if (carry) {
			view.setCbox(true);
		}
		short s = toShort(booleanResult);
		if (s == 0) {
			zFlag.setFlag(true);
			view.setZbox(true);
		} else if (s < 0) {
			nFlag.setFlag(true);
			view.setNbox(true);
		}
		return s;
	}
	
	/**
	 * Add two short values together when one of them is stored in a register.
	 * @param x The first short value that we want to add.
	 * @param reg The register where our second short value is stored.
	 * @return The sum of our two short values.
	 */
	public short add(Register reg, short x) {
		return add(reg.getReg(), x);
	}
	
	/**
	 * Subtract two short values, this method breaks them down to simulate the inner workings of an ALU.
	 * @param x1 The first short value that we want to manipulate.
	 * @param x2 The short value that we wish to subtract from x1.
	 * @return The result of x1 - x2, this will be a short.
	 */
	public short subtract(short x1, short x2) {
		//subtract by adding the negative of the second number to the first
		boolean[] boolArray2 = toBoolArray(x2);
		for (int i = 0; i < boolArray2.length; i++) {
			if (boolArray2[i]) {
				boolArray2[i] = false;
			} else {
				boolArray2[i] = true;
			}
		}
		boolean breaker = false;
		for (int i = boolArray2.length - 1; i >= 0; i--) {
			if (!breaker) {
				if (boolArray2[i]) {
					boolArray2[i] = false;
				} else {
					boolArray2[i] = true;
					breaker = true;
				}
			}
		}
		return add(x1, toShort(boolArray2));
	}
	
	/**
	 * Subtracts two short values, one of which is stored in a register.
	 * @param reg The short value that we will manipulate, stored in a register.
	 * @param x The short value which we will subtract from the value in the register.
	 * @return The result of the subtraction.
	 */
	public short subtract(Register reg, short x) {
		return subtract(reg.getReg(), x);
	}
	
	/**
	 * Subtracts two short values, one of which is stored in a register.
	 * @param x The short value that we will manipulate.
	 * @param reg A register holding a short value which we will subtract from x.
	 * @return The result of our subtraction.
	 */
	public short subtract(short x, Register reg) {
		return subtract(reg.getReg(), x);
	}

	/**
	 * Returns a negated short value.
	 * @param x1 input short
	 * @return x1 * -1;
	 */
	public short negate(short x1) {
		short result = (short) (x1 * -1);
		nFlag.setFlag(result < 0);
		zFlag.setFlag(result == 0);
		vFlag.setFlag(result == x1);
		view.setNbox(nFlag.isSet());
		view.setZbox(zFlag.isSet());
		view.setVbox(vFlag.isSet());
		return result;
	}

	/**
	 * Bitwise inverts a short value.
	 * @param x1 input short value.
	 * @return inverted short
	 */
	public short invert(short x1) {
		boolean[] boolArray = toBoolArray(x1);
		for (int i = 0; i < boolArray.length; i++) {
			boolArray[i] = !boolArray[i];
		}
		short result = toShort(boolArray);
		nFlag.setFlag(result < 0);
		zFlag.setFlag(result == 0);
		view.setNbox(nFlag.isSet());
		view.setZbox(zFlag.isSet());
		return result;
	}

	public short and(Register r, short x2) {
		short x1 = r.getReg();
		short ret = (short) (x1 & x2);
		if (ret < 0) {
			nFlag.setFlag(true);
			zFlag.setFlag(false);
		} else if (ret == 0) {
			nFlag.setFlag(false);
			zFlag.setFlag(true);
		}
		view.setNbox(nFlag.isSet());
		view.setZbox(zFlag.isSet());
		return ret;
	}

	public short or(Register r, short x2) {
		short x1 = r.getReg();
		short ret = (short) (x1 | x2);
		if (ret < 0) {
			nFlag.setFlag(true);
			zFlag.setFlag(false);
		} else if (ret == 0) {
			zFlag.setFlag(true);
			nFlag.setFlag(false);
		}
		view.setNbox(nFlag.isSet());
		view.setZbox(zFlag.isSet());
		return ret;
	}

	public short arithShiftLeft(Register r) {
		resetFlags();
		short x = r.getReg();
		boolean[] boolArr1 = toBoolArray(x);
		cFlag.setFlag(boolArr1[0]);
		view.setCbox(boolArr1[0]);
		x <<= 1;
		boolean[] boolArr2 = toBoolArray(x);
		if (boolArr2[0] != boolArr1[0]) {
			vFlag.setFlag(true);
			view.setVbox(true);
		}

		if (x < 0) {
			nFlag.setFlag(true);
			view.setNbox(true);
		} else if (x == 0) {
			zFlag.setFlag(true);
			view.setZbox(true);
		}

		return x;
	}

	public short arithShiftRight(Register r) {
		resetFlags();
		short x = r.getReg();
		boolean[] boolArr = toBoolArray(x);
		cFlag.setFlag(boolArr[boolArr.length - 1]);
		view.setCbox(boolArr[boolArr.length - 1]);

		x >>= 1;
		if (x < 0) {
			nFlag.setFlag(true);
			view.setNbox(true);
		} else if (x == 0) {
			zFlag.setFlag(true);
			view.setZbox(true);
		}

		return x;
	}

	public short rotateLeft(Register r) {
		short x = r.getReg();
		boolean carryBit = cFlag.isSet();
		resetFlags();
		boolean[] boolArr1 = toBoolArray(x);
		cFlag.setFlag(boolArr1[0]);
		view.setCbox(boolArr1[0]);
		x <<= 1;
		boolean[] boolArr2 = toBoolArray(x);
		boolArr2[boolArr2.length - 1] = carryBit;
		return toShort(boolArr2);
	}

	public short rotateRight(Register r) {
		short x = r.getReg();
		boolean carryBit = cFlag.isSet();
		resetFlags();
		boolean[] boolArr1 = toBoolArray(x);
		cFlag.setFlag(boolArr1[boolArr1.length - 1]);
		view.setCbox(boolArr1[boolArr1.length - 1]);
		x >>= 1;
		boolean[] boolArr2 = toBoolArray(x);
		boolArr2[0] = carryBit;
		return toShort(boolArr2);
	}

	public void compare(Register r, short x2) {
		resetFlags();
		short x1 = r.getReg();
		// by subtracting without returning, comparison is complete
		subtract(r, x2);
	}

	public boolean nFlagIsSet() {
		return nFlag.isSet();
	}

	public boolean zFlagIsSet() {
		return zFlag.isSet();
	}

	public boolean vFlagIsSet() {
		return vFlag.isSet();
	}

	public boolean cFlagIsSet() {
		return cFlag.isSet();
	}

	private void resetFlags() {
		view.setNbox(false);
		view.setZbox(false);
		view.setVbox(false);
		view.setCbox(false);
		nFlag.setFlag(false);
		zFlag.setFlag(false);
		vFlag.setFlag(false);
		cFlag.setFlag(false);
	}
	
	/**
	 * A helper function which converts a short into a 16 bit boolean array.
	 * @param x The short that we will manipulate.
	 * @return A boolean array equivalent to our short value.
	 */
	private boolean[] toBoolArray(short x) {
		boolean[] rtnArray = new boolean[16];
		short twoPow[] = {16384, 8192, 4096, 2048, 1024, 512, 256, 128, 64, 32, 16, 8, 4, 2, 1};
		for (int i = 0; i < rtnArray.length; i++) {
			if (i == 0) {
				if (x < 0) {
					rtnArray[i] = true;
					x = (short) (Short.toUnsignedInt(x) - 32768);
				} else {
					rtnArray[i] = false;
				}
			} else {
				if (x >= twoPow[i - 1]) {
					rtnArray[i] = true;
					x -= twoPow[i - 1];
				} else {
					rtnArray[i] = false;
				}
			}
		}
		return rtnArray;
	}
	
	/**
	 * A helper method which converts a boolean array to a short.
	 * @param boolArray The boolean array that we wish to convert.
	 * @return A short value representing the input boolean array.
	 */
	private short toShort(boolean[] boolArray) {
		short rtnShort = 0;
		short twoPow[] = {16384, 8192, 4096, 2048, 1024, 512, 256, 128, 64, 32, 16, 8, 4, 2, 1};
		for (int i = 0; i < boolArray.length; i++) {
			if (i == 0) {
				if (boolArray[i]) {
					rtnShort = (short) -32768;
				}
			} else {
				if (boolArray[i]) {
					rtnShort += twoPow[i - 1];
				}
			}
		}
		return rtnShort;
	}
}
