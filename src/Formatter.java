import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

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
			//Read input file line by line and send text to importedText pane.
			BufferedReader bufferedReaderInitial = new BufferedReader(new FileReader(importFile));
			String lineforDisplay;
			while ((lineforDisplay = bufferedReaderInitial.readLine()) != null) {
				// Putting the text imported line by line into the JEditorPane
				lineforDisplay += "\r"; //add new line
				importedText.setText(importedText.getText() + lineforDisplay);
			}
			bufferedReaderInitial.close();
			
			BufferedReader bufferedReader = new BufferedReader(new FileReader(importFile));
			String line;
			String formattedLine;
			int lineCount = 0;
			boolean flag = false;
			int numCol = 1;
			while (!flag && (line = bufferedReader.readLine()) != null) {
				lineCount++;
				if (line.length() >= 1) {
					if (line.compareTo("-a2") == 0) {
						numCol = 2;
					} else if (line.compareTo("-a1") == 0) {
						numCol = 1;
					} else if (line.length() > 3 && (line.substring(0, 3).compareTo("-a2") == 0 
							|| line.substring(0, 3).compareTo("-a1") == 0)) {
						errorsReported += "Line " + lineCount + ": Invalid number of columns.\n";
						numCol = 1;
					}
					if (numCol == 2) {
						String doubleColumnSection;
						if (line.compareTo("-a2") == 0) {
							doubleColumnSection = "";
						} else {
							doubleColumnSection = line + "\n";
						}
						int startLineCount = lineCount;
						while ((line = bufferedReader.readLine()) != null && line.compareTo("-a1") != 0 
								&& line.compareTo("-t") != 0 && !(line.length() > 3 && line.substring(0, 3).compareTo("-a1") == 0)
								&& !(line.length() > 3 && line.substring(0, 3).compareTo("-a2") == 0)) {
							lineCount++;
							if (line.length() != 0) {
								if (line.compareTo("-a2") != 0) {
									doubleColumnSection += line + "\n";
								}
							}
						}
						formattedLine = applyDoubleColumnFormatting(doubleColumnSection, startLineCount + 1);
						formattedText.setText(formattedText.getText() + formattedLine);
						if (line == null) {
							flag = true;
						} else if (line.compareTo("-t") == 0) {
							line = bufferedReader.readLine();
							lineCount++;
							if (line == null) {
								flag = true;
							} else {
								int titleLength = line.length();
								if (titleLength > 90) {
									errorsReported += "Line " + lineCount + ": Title exceeds maximum number of characters.\n";
								} else {
									String underline = "";
									for (int iterator = 0; iterator < titleLength; iterator++) {
										underline += "-";
									}
									int numSpacesToCenter = 90 - titleLength;
									int numSpacesToBegin = numSpacesToCenter/2;
									int numSpacesToEnd = numSpacesToCenter - numSpacesToBegin;
									for (int iterator = 0; iterator < numSpacesToBegin; iterator++) {
										line = " " + line;
										underline = " " + underline;
									}
									for (int iterator = 0; iterator < numSpacesToEnd; iterator++) {
										line = line + " ";
										underline = underline + " ";
									}
									formattedLine = line;
									formattedText.setText(formattedText.getText() + formattedLine);
									formattedText.setText(formattedText.getText() + underline);	
									currentFormattingScheme.reset();
									numCol = 1;
								}
							}
						} else if (line.length() > 3 && (line.substring(0, 3).compareTo("-a2") == 0 || 
								line.substring(0, 3).compareTo("-a1") == 0)) {
							errorsReported += "Line " + lineCount + ": Invalid number of columns.\n";
							numCol = 1;
						} else if (line.compareTo("-a1") == 0) {
							numCol = 1;
						}
					} else if (numCol == 1) {
						String singleColumnSection = "";
						if (line.compareTo("-a1") == 0) {
							singleColumnSection = "";
						} else {
							singleColumnSection = line + "\n";
						}
						int startLineCount = lineCount;
						while ((line = bufferedReader.readLine()) != null && line.compareTo("-a2") != 0) {
							lineCount++;
							if (line.length() != 0 && line.compareTo("-a1") != 0) {
								if (line.length() > 3 && (line.substring(0, 3).compareTo("-a2") == 0 ||
										line.substring(0, 3).compareTo("-a1") == 0)) {
									errorsReported += "Line " + lineCount + ": Invalid number of columns.\n";
								} else {
									singleColumnSection += line + "\n";
								}
							}		
						}
						formattedLine = applySingleColumnFormatting(singleColumnSection, startLineCount + 1);
						formattedText.setText(formattedText.getText() + formattedLine);
						if (line == null) {
							flag = true;
						} else if (line.compareTo("-a2") == 0) {
							numCol = 2;
						}
					}
				}
			}
			// This removes the last \r\n we added, 3 since it also adds a blank space
			importedText.getDocument().remove(importedText.getDocument().getLength() - 1, 3);
			formattedText.getDocument().remove(formattedText.getDocument().getLength() - 1, 3);
			
			bufferedReader.close();
		} catch (IOException | BadLocationException ignored) { }
		return errorsReported;
	}
	
	private String applySingleColumnFormatting(String line, int lineCount) {
		String result = "";
		Scanner section = new Scanner(line);
		String currentLine;
		int currentLineCount = lineCount - 1;
		
		while (section.hasNextLine() == true) {
			currentLine = section.nextLine();
			currentLineCount++;
			if (currentLine.substring(0, 1).compareTo("-") == 0) {
				String command = currentLine;
				String returnedValue = parseCommand(command, lineCount);
				if (returnedValue.equals("Title") && section.hasNextLine() == true) {
					String title = section.nextLine();
					currentLineCount++;
					int titleLength = title.length();
					if (titleLength > 90) {
						errorsReported += "Line " + lineCount + ": Title exceeds maximum number of characters.\n";
					} else {
						String underline = "";
						for (int iterator = 0; iterator < titleLength; iterator++) {
							underline += "-";
						}
						int numSpacesToCenter = 90 - titleLength;
						int numSpacesToBegin = numSpacesToCenter/2;
						int numSpacesToEnd = numSpacesToCenter - numSpacesToBegin;
						for (int iterator = 0; iterator < numSpacesToBegin; iterator++) {
							title = " " + title;
							underline = " " + underline;
						}
						for (int iterator = 0; iterator < numSpacesToEnd; iterator++) {
							title = title + " ";
							underline = underline + " ";
						}
						result = result + title + "\n";
						result = result + underline + "\n";
						currentFormattingScheme.reset();
					}
				} else {
					result += returnedValue;
				}
			} else {
				int desiredLineLength = currentFormattingScheme.getLineLength();
				char desiredJustification = currentFormattingScheme.getJustification();
				int desiredWrapping = currentFormattingScheme.getWrapping();
				int desiredSpacing = currentFormattingScheme.getSpacing();
				int desiredIndentation = currentFormattingScheme.getIndentation();
				String nextLine = "";
				currentLine += "\n";
				
				if(desiredIndentation != 0) {
					for (int iterator = 0; iterator < desiredIndentation; iterator++) {
						currentLine  = " " + currentLine;
					}
					currentFormattingScheme.setIndentation(0);
				}
				
				if (desiredWrapping == 1) {
					currentLine = currentLine.substring(0, currentLine.length() - 1);
					boolean flag = false;
					while (section.hasNextLine() && !flag) {
						nextLine = section.nextLine();
						currentLineCount++;
						if (nextLine.substring(0, 1).compareTo("-") == 0) {
							nextLine = nextLine.substring(0, nextLine.length() - 1);
							flag = true;
						} else {
							currentLine = currentLine + " " + nextLine;
						}
					}
					currentLine += "\n";
				}

				int numCharsInLine = currentLine.length() - 1;
				
				if (numCharsInLine > desiredLineLength) {
					String tempLine = "";
					while (currentLine.length() > desiredLineLength) {
						int startIndex = 0;
						int endIndex = desiredLineLength - 1;
						while (currentLine.charAt(endIndex) != ' ') {
							endIndex--;
						}
						tempLine += currentLine.substring(startIndex, endIndex + 1);
						tempLine += "\n";
						currentLine = currentLine.substring(endIndex + 1);
					}
					tempLine += currentLine;
					currentLine = tempLine;
				}
				
				if (desiredJustification == 'r') {
					int startIndex = 0;
					while (startIndex < currentLine.length()) {
						int stopIndex = currentLine.indexOf('\n', startIndex);
						String singleLine = currentLine.substring(startIndex, stopIndex);
						int numSpacesToAdd = desiredLineLength - singleLine.length();
						if (startIndex == 0) {
							for (int iterator = 0; iterator < numSpacesToAdd; iterator++) {
								currentLine = " " + currentLine;
							}
						} else {
							String firstPart = currentLine.substring(0, startIndex);
							String secondPart = currentLine.substring(startIndex);
							String totalSpace = "";
							for (int iterator = 0; iterator < numSpacesToAdd; iterator++) {
								totalSpace += " ";
							}
							currentLine = firstPart + totalSpace + secondPart;
						}
						while (currentLine.charAt(startIndex) != '\n') {
							startIndex++;
						}
						startIndex = startIndex + 1;
					}
				} else if (desiredJustification == 'c') {
					int startIndex = 0;
					while (startIndex < currentLine.length()) {
						int stopIndex = currentLine.indexOf('\n', startIndex);
						String singleLine = currentLine.substring(startIndex, stopIndex);
						int numSpacesToAdd = desiredLineLength - singleLine.length();
						int numSpacesToAddBegin = numSpacesToAdd/2;
						int numSpacesToAddEnd = numSpacesToAdd - numSpacesToAddBegin;
						if (startIndex == 0) {
							for (int iterator = 0; iterator < numSpacesToAddBegin; iterator++) {
								currentLine = " " + currentLine;
								startIndex++;
								stopIndex++;
							}
							String firstPart = currentLine.substring(0, stopIndex);
							String lastPart = currentLine.substring(stopIndex);
							String totalSpace = "";
							for (int iterator = 0; iterator < numSpacesToAddEnd; iterator++) {
								totalSpace += " ";
								stopIndex++;
							}
							currentLine = firstPart + totalSpace + lastPart;
						} else {
							String firstPart = currentLine.substring(0, startIndex);
							String middlePart = currentLine.substring(startIndex, stopIndex);
							String lastPart = currentLine.substring(stopIndex);
							String totalBeginSpace = "";
							String totalEndSpace = "";
							for (int iterator = 0; iterator < numSpacesToAddBegin; iterator++) {
								totalBeginSpace += " ";
								startIndex++;
								stopIndex++;
							}
							for (int iterator = 0; iterator < numSpacesToAddEnd; iterator++) {
								totalEndSpace += " ";
								stopIndex++;
							}
							currentLine = firstPart + totalBeginSpace + middlePart + totalEndSpace + lastPart;
						}
						startIndex = stopIndex + 1;
					}
				} else if (desiredJustification == 'e') {
					int startIndex = 0 + desiredIndentation;
					while (startIndex < currentLine.length()) {
						int stopIndex = currentLine.indexOf('\n', startIndex);
						String singleLine;
						if (startIndex == 0 + desiredIndentation) {
							singleLine = currentLine.substring(0, stopIndex);
						} else {
							singleLine = currentLine.substring(startIndex, stopIndex);
						}
						int numSpacesToAdd = desiredLineLength - singleLine.length();
						int numSpacesAdded = 0;
						int firstInstanceOfSpace = currentLine.indexOf(' ', startIndex);
						int iterator = firstInstanceOfSpace;
						while (numSpacesAdded != numSpacesToAdd) {
							String firstPart = currentLine.substring(0, iterator);
							String secondPart = currentLine.substring(iterator);
							currentLine = firstPart + " " + secondPart;
							stopIndex++;
							numSpacesAdded++;
							while (currentLine.charAt(iterator) == ' ') {
								iterator++;
							}
							iterator = currentLine.indexOf(' ', iterator);
							if (iterator >= stopIndex - 1 || iterator == -1) {
								iterator = firstInstanceOfSpace;
							}
						}
						startIndex = stopIndex + 1;
					}
				}
				
				if (desiredSpacing == 2) {
					int newLineIndex = 0;
					while (newLineIndex < currentLine.length() - 2) {
						newLineIndex = currentLine.indexOf('\n', newLineIndex + 2);
						String firstHalf = currentLine.substring(0, newLineIndex);
						String secondHalf = currentLine.substring(newLineIndex);
						currentLine = firstHalf + "\n" + secondHalf;
					}
				}
				
				result += currentLine;
				
				if (nextLine.length() != 0) {
					String returnedValue = parseCommand(nextLine, lineCount);
					if (returnedValue.equals("Title") && section.hasNextLine() == true) {
						String title = section.nextLine();
						currentLineCount++;
						int titleLength = title.length();
						if (titleLength > 90) {
							errorsReported += "Line " + lineCount + ": Title exceeds maximum number of characters.\n";
						} else {
							String underline = "";
							for (int iterator = 0; iterator < titleLength; iterator++) {
								underline += "-";
							}
							int numSpacesToCenter = 90 - titleLength;
							int numSpacesToBegin = numSpacesToCenter/2;
							int numSpacesToEnd = numSpacesToCenter - numSpacesToBegin;
							for (int iterator = 0; iterator < numSpacesToBegin; iterator++) {
								title = " " + title;
								underline = " " + underline;
							}
							for (int iterator = 0; iterator < numSpacesToEnd; iterator++) {
								title = title + " ";
								underline = underline + " ";
							}
							result = result + title + "\n";
							result = result + underline + "\n";
							currentFormattingScheme.reset();
						}
					}
				}	
			}
		}
		section.close();
		return result;
	}
	
	private String applyDoubleColumnFormatting(String section, int lineCount) {
		return "";
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
		
				default:
					errorsReported += "Line " + lineCount + ": Invalid command\n";
					break;
			}
		}
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
		
		//num spaces to indent by
		private int indentation;
		
		//Initialize with defaults
		FormattingScheme() {
			lineLength = 80;
			justification = 'l';
			wrapping = 0;
			spacing = 1;
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
			indentation = 0;
		}
	}
}