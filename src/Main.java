import javax.swing.*;


/**
 * @author Yu Fu, Amodini Pathak, Jacob Richard, Connor Wardell
 * @version 1.0, 3 Dec. 2019
 * Arizona State University
 * CSE 360: Intro to Software Engineering (85141)
 * Team Project
 *
 * Main.java is the runner class for the project, creating the
 * window for the program.
 */
public class Main {
	
	/**
	 * Main method serves as the entry point into the program by launching the GUI.
	 * 
	 * @param args	String array of arguments passed to the program at launch
	 */
	public static void main(String[] args) {
		JFrame window = new GUI("Text Formatter");
		window.setVisible(true);
	}
}