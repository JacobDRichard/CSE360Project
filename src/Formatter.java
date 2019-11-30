import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

class Formatter {
	private File importFile;
	private int hasError;
	private String errorsReported;
	private FormattingScheme currentFormattingScheme;

	Formatter(File importFile) {
		this.importFile = importFile;
		hasError = 0;
		errorsReported = "";
		currentFormattingScheme = new FormattingScheme();
	}

	int getError() { return hasError; }

	String parse(JEditorPane importedText, JEditorPane formattedText) {
		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(importFile));
			
			int lineCount = 0;
			String line;
			String formattedLine;
			String command;
			while ((line = bufferedReader.readLine()) != null) {
				lineCount++;
				command = "";
				formattedLine = "";
				if (line.length() >= 1) {
					if (line.substring(0, 1).compareTo("-") == 0) {
						command = line;
						String result = parseCommand(command, lineCount);
						if (result.equals("Title")) {
							//Not yet implemented
						} else {
							formattedLine = result;
						}
					} else {
						formattedLine = applyFormatting(line);
					}
				}

				// Putting the text imported line by line into the JEditorPane
				line += "\r"; //add new line
				importedText.setText(importedText.getText() + line);
				formattedText.setText(formattedText.getText() + formattedLine);
				//currently just copies everything from importedtext and writes it onto formattedText
			}

			// This removes the last \r\n we added, 3 since it also adds a blank space
			importedText.getDocument().remove(importedText.getDocument().getLength() - 1, 3);
			formattedText.getDocument().remove(formattedText.getDocument().getLength() - 1, 3);
			
			bufferedReader.close();
		} catch (IOException | BadLocationException ignored) { }
		return errorsReported;
	}
	
	private String parseCommand(String command, int lineCount) {
		String result = "";
		if (command.length() == 1) {
			errorsReported += "Line " + lineCount + ": Invalid command\n";
		} else {
			/*
			* Here is the switch statement to read the different commands.
			* -b is an example of a command with a numerical argument.
			*/
			switch (command.substring(0, 2)) {
				case "-n":
					if (command.length() == 2) {
						errorsReported += "Line " + lineCount + ": Invalid line length.\n";
						currentFormattingScheme.setLineLength(80);
					} else {
						String numCharacters = command.substring(2, command.length());
						int numChars;
						try {
							numChars = Integer.parseInt(numCharacters);
							if (numChars < 1 || numChars > 90) {
								errorsReported += "Line " + lineCount + ": Invalid line length.\n";
								currentFormattingScheme.setLineLength(80);
							} else {
								currentFormattingScheme.setLineLength(numChars);
							}
						} catch (NumberFormatException e) {
							errorsReported += "Line " + lineCount + ": Invalid line length.\n";
							currentFormattingScheme.setLineLength(80);
						}
					}
					break;
		
				case "-r":
					if (command.length() > 2) {
						errorsReported += "Line " + lineCount + ": Invalid command\n";
						currentFormattingScheme.setJustification('l');
					} else {
						currentFormattingScheme.setJustification('r');
					}
					break;
		
				case "-l":
					if (command.length() > 2) {
						errorsReported += "Line " + lineCount + ": Invalid command\n";
					} 
					currentFormattingScheme.setJustification('l');
					break;
		
				case "-c":
					if (command.length() > 2) {
						errorsReported += "Line " + lineCount + ": Invalid command\n";
						currentFormattingScheme.setJustification('l');
					} else {
						currentFormattingScheme.setJustification('c');
					}
					break;
		
				case "-e":
					if (command.length() > 2) {
						errorsReported += "Line " + lineCount + ": Invalid command\n";
						currentFormattingScheme.setJustification('l');
					} else {
						currentFormattingScheme.setJustification('e');
					}
					break;
		
				case "-w":
					if (command.length() != 3) {
						errorsReported += "Line " + lineCount + ": Invalid command\n";
						currentFormattingScheme.setWrapping(0);
					} else {
						if (command.charAt(2) == '+' ) {
							currentFormattingScheme.setWrapping(1);
						} else if (command.charAt(2) == '-') {
							currentFormattingScheme.setWrapping(0);
						} else {
							errorsReported += "Line " + lineCount + ": Invalid command\n";
							currentFormattingScheme.setWrapping(0);
						}
					}
					break;
		
				case "-s":
					if (command.length() > 2) {
						errorsReported += "Line " + lineCount + ": Invalid command\n";
					}
					currentFormattingScheme.setSpacing(1);
					break;
		
				case "-d":
					if (command.length() > 2) {
						errorsReported += "Line " + lineCount + ": Invalid command\n";
						currentFormattingScheme.setSpacing(1);
					} else {
						currentFormattingScheme.setSpacing(2);
					}
					break;
		
				case "-t":
					if (command.length() > 2) {
						errorsReported += "Line " + lineCount + ": Invalid command\n";
					} else {
						result = "Title";
					}
					break;
		
				case "-p":
					if (command.length() == 2) {
						errorsReported += "Line " + lineCount + ": Invalid number of spaces to indent paragraph by.\n";
						currentFormattingScheme.setIndentation(0);
					} else {
						String numberSpaces = command.substring(2, command.length());
						int numSpaces;
						try {
							numSpaces = Integer.parseInt(numberSpaces);
							if (numSpaces < 0 || numSpaces >= currentFormattingScheme.getLineLength()) {
								errorsReported += "Line " + lineCount + ": Invalid number of spaces to indent paragraph by.\n";
								currentFormattingScheme.setIndentation(0);
							} else {
								currentFormattingScheme.setIndentation(numSpaces);
							}
						} catch (NumberFormatException e) {
							errorsReported += "Line " + lineCount + ": Invalid number of spaces to indent paragraph by.\n";
							currentFormattingScheme.setIndentation(0);
						}
					}
					break;
		
				case "-b":
					if (command.length() == 2) {
						errorsReported += "Line " + lineCount + ": Invalid number of blank lines.\n";
					} else {
						String numberLines = command.substring(2, command.length());
						int numLines;
						try {
							numLines = Integer.parseInt(numberLines);
							if (numLines < 0) {
								errorsReported += "Line " + lineCount + ": Invalid number of blank lines.\n";
							} else {
								for (int count = 0; count < numLines; count++) {
									result += "\n";
								}
							}
						} catch (NumberFormatException e) {
							errorsReported += "Line " + lineCount + ": Invalid number of blank lines.\n";
						}
					}
					break;
		
				case "-a":
					if (command.length() != 3) {
						errorsReported += "Line " + lineCount + ": Invalid number of columns.\n";
						currentFormattingScheme.setNumColumns(1);
					} else {
						if (command.charAt(2) != '1' || command.charAt(2) != '2') {
							errorsReported += "Line " + lineCount + ": Invalid number of columns.\n";
							currentFormattingScheme.setNumColumns(1);
						} else {
							currentFormattingScheme.setNumColumns(Integer.parseInt(command.substring(2, 3)));
						}
					}
					break;
		
				default:
					errorsReported += "Line " + lineCount + ": Invalid command\n";
					break;
			}
		}
		return result;
	}
	
	private String applyFormatting(String line) {
		String result = "";
		
		return result;
	}
	
	class FormattingScheme {
		//num char per line
		private int lineLength;
		
		//r = right, l = left, c = center, e = equal
		private char justification;
		
		//0 = off, 1 = on
		private int wrapping;
		
		//1 = single, 2 = double
		private int spacing;
		
		//number of columns
		private int numColumns;
		
		//num spaces to indent by
		private int indentation;
		
		//Initialize with defaults
		FormattingScheme() {
			lineLength = 80;
			justification = 'l';
			wrapping = 0;
			spacing = 1;
			numColumns = 1;
			indentation = 0;
		}
		
		int getLineLength() {
			return lineLength;
		}
		
		void setLineLength(int numChar) {
			this.lineLength = numChar;
		}
		
		char getJustification() {
			return justification;
		}
		
		void setJustification(char justification) {
			this.justification = justification;
		}
		
		int getWrapping() {
			return wrapping;
		}
		
		void setWrapping(int wrapping) {
			this.wrapping = wrapping;
		}
		
		int getSpacing() {
			return spacing;
		}
		
		void setSpacing(int spacing) {
			this.spacing = spacing;
		}
		
		int getNumColumns() {
			return numColumns;
		}
		
		void setNumColumns(int numColumns) {
			this.numColumns = numColumns;
		}
		
		int getIndentation() {
			return indentation;
		}
		
		void setIndentation(int numSpaces) {
			indentation = numSpaces;
		}
		
		void reset() {
			lineLength = 80;
			justification = 'l';
			wrapping = 0;
			spacing = 1;
			numColumns = 1;
			indentation = 0;
		}
	}
}