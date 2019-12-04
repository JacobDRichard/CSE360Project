import javax.swing.*;


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