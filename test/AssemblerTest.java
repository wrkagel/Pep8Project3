//Changed from control to controller to match Project 3
//Changed to use jUnit 4 to match project 3

import controller.Assembler;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/*
TCSS 360
Project #3
Group 8
Walter Kagel, Taehong Kim, RJ Alabado
 */

/**
 * Class that tests the assembler.
 * @author Group 8: Lead Walter Kagel
 * @version 12/11/20
 */
public class AssemblerTest {

    private Assembler assembler = new Assembler();

    //A simple test of assembling source code.
    @Test
    public void testAssembleSourceCodeByte() {
        assembler.assembleSourceCode(".BYTE 0x10\n.end");
        assertEquals("10  ", assembler.getMachineCode());
    }

    @Test
    public void testAssembleSourceCodeUnary() {
        assembler.assembleSourceCode(("Stop\n.end"));
        assertEquals("00  ", assembler.getMachineCode());
    }

    @Test
    public void testAssembleSourceCodeNonUnary() {
        assembler.assembleSourceCode("BR 0x1000,x\n.End");
        assertEquals("05 10 00  ", assembler.getMachineCode());
    }

    @Test
    public void testGetMachineCode() {
        assembler.assembleSourceCode("ADDA 15,i\nSTOP\n.END");
        assertEquals("70 00 0F 00  ", assembler.getMachineCode());
    }

    @Test
    public void testGetErrorMessagesNoEnd() {
        assembler.assembleSourceCode("ADDA 15,i");
        assertEquals("Source code does not contain the .END sentinel.\n", assembler.getErrorMessages().get(0));
    }

    @Test
    public void testGetErrorMessagesNoAddressingMode() {
        assembler.assembleSourceCode("ADDA 10\n.END");
        assertEquals("Instruction requires an addressing mode at line 0.\n",
                assembler.getErrorMessages().get(0));
    }

    @Test
    public void testGetErrorMessagesSymbolNotFound() {
        assembler.assembleSourceCode("ADDA test,i\n.end");
        assertEquals("Symbol not found error at line 0.\n",
                assembler.getErrorMessages().get(0));
    }

    @Test
    public void testGetErrorMessagesValueTooLarge() {
        assembler.assembleSourceCode("SUBI 0xFFFFF,n\n.end");
        assertEquals("Error when translating line 0 to machine code. Value out of bounds\n",
                assembler.getErrorMessages().get(0));
    }

    @Test
    public void testGetErrorMessagesSourceLineError() {
        assembler.assembleSourceCode("ADDA 0x30,i test\n.end");
        assertEquals("Error when translating line 0 to machine code. " +
                        "No enum constant controller.AddressingMode.I TEST\n",
                assembler.getErrorMessages().get(0));
    }

    @Test
    public void testGetErrorMessagesInvalidMnemonic() {
        assembler.assembleSourceCode("ADDB 0x3008,sx\n.end");
        assertEquals("No enum constant controller.Mnemonic.ADDB at line 0.\n",
                assembler.getErrorMessages().get(0));
    }

    @Test
    public void testSymbolTable() {
        assembler.assembleSourceCode("BR main,i\nnum: .WORD 2\nmain: ADDA num,d\nSTOP\n.END");
        assertEquals("04 00 05 00 02 71 00 03 00  ", assembler.getMachineCode());
    }

    @Test
    public void testInvalidSymbolError() {
        assembler.assembleSourceCode("7Test: .BYTE 2\nstop\n.END");
        assertEquals("Invalid symbol name at line 0.\n", assembler.getErrorMessages().get(0));
    }

    @Test
    public void testRepeatedSymbolError() {
        assembler.assembleSourceCode("num: .WORD 1\nnum: .WORD 2\n.END");
        assertEquals("Symbol at line 1 already exists in symbol table.\n", assembler.getErrorMessages().get(0));
    }
}
