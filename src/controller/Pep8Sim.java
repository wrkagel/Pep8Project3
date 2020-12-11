package controller;

import model.Machine;
import view.GUI;

import javax.swing.*;
import java.util.ArrayList;

/**
 * This class is designed to instantiate both the Machine and GUI and facilitate communication between them.
 * @author Group 6: Walter Kagel
 * @version 12/08/2020
 */
public class Pep8Sim {

    private final GUI pep8View;
    private final Machine pep8Machine;
    private final Assembler pep8Assembler;

    /**
     * Adds the property change listeners and instantiates the controller.
     * @param pep8View Used for access to the GUI
     * @param pep8Machine Used for access to the Machine
     */
    public Pep8Sim(GUI pep8View, Machine pep8Machine) {
        this.pep8View = pep8View;
        this.pep8Machine = pep8Machine;
        this.pep8Assembler = new Assembler();
        //Use a listener to tell when someone presses start, read in code, store it to memory, and run code.
        pep8View.addPropertyChangeListener("Start", e -> {
            startMachine();
        });
        pep8View.addPropertyChangeListener("New", e -> pep8Machine.reset());
        pep8View.addPropertyChangeListener("Assemble", e -> {
            assembleSourceCode();
        });
        pep8View.addPropertyChangeListener("Run Source", e -> {
            assembleSourceCode();
            startMachine();
        });
    }

    /**
     * Runs the code in the machine code window of the GUI on the pep8Machine.
     */
    private void startMachine() {
        String[] codeArray = pep8View.getObjectCode().split(" ");
        byte[] byteArray = new byte[codeArray.length];
        try {
            for (int i = 0; i < codeArray.length; i++) {
                byteArray[i] = (byte) (Integer.parseUnsignedInt(codeArray[i], 16));
            }
        } catch (Exception exception) {
            JOptionPane.showMessageDialog(pep8View, "Error in loading Object Code.");
            return;
        }
        pep8Machine.store((short) 0, byteArray);
        pep8Machine.run();
    }

    private void assembleSourceCode() {
        pep8Assembler.assembleSourceCode(pep8View.getSourceCode());
        ArrayList<String> errors = pep8Assembler.getErrorMessages();
        if (errors.size() > 0) {
            StringBuilder sb = new StringBuilder();
            for (String error:errors) {
                sb.append(error);
            }
            pep8View.setAsListing(sb.toString());
        } else {
            pep8View.setObjectCode(pep8Assembler.getMachineCode());
        }
    }
}
