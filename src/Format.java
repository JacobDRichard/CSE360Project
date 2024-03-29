/**
 * @author Yu Fu, Amodini Pathak, Jacob Richard, Connor Wardell
 * @version 1.0, 3 Dec. 2019
 * Arizona State University
 * CSE 360: Intro to Software Engineering (85141)
 * Team Project
 *
 * Format.java outlines the formatting rules that are applied
 * to the imported text file.
 */
class Format {
	
	/**
	 * Integer defining the number of characters per line
	 */
	private int lineLength;
	
	/**
	 * Character defining justification.
	 * 'r' = right justify
	 * 'l' = left justify
	 * 'c' = center justify
	 * 'e' = equal justify
	 */
	private char justification;
	
	/**
	 * boolean defining wrapping. False is off and true is on.
	 */
	private boolean wrapping;
	
	/**
	 * Integer defining line spacing. 1 is single spacing, 2 is double.
	 */
	private int spacing;
	
	/**
	 * Integer defining the number of spaces to indent by.
	 */
	private int indentation;
	
	/**
	 * Integer defining the number of blank lines to print.
	 */
	private int blankLines;
	
	/**
	 * Integer defining the number of columns.
	 */
	private int columns;

	/**
	 * Default constructor sets formatting attributes to their
	 * default values.
	 */
	Format() {
		lineLength = 80;
		justification = 'l';
		wrapping = false;
		spacing = 1;
		indentation = 0;
		blankLines = 0;
		columns = 1;
	}

	/**
	 * Returns the format's current line length
	 *
	 * @return  Line length
	 */
	int getLineLength() {
		return lineLength;
	}

	/**
	 * Replaces the line length setting of the format
	 *
	 * @param   numChar Line length in characters
	 */
	void setLineLength(int numChar) {
		this.lineLength = numChar;
	}

	/**
	 * Returns the format's current justification
	 *
	 * @return  Justification
	 */
	char getJustification() {
		return justification;
	}

	/**
	 * Replaces the justification setting of the format
	 *
	 * @param   justification Character representation of justification
	 */
	void setJustification(char justification) {
		this.justification = justification;
	}

	/**
	 * Returns the format's current wrapping
	 *
	 * @return  Wrapping boolean
	 */
	boolean getWrapping() {
		return wrapping;
	}

	/**
	 * Replaces the wrapping setting of the format
	 *
	 * @param   wrapping    Boolean choice for wrapping
	 */
	void setWrapping(boolean wrapping) {
		this.wrapping = wrapping;
	}

	/**
	 * Returns the format's current spacing
	 *
	 * @return  Spacing
	 */
	int getSpacing() {
		return spacing;
	}

	/**
	 * Replaces the spacing setting of the format
	 *
	 * @param   spacing Spacing
	 */
	void setSpacing(int spacing) {
		this.spacing = spacing;
	}

	/**
	 * Returns the format's current indentation
	 *
	 * @return  Indentation
	 */
	int getIndentation() {
		return indentation;
	}

	/**
	 * Replaces the indentation setting of the format
	 *
	 * @param   numSpaces   Indentation
	 */
	void setIndentation(int numSpaces) {
		indentation = numSpaces;
	}

	/**
	 * Replaces the blank lines setting of the format
	 *
	 * @param   numLines   Blank lines
	 */
	void setBlankLines(int numLines) {
		blankLines = numLines;
	}

	/**
	 * Replaces the columns setting of the format
	 *
	 * @param   columns   Columns
	 */
	void setColumns(int columns) {
		this.columns = columns;
	}

	/**
	 * Returns a string representation of the format
	 *
	 * @return  Representation of format in a string
	 */
	public String toString() {
		String strJustification = "";
		if(justification == 'l')
			strJustification = "Left";
		else if(justification == 'c')
			strJustification = "Center";
		else if(justification == 'r')
			strJustification = "Right";
		else
			strJustification = "Equal";

		return "Line Length: " + lineLength + (lineLength == 80 ? " (Default)" : " (Imported)") + "\n" +
				"Justification: " + strJustification + (justification == 'l' ? " (Default)" : " (Imported)") + "\n" +
				"Wrapping: " + (wrapping ? "On" : "Off") + (!wrapping ? " (Default)" : " (Imported)") + "\n" +
				"Line Spacing: " + (spacing == 1 ? "Single" : "Double") + (spacing == 1 ? " (Default)" : " (Imported)") + "\n" +
				"Paragraph: " + indentation + (indentation == 0 ? " (Default)" : " (Imported)") + "\n" +
				"Blank Lines: " + blankLines + (blankLines == 0 ? " (Default)" : " (Imported)") + "\n" +
				"Columns: " + columns + (columns == 1 ? " (Default)" : " (Imported)");
	}

	/**
	 * Resets the format to the default settings
	 */
	void reset() {
		lineLength = 80;
		justification = 'l';
		wrapping = false;
		spacing = 1;
		indentation = 0;
		blankLines = 0;
		columns = 1;
	}
}