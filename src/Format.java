class Format {
	private int lineLength;
	private char justification;
	private boolean wrapping;
	private int spacing;
	private int indentation;
	private int blankLines;
	private int columns;

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