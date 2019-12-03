class Format {
	private int lineLength;
	private char justification;
	private boolean wrapping;
	private int spacing;
	private int indentation;

	Format() {
		lineLength = 80;
		justification = 'l';
		wrapping = false;
		spacing = 1;
		indentation = 0;
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
	 * Resets the format to the default settings
	 */
	void reset() {
		lineLength = 80;
		justification = 'l';
		wrapping = false;
		spacing = 1;
		indentation = 0;
	}
}