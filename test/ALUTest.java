import model.ALU;
import model.Register;
import org.junit.Before;
import org.junit.Test;
import view.GUI;

import javax.swing.*;

import static org.junit.Assert.*;

/**
 * JUnit4 tests for the ALU class. All tests should pass.
 * 
 * @author Group 6: Anna Blanton
 * @version 10/18/2020
 */
public class ALUTest {
	ALU alu;
	Register r;
	GUI view;

	@Before
	public void setUp() {
		view = new GUI();
		alu = new ALU(view);
		r = new Register();
	}

	@Test
	public void testAdd() {
		r.load((short) 13);
		assertEquals(20, alu.add(r, (short) 7));
	}

	@Test
	public void testAddOverflowPositive() {
		r.load((short) 0x7FFF);
		short result = alu.add(r, (short) 0x7FFF);
		assertTrue(alu.cFlagIsSet());
		assertTrue(alu.vFlagIsSet());
		assertFalse(alu.zFlagIsSet());
		assertTrue(alu.nFlagIsSet());
		assertEquals(-2, result);
	}

	@Test
	public void testAddOverflowNegative() {
		r.load((short) -32000);
		short result = alu.add(r, (short) -769);
		assertTrue(alu.cFlagIsSet());
		assertTrue(alu.vFlagIsSet());
		assertFalse(alu.zFlagIsSet());
		assertFalse(alu.nFlagIsSet());
		assertEquals(32767, result);
	}

	@Test
	public void testSubtract() {
		r.load((short) 15);
		assertEquals(5, alu.subtract(r, (short) 10));
	}

	@Test
	public void testSubtractCarryBitPositive() {
		r.load((short) 10);
		alu.subtract(r, (short) 5);
		assertTrue(alu.cFlagIsSet());
		alu.subtract(r, (short) -11);
		assertFalse(alu.cFlagIsSet());
		alu.subtract(r, (short) 10);
		assertTrue(alu.cFlagIsSet());
	}

	@Test
	public void testSubtractCarryBitNegative() {
		r.load((short) -10);
		alu.subtract(r, (short) 5);
		assertTrue(alu.cFlagIsSet());
		alu.subtract(r, (short) -11);
		assertTrue(alu.cFlagIsSet());
		alu.subtract(r, (short) -4);
		assertFalse(alu.cFlagIsSet());
	}

	@Test
	public void testAddOverflow() {
		r.load((short) 32767);
		assertEquals(-32765, alu.add(r, (short) 4));
	}

	@Test
	public void testSubtractUnderflow() {
		r.load((short) -32768);
		assertEquals(32765, alu.subtract(r, (short) 3));
	}

	@Test
	public void testSubtractToNeg() {
		r.load((short) 3);
		assertEquals(-3, alu.subtract(r, (short) 6));
	}

	@Test
	public void testAddToPos() {
		r.load((short) -3);
		assertEquals(3, alu.add(r, (short) 6));
	}

	@Test
	public void testAnd() {
		r.load((short) 10);
		assertEquals(2, alu.and(r, (short) 2));
	}

	@Test
	public void testOr() {
		r.load((short) 12);
		assertEquals(13, alu.or(r, (short) 13));
	}

	@Test
	public void testROR() {
		r.load((short) 17);
		assertEquals(8, alu.rotateRight(r));
	}

	@Test
	public void testROL() {
		r.load((short) 30);
		assertEquals(60, alu.rotateLeft(r));
	}

	@Test
	public void testArithShiftLeft() {
		r.load((short) 25);
		assertEquals(50, alu.arithShiftLeft(r));
	}

	@Test
	public void testArithShiftRight() {
		r.load((short) 10);
		assertEquals(5, alu.arithShiftRight(r));
	}

	@Test
	public void testInvertZFlag() {
		r.load((short) -1);
		r.load(alu.invert(r.getReg()));
		assertFalse(alu.nFlagIsSet());
		assertTrue(alu.zFlagIsSet());
		assertEquals((short) 0, r.getReg());
	}

	@Test
	public void testInvertNFlag() {
		r.load((short) 0);
		r.load(alu.invert(r.getReg()));
		assertTrue(alu.nFlagIsSet());
		assertFalse(alu.zFlagIsSet());
		assertEquals((short) -1, r.getReg());
	}
}
