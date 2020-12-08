package view;

/*
TCSS 360
Project #3
Group 6
Anna Blanton, Walter Kagel, Taehong Kim, John Earl Morton
 */

import controller.Pep8Sim;
import model.Machine;

/**
 * Runs the pep8 sim.
 * @version 12/8/2020
 * @author Group 6, Lead: Walter Kagel
 */
public class Main {

    public static void main(String[] args) {
        GUI pep8View = new GUI();
        Machine pep8Machine = new Machine(pep8View);
        Pep8Sim controller = new Pep8Sim(pep8View, pep8Machine);
    }
}
