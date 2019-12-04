import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

/**
 * @author Yu Fu, Amodini Pathak, Jacob Richard, Connor Wardell
 * @version 1.0, 3 Dec. 2019
 * Arizona State University
 * CSE 360: Intro to Software Engineering (85141)
 * Team Project
 *
 * Formatter.java handles all parsing of commands, creation of a format
 * and application of that format to the imported text file.
 */
class Formatter {
	private File importFile;
	private String errorsReported;
	private Format currentFormat;

	Formatter(File importFile) {
		this.importFile = importFile;
		errorsReported = "";
		currentFormat = new Format();
	}

	/**
	 * Determines if any errors were found during import
	 *
	 * @return  Boolean if any errors were found on import
	 */
	boolean hasError() {
		return errorsReported.compareTo("") != 0;
	}

	/**
	 * Parses the imported file for commands and shows the imported text on the GUI
	 *
	 * @param   importedText    JEditorPane where imported text is placed
	 * @param   formattedText   JEditorPane where formatted text is placed
	 * @return  The errors found during import
	 */
	String parse(JEditorPane importedText, JEditorPane formattedText) {
		try {
			//Read input file line by line and send text to importedText pane.
			BufferedReader bufferedReaderInitial = new BufferedReader(new FileReader(importFile));
			String lineForDisplay;
			while ((lineForDisplay = bufferedReaderInitial.readLine()) != null) {
				// Putting the text imported line by line into the JEditorPane
				lineForDisplay += "\r"; //add new line
				importedText.setText(importedText.getText() + lineForDisplay);
			}
			bufferedReaderInitial.close();
			
			//Open new bufferedReader for the formatted text
			BufferedReader bufferedReader = new BufferedReader(new FileReader(importFile));
			String line;
			String formattedLine;
			int lineCount = 0;
			boolean flag = false;
			int numCol = 1;
			//End of file has not been reached
			while (!flag && (line = bufferedReader.readLine()) != null) {
				//To keep track of command line number for error messages
				lineCount++;

				//Skip blank lines in input text. Only blank lines added using "-b" will
				//be in the formatted text
				if (line.length() >= 1) {
					//Check if line specifies column number
					if (line.compareTo("-a2") == 0) {
						currentFormat.setColumns(2);
						numCol = 2;
					} else if (line.compareTo("-a1") == 0) {
						currentFormat.setColumns(1);
						numCol = 1;
					//If line starts with column-changing command but has other content (an invalid command)
					//issue error message and return column number to default (1)
					} else if (line.length() > 3 && (line.substring(0, 3).compareTo("-a2") == 0 
							|| line.substring(0, 3).compareTo("-a1") == 0)) {
						errorsReported += "Line " + lineCount + ": Invalid number of columns.\n";
						currentFormat.setColumns(1);
						numCol = 1;
					}
					//2 column sections are processed separately
					if (numCol == 2) {
						//Check if the line currently read is the double column command
						//Or the line after. Start the section accordingly.
						String doubleColumnSection;
						int startLineCount = lineCount;
						if (line.compareTo("-a2") == 0) {
							doubleColumnSection = "";
						} else {
							doubleColumnSection = line + "\n";
							startLineCount--;
						}
						//Keep adding next lines while there are lines, the 1 column command isn't given, the title command
						//isn't given (because it would reset columns to 1) and an invalid column command isn't given
						//(would set column to default = 1)
						while ((line = bufferedReader.readLine()) != null && line.compareTo("-a1") != 0 
								&& line.compareTo("-t") != 0 && !(line.length() > 3 && line.substring(0, 3).compareTo("-a1") == 0)
								&& !(line.length() > 3 && line.substring(0, 3).compareTo("-a2") == 0)) {
							lineCount++;
							doubleColumnSection += line + "\n";
						}
						//Once section is complete, apply formatting and send to output display
						formattedLine = applyDoubleColumnFormatting(doubleColumnSection, startLineCount);
						formattedText.setText(formattedText.getText() + formattedLine);
						//If section terminated because there are no more lines, set flag to true to terminate file loop
						if (line == null) {
							flag = true;
						//If section is terminated because of title command, add a title
						} else if (line.compareTo("-t") == 0) {
							lineCount++;
							//Set line length to 90 for title.
							
							//Read the next line which serves as the title
							line = bufferedReader.readLine();
							lineCount++;
							//Make sure that line exists (you haven't run out of lines)
							if (line == null) {
								flag = true;
							} else {
								//Ensure title is not too long. If it is, issue error and ignore title.
								int titleLength = line.length();
								if (titleLength > 90) {
									errorsReported += "Line " + lineCount + ": Title exceeds maximum number of characters.\n";
								} else {
									//Create underline of same length as title
									String underline = "";
									for (int iterator = 0; iterator < titleLength; iterator++) {
										underline += "-";
									}
									//Add equal (or almost equal) number of spaces to each side of line to center
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
									line += "\n";
									underline += "\n";
									formattedLine = line;
									//Display title and underline
									formattedText.setText(formattedText.getText() + formattedLine);
									formattedText.setText(formattedText.getText() + underline);	
									//Reset formatting scheme
									currentFormat.reset();
									numCol = 1;
								}
							}
						//If section terminated due to invalid column command, issue error and reset number
						//of columns to 1
						} else if (line.length() > 3 && (line.substring(0, 3).compareTo("-a2") == 0 || 
								line.substring(0, 3).compareTo("-a1") == 0)) {
							lineCount++;
							errorsReported += "Line " + lineCount + ": Invalid number of columns.\n";
							numCol = 1;
						//Section terminated because number of columns set to 1
						} else if (line.compareTo("-a1") == 0) {
							lineCount++;
							numCol = 1;
						}
					//Single column sections are processed separately.
					} else if (numCol == 1) {
						//Check if line currently read is single column command
						//or the line after that command. Start section accordingly.
						String singleColumnSection = "";
						int startLineCount = lineCount;
						if (line.compareTo("-a1") == 0) {
							singleColumnSection = "";
						} else {
							singleColumnSection = line + "\n";
							startLineCount--;
						}
						//Add next lines to section while there are still lines and the 2 column command isn't given
						while ((line = bufferedReader.readLine()) != null && line.compareTo("-a2") != 0) {
							lineCount++;
							singleColumnSection += line + "\n";	
						}
						//Once section is completed, apply formatting and send to output display
						formattedLine = applySingleColumnFormatting(singleColumnSection, startLineCount);
						formattedText.setText(formattedText.getText() + formattedLine);
						//If section terminated because there are no more lines, set flag to true to terminate file loop
						if (line == null) {
							flag = true;
						//Else section terminated because number of columns set to 2
						} else if (line.compareTo("-a2") == 0) {
							lineCount++;
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
		return errorsReported + "-" + currentFormat.toString();
	}

	/**
	 * Applies formatting rules where columns is set to 1
	 *
	 * @param   line    String to apply formatting
	 * @param   lineCount   Line number
	 * @return  Formatted string
	 */
	private String applySingleColumnFormatting(String line, int lineCount) {
		String result = "";
		Scanner section = new Scanner(line);
		String currentLine;
		int currentLineCount = lineCount;
		
		//Loop through the section using a Scanner object
		while (section.hasNextLine() == true) {
			currentLine = section.nextLine();
			currentLineCount++;
			//Disregard blank input lines and duplicate 1 column commands
			if (currentLine.length() != 0 && currentLine.compareTo("-a1") != 0) {
				//If line is an invalid column command, issue error
				if (currentLine.length() > 3 && (currentLine.substring(0, 3).compareTo("-a2") == 0 ||
						currentLine.substring(0, 3).compareTo("-a1") == 0)) {
					errorsReported += "Line " + currentLineCount + ": Invalid number of columns.\n";
				} else {
					//Check if the line is a command
					if (currentLine.substring(0, 1).compareTo("-") == 0) {
						String command = currentLine;
						//Parse the command
						String returnedValue = parseCommand(command, currentLineCount);
						//Check if command was for title
						if (returnedValue.equals("Title") && section.hasNextLine() == true) {
							//Set title to next line
							String title = section.nextLine();
							currentLineCount++;
							//Ensure title is not too long. If it is, issue error and ignore title.
							int titleLength = title.length();
							if (titleLength > 90) {
								errorsReported += "Line " + currentLineCount + ": Title exceeds maximum number of characters.\n";
							} else {
								//Create underline the same length as the title
								String underline = "";
								for (int iterator = 0; iterator < titleLength; iterator++) {
									underline += "-";
								}
								//Center title and underline
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
								//Add title and underline to result
								result = result + title + "\n";
								result = result + underline + "\n";
								//Reset formatting
								currentFormat.reset();
							}
						} else {
							//Add any blank lines to result
							result += returnedValue;
						}
					} else {
						//Determine the formatting to apply to the line
						int desiredLineLength = currentFormat.getLineLength();
						char desiredJustification = currentFormat.getJustification();
						boolean desiredWrapping = currentFormat.getWrapping();
						int desiredSpacing = currentFormat.getSpacing();
						int desiredIndentation = currentFormat.getIndentation();
						String nextLine = "";
						currentLine += "\n";
						
						//Apply indentation if necessary
						if(desiredIndentation != 0) {
							for (int iterator = 0; iterator < desiredIndentation; iterator++) {
								currentLine  = " " + currentLine;
							}
							//reset indentation after application
							currentFormat.setIndentation(0);
						}
						
						//Add wrapping if necessary
						if (desiredWrapping) {
							//remove newline character
							currentLine = currentLine.substring(0, currentLine.length() - 1);
							boolean flag = false;
							//Loop through next lines
							while (section.hasNextLine() && !flag) {
								nextLine = section.nextLine();
								currentLineCount++;
								
								//If next line is a command, terminate wrapping section
								if(nextLine.length() > 2)
								{
									if (nextLine.substring(0, 1).compareTo("-") == 0) {
										
										flag = true;
									//If next line is not a command, add it to the wrapping section.
									//The wrapped section is essentially one long line that will be broken up
									} 
									else {
										currentLine = currentLine + " " + nextLine;
									}
								
								}

							}
							if (section.hasNextLine() == false) {
								nextLine = "";
							}
							//Add newline to end of wrapped section
							currentLine += "\n";
						}
						
						//Determine length of line
						int numCharsInLine = currentLine.length() - 1;
						
						//Break up line if necessary due to length (will probably be necessary if wrapped)
						//Do not break mid-word
						if (numCharsInLine > desiredLineLength) {
							String tempLine = "";
							while (currentLine.length() > desiredLineLength) {
								int startIndex = 0;
								//Start end-of-line iterator at desired line length
								int endIndex = desiredLineLength - 1;
								//Move iterator backwards until a space is encountered
								//to ensure it is not mid-word
								

								while (currentLine.charAt(endIndex) != ' ') {
									if (endIndex > startIndex)
									{
										endIndex--;
									}

									else
									{	
										errorsReported += "Line " + currentLineCount + ": Word exceeds maximum number of characters.\n";
										break;
									}

									
								}
								//Break up the line by adding a newline
								tempLine += currentLine.substring(startIndex, endIndex + 1);
								tempLine += "\n";
								currentLine = currentLine.substring(endIndex + 1);
								
							
							}
							tempLine += currentLine;
							currentLine = tempLine;
						}
						
						//Apply right justification if necessary
						if (desiredJustification == 'r') {
							int startIndex = 0;
							//Loop to handle multiple lines (due to newlines)
							while (startIndex < currentLine.length()) {
								int stopIndex = currentLine.indexOf('\n', startIndex);
								String singleLine = currentLine.substring(startIndex, stopIndex);
								//Determine how many spaces are needed to reach desired line length
								int numSpacesToAdd = desiredLineLength - singleLine.length();
								//Add the spaces to the beginning of the line
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
						//Apply center justification if necessary
						} else if (desiredJustification == 'c') {
							int startIndex = 0;
							//Loop to handle multiple lines (due to newlines)
							while (startIndex < currentLine.length()) {
								int stopIndex = currentLine.indexOf('\n', startIndex);
								String singleLine = currentLine.substring(startIndex, stopIndex);
								//Determine how many spaces are needed to reach desired line length
								int numSpacesToAdd = desiredLineLength - singleLine.length();
								int numSpacesToAddBegin = numSpacesToAdd/2;
								int numSpacesToAddEnd = numSpacesToAdd - numSpacesToAddBegin;
								//Add half the spaces to the beginning and half (or half + 1) to the end
								//to center
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
						//Apply equal justification if necessary
						} else if (desiredJustification == 'e') {
							//Start past the indentation since indentation would count as first instance
							//of space after start of line.
							int startIndex = 0 + desiredIndentation;
							//Loop to handle multiple lines (due to newlines)
							while (startIndex < currentLine.length()) {
								int stopIndex = currentLine.indexOf('\n', startIndex);
								String singleLine;
								if (startIndex == 0 + desiredIndentation) {
									singleLine = currentLine.substring(0, stopIndex);
								} else {
									singleLine = currentLine.substring(startIndex, stopIndex);
								}
								//Determine how many spaces are needed to reach desired line length
								int numSpacesToAdd = desiredLineLength - singleLine.length();
								int numSpacesAdded = 0;
								//Find the first instance of space after the starting point (since 
								//spacing should be added between words and not at the beginning or end)
								int firstInstanceOfSpace = currentLine.indexOf(' ', startIndex);
								int iterator = firstInstanceOfSpace;
								//Loop over line repeatedly, adding an additional space between each word
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
									//Restart iterator at first instance of space
									if (iterator >= stopIndex - 1 || iterator == -1) {
										iterator = firstInstanceOfSpace;
									}
								}
								startIndex = stopIndex + 1;
							}
						}
						
						//Apply double spacing if necessary
						if (desiredSpacing == 2) {
							int newLineIndex = 0;
							//Add a newline next to every existing newline
							while (newLineIndex < currentLine.length() - 2) {
								newLineIndex = currentLine.indexOf('\n', newLineIndex + 2);
								String firstHalf = currentLine.substring(0, newLineIndex);
								String secondHalf = currentLine.substring(newLineIndex);
								currentLine = firstHalf + "\n" + secondHalf;
							}
						}
						
						//Add formatted line to result
						result += currentLine;
						
						//If wrapping was applied, the wrapping section stopped after a command was read.
						//Parse and apply that command here if necessary
						if (nextLine.length() != 0) {
							String returnedValue = parseCommand(nextLine, currentLineCount);
							if (returnedValue.equals("Title") && section.hasNextLine() == true) {
								String title = section.nextLine();
								currentLineCount++;
								int titleLength = title.length();
								if (titleLength > 90) {
									errorsReported += "Line " + currentLineCount + ": Title exceeds maximum number of characters.\n";
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
									currentFormat.reset();
								}
							} else {
								result += returnedValue;
							}
						}	
					}
				}
			}
		}
		section.close();
		return result;
	}

	/**
	 * Applies formatting rules where columns is set to 2
	 *
	 * @param   line    String to apply formatting
	 * @param   lineCount   Line number
	 * @return  Formatted string
	 */
	private String applyDoubleColumnFormatting(String line, int lineCount) {
		//Same as single column formatting except, at end, bottom half of the lines
		//are moved up to become the second column. Also, line length is pre-set to
		//35 with a 10 char gap between columns
		String result = "";
		Scanner section = new Scanner(line);
		String currentLine;
		int currentLineCount = lineCount;
		
		//Loop through the section using a Scanner object
		while (section.hasNextLine() == true) {
			currentLine = section.nextLine();
			currentLineCount++;
			//To disregard input blank lines and duplicate 2 column command
			if (currentLine.length() != 0 && currentLine.compareTo("-a2") != 0) { 
				//Check if the line is a command
				if (currentLine.substring(0, 1).compareTo("-") == 0) {
					String command = currentLine;
					//Parse the command
					String returnedValue = parseCommand(command, currentLineCount);
					//There should not be any title commands in this section
					//so returnedValue shouldn't be "Title"
					if (returnedValue.compareTo("Title") != 0) {
						result += returnedValue;
					}
				} else {
					//Determine the formatting to apply to the line
					//Line length is set to 35 by default for 2 columns, regardless of
					//requested setting
					int desiredLineLength = 35;
					char desiredJustification = currentFormat.getJustification();
					boolean desiredWrapping = currentFormat.getWrapping();
					int desiredSpacing = currentFormat.getSpacing();
					int desiredIndentation = currentFormat.getIndentation();
					String nextLine = "";
					currentLine += "\n";
					
					//Apply indentation if necessary
					if(desiredIndentation != 0) {
						for (int iterator = 0; iterator < desiredIndentation; iterator++) {
							currentLine  = " " + currentLine;
						}
						//reset indentation after application
						currentFormat.setIndentation(0);
					}
					
					//Add wrapping if necessary
					if (desiredWrapping) {
						//remove newline character
						currentLine = currentLine.substring(0, currentLine.length() - 1);
						boolean flag = false;
						//Loop through next lines
						while (section.hasNextLine() && !flag) {
							nextLine = section.nextLine();
							currentLineCount++;
							//If next line is a command, terminate wrapping section
							if (nextLine.substring(0, 1).compareTo("-") == 0) {
								flag = true;
							//If next line is not a command, add it to the wrapping section.
							//The wrapped section is essentially one long line that will be broken up
							} else {
								currentLine = currentLine + " " + nextLine;
							}
						}
						if (section.hasNextLine() == false) {
							nextLine = "";
						}
						//Add newline to end of wrapped section
						currentLine += "\n";
					}
					
					//Determine length of line
					int numCharsInLine = currentLine.length() - 1;
					
					//Break up line if necessary due to length (will probably be necessary if wrapped)
					//Do not break mid-word
					if (numCharsInLine > desiredLineLength) {
						String tempLine = "";
						while (currentLine.length() > desiredLineLength) {
							int startIndex = 0;
							//Start end-of-line iterator at desired line length
							int endIndex = desiredLineLength - 1;
							//Move iterator backwards until a space is encountered
							//to ensure it is not mid-word
							while (currentLine.charAt(endIndex) != ' ') {
								endIndex--;
							}
							//Break up the line by adding a newline
							tempLine += currentLine.substring(startIndex, endIndex + 1);
							tempLine += "\n";
							currentLine = currentLine.substring(endIndex + 1);
						}
						tempLine += currentLine;
						currentLine = tempLine;
					}
					
					//Apply right justification if necessary
					if (desiredJustification == 'r') {
						int startIndex = 0;
						//Loop to handle multiple lines (due to newlines)
						while (startIndex < currentLine.length()) {
							int stopIndex = currentLine.indexOf('\n', startIndex);
							String singleLine = currentLine.substring(startIndex, stopIndex);
							//Determine how many spaces are needed to reach desired line length
							int numSpacesToAdd = desiredLineLength - singleLine.length();
							//Add the spaces to the beginning of the line
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
					//Apply center justification if necessary
					} else if (desiredJustification == 'c') {
						int startIndex = 0;
						//Loop to handle multiple lines (due to newlines)
						while (startIndex < currentLine.length()) {
							int stopIndex = currentLine.indexOf('\n', startIndex);
							String singleLine = currentLine.substring(startIndex, stopIndex);
							//Determine how many spaces are needed to reach desired line length
							int numSpacesToAdd = desiredLineLength - singleLine.length();
							int numSpacesToAddBegin = numSpacesToAdd/2;
							int numSpacesToAddEnd = numSpacesToAdd - numSpacesToAddBegin;
							//Add half the spaces to the beginning and half (or half + 1) to the end
							//to center
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
					//Apply equal justification if necessary
					} else if (desiredJustification == 'e') {
						//Start past the indentation since indentation would count as first instance
						//of space after start of line.
						int startIndex = 0 + desiredIndentation;
						//Loop to handle multiple lines (due to newlines)
						while (startIndex < currentLine.length()) {
							int stopIndex = currentLine.indexOf('\n', startIndex);
							String singleLine;
							if (startIndex == 0 + desiredIndentation) {
								singleLine = currentLine.substring(0, stopIndex);
							} else {
								singleLine = currentLine.substring(startIndex, stopIndex);
							}
							//Determine how many spaces are needed to reach desired line length
							int numSpacesToAdd = desiredLineLength - singleLine.length();
							int numSpacesAdded = 0;
							//Find the first instance of space after the starting point (since 
							//spacing should be added between words and not at the beginning or end)
							int firstInstanceOfSpace = currentLine.indexOf(' ', startIndex);
							int iterator = firstInstanceOfSpace;
							//Loop over line repeatedly, adding an additional space between each word
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
								//Restart iterator at first instance of space
								if (iterator >= stopIndex - 1 || iterator == -1) {
									iterator = firstInstanceOfSpace;
								}
							}
							startIndex = stopIndex + 1;
						}
					}
					
					//Apply double spacing if necessary
					if (desiredSpacing == 2) {
						int newLineIndex = 0;
						//Add a newline next to every existing newline
						while (newLineIndex < currentLine.length() - 2) {
							newLineIndex = currentLine.indexOf('\n', newLineIndex + 2);
							String firstHalf = currentLine.substring(0, newLineIndex);
							String secondHalf = currentLine.substring(newLineIndex);
							currentLine = firstHalf + "\n" + secondHalf;
						}
					}
					
					//Add formatted line to result
					result += currentLine;
					
					//If wrapping was applied, the wrapping section stopped after a command was read.
					//Parse and apply that command here if necessary
					if (nextLine.length() != 0) {
						String returnedValue = parseCommand(nextLine, currentLineCount);
						//There should be no title commands in this section so returnedValue shouldn't
						//be "Title"
						result += returnedValue;
					}	
				}
			}
		}
		section.close();
		
		//Count number of lines in section
		int numLines = 0;
		int iterator = 0;
		while (iterator < result.length() - 1 || iterator == -1) {
			numLines++;
			iterator = result.indexOf('\n', iterator + 1);
		}
		//Split number of lines in 2 for the 2 columns.
		//If split is uneven, larger number goes in column 1
		int numLinesCol1 = 0;
		if (numLines % 2 == 0) {
			numLinesCol1 = numLines/2;
		} else {
			numLinesCol1 = numLines/2 + 1;
		}
		int numLinesCol2 = numLines - numLinesCol1;
		//Separate the two halves of lines
		String col1 = "";
		String col2 = "";
		Scanner scanCol = new Scanner(result);
		for (int loop = 0; loop < numLinesCol1; loop++) {
			String readString = scanCol.nextLine();
			while (readString.length() != 35) {
				readString += " ";
			}
			col1 = col1 + readString + "\n";
		}
		for (int loop = 0; loop < numLinesCol2; loop++) {
			String readString = scanCol.nextLine();
			while (readString.length() != 35) {
				readString += " ";
			}
			col2 = col2 + readString + "\n";
		}
		scanCol.close();
		
		//Create final String by adding one line from column 1,
		//then adding 10 spaces, then adding one line from column 2, 
		//followed by a newLine, for each line.
		String finalColumnString = "";
		Scanner scanCol1 = new Scanner(col1);
		Scanner scanCol2 = new Scanner(col2);
		while (scanCol2.hasNextLine()) {
			finalColumnString += scanCol1.nextLine();
			finalColumnString += "          ";
			finalColumnString += scanCol2.nextLine();
			finalColumnString += "\n";
		}
		//If total number of lines is odd, column 1 will have an extra line.
		//Need to add it to the end on its own line.
		if (numLines % 2 != 0) {
			finalColumnString += scanCol1.nextLine();
		}
		scanCol1.close();
		scanCol2.close();
		return finalColumnString;
	}

	/**
	 * Parses the string for commands and changes the format attribute if necessary
	 *
	 * @param   command    Command string to be parsed
	 * @param   lineCount   Line number
	 * @return  Result of command parsing
	 */
	private String parseCommand(String command, int lineCount) {
		//Returns a string that is either empty (if command was invalid or resulted
		//in a change of currentFormat attributes), the word "Title" to
		//indicate that the next line is a title, or however many blank lines were
		//requested if the command specified blank lines.
		String result = "";
		//Command that is just a "-" is invalid
		if (command.length() == 1) {
			errorsReported += "Line " + lineCount + ": Invalid command\n";
		} else {
			//Look at the first 2 characters to determine what kind of command it is
			switch (command.substring(0, 2)) {
				//Change line length
				case "-n":
					//Invalid line length command because no number specified. Set to default
					if (command.length() == 2) {
						errorsReported += "Line " + lineCount + ": Invalid line length.\n";
						currentFormat.setLineLength(80);
					} else {
						//Look at what comes after "-n"
						String numCharacters = command.substring(2, command.length());
						int numChars;
						try {
							//If a number follows "-n", check if it is an int between 1 & 90, inclusive.
							//If yes, set line length to it. If not, send error and set to default.
							numChars = Integer.parseInt(numCharacters);
							if (numChars < 1 || numChars > 90) {
								errorsReported += "Line " + lineCount + ": Invalid line length.\n";
								currentFormat.setLineLength(80);
							} else {
								currentFormat.setLineLength(numChars);
							}
						//Command is invalid if "-n" is followed by any non-numeric characters
						} catch (NumberFormatException e) {
							errorsReported += "Line " + lineCount + ": Invalid line length.\n";
							currentFormat.setLineLength(80);
						}
					}
					break;
				//Right justify
				case "-r":
					//If "-r" is followed by any characters, the command is invalid.
					//Issue error and set to default
					if (command.length() > 2) {
						errorsReported += "Line " + lineCount + ": Invalid command\n";
						currentFormat.setJustification('l');
					//Else, set to right justification
					} else {
						currentFormat.setJustification('r');
					}
					break;
				//Left justify
				case "-l":
					//If "-l" is followed by any characters, the command is invalid. Issue error.
					if (command.length() > 2) {
						errorsReported += "Line " + lineCount + ": Invalid command\n";
					} 
					//Either way, set justification to left (since it is also the default)
					currentFormat.setJustification('l');
					break;
				//Center justify
				case "-c":
					//If "-c" is followed by any characters, the command is invalid.
					//Issue error and set to default
					if (command.length() > 2) {
						errorsReported += "Line " + lineCount + ": Invalid command\n";
						currentFormat.setJustification('l');
					//Else, set to center justification
					} else {
						currentFormat.setJustification('c');
					}
					break;
				//Equal justify
				case "-e":
					//If "-e" is followed by any characters, the command is invalid.
					//Issue error and set to default
					if (command.length() > 2) {
						errorsReported += "Line " + lineCount + ": Invalid command\n";
						currentFormat.setJustification('l');
					//Else, set to equal justification
					} else {
						currentFormat.setJustification('e');
					}
					break;
				//Wrapping
				case "-w":
					//Commands that are just "-w" or have unnecessary characters appended are invalid
					//Issue error and set to default
					if (command.length() != 3) {
						errorsReported += "Line " + lineCount + ": Invalid command\n";
						currentFormat.setWrapping(false);
					} else {
						//Check the 3rd character. + or - are valid. Others are not.
						if (command.charAt(2) == '+' ) {
							currentFormat.setWrapping(true);
						} else if (command.charAt(2) == '-') {
							currentFormat.setWrapping(false);
						} else {
							errorsReported += "Line " + lineCount + ": Invalid command\n";
							currentFormat.setWrapping(false);
						}
					}
					break;
				//Single spacing
				case "-s":
					//If "-s" is followed by other characters, command is invalid
					//Issue error
					if (command.length() > 2) {
						errorsReported += "Line " + lineCount + ": Invalid command\n";
					}
					//Set to single spacing either way since it is default
					currentFormat.setSpacing(1);
					break;
				//Double spacing
				case "-d":
					//If "-d" is followed by other characters, command is invalid
					//Issue error and set to default
					if (command.length() > 2) {
						errorsReported += "Line " + lineCount + ": Invalid command\n";
						currentFormat.setSpacing(1);
					//Else set to double spacing
					} else {
						currentFormat.setSpacing(2);
					}
					break;
				//Title
				case "-t":
					//If "-t" is followed by other characters, command is invalid
					//Issue error and ignore command.
					if (command.length() > 2) {
						errorsReported += "Line " + lineCount + ": Invalid command\n";
					//Else return "Title" to indicate that next line should be title
					} else {
						currentFormat.setLineLength(90);
						result = "Title";
					}
					break;
				//Paragraphing/Indentation
				case "-p":
					//"-p" by itself is an invalid command. Issue error and set to default
					if (command.length() == 2) {
						errorsReported += "Line " + lineCount + ": Invalid number of spaces to indent paragraph by.\n";
						currentFormat.setIndentation(0);
					} else {
						//Look at characters after "-p"
						String numberSpaces = command.substring(2, command.length());
						int numSpaces;
						try {
							//If a number follows "-p", check if the number is an int and valid.
							//If invalid, issue error and set to default. Else, set indentation to number.
							numSpaces = Integer.parseInt(numberSpaces);
							if (numSpaces < 0 || numSpaces >= currentFormat.getLineLength()) {
								errorsReported += "Line " + lineCount + ": Invalid number of spaces to indent paragraph by.\n";
								currentFormat.setIndentation(0);
							} else {
								currentFormat.setIndentation(numSpaces);
							}
						//If "-p" is followed by non-numeric characters, it is invalid. Issue error and set to default
						} catch (NumberFormatException e) {
							errorsReported += "Line " + lineCount + ": Invalid number of spaces to indent paragraph by.\n";
							currentFormat.setIndentation(0);
						}
					}
					break;
				//Blank lines
				case "-b":
					//If command is just "-b" it is invalid. Issue error and ignore command
					if (command.length() == 2) {
						errorsReported += "Line " + lineCount + ": Invalid number of blank lines.\n";
					} else {
						//Look at what follows "-b"
						String numberLines = command.substring(2, command.length());
						int numLines;
						try {
							//If "-b" is followed by a number, check if it is a nonnegative number
							//If the number is valid, return that number of newlines (blank lines)
							//Else issue error and ignore command
							numLines = Integer.parseInt(numberLines);
							if (numLines < 0) {
								errorsReported += "Line " + lineCount + ": Invalid number of blank lines.\n";
							} else {
								currentFormat.setBlankLines(numLines);
								for (int count = 0; count < numLines; count++) {
									result += "\n";
								}
							}
						//If "-b" is followed by nonnumeric characters, the command is invalid. Issue error
						} catch (NumberFormatException e) {
							errorsReported += "Line " + lineCount + ": Invalid number of blank lines.\n";
						}
					}
					break;
				//Anything else is invalid
				default:
					errorsReported += "Line " + lineCount + ": Invalid command\n";
					break;
			}
		}
		return result;
	}
}