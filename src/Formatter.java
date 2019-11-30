import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

class Formatter {
	private File importFile;
	private int hasError;

	Formatter(File importFile) {
		this.importFile = importFile;
		hasError = 0;

	}

	int getError() { return hasError; }

	void parse(JEditorPane importedText, JEditorPane formattedText) {
		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(importFile));

			String line;
			String formattedLine;
			String command;
			while ((line = bufferedReader.readLine()) != null) {
				formattedLine = line;
				command = "0000";
				if (formattedLine.length() >= 1) {
					if (formattedLine.substring(0, 1).compareTo("-") == 0) {
						if (formattedLine.length() < 3)
							command = formattedLine.substring(0, 2);
						else if (formattedLine.length() >= 3)
							command = formattedLine.substring(0, formattedLine.length());
					}
				}

				/*
					Here is the switch statement to read the different commands.
					-b is an example of a command with a numerical argument.
				*/
				switch (command.substring(0, 2)) {
					case "-n":
						System.out.println("-n");
						break;

					case "-r":

						break;

					case "-l":

						break;

					case "-c":

						break;

					case "-e":

						break;

					case "-w":

						break;

					case "-s":

						break;

					case "-d":

						break;

					case "-t":

						break;

					case "-p":

						break;

					case "-b":

						System.out.println("-b" + command.substring(2, command.length()));

						break;

					case "-a":

						break;

					default:

						break;
				}


				// Putting the text imported line by line into the JEditorPane
				line += "\r"; //add new line
				formattedLine = line;
				importedText.setText(importedText.getText() + line);
				formattedText.setText(formattedText.getText() + formattedLine);
				//currently just copies everything from importedtext and writes it onto formattedText
			}

			// This removes the last \r\n we added, 3 since it also adds a blank space
			importedText.getDocument().remove(importedText.getDocument().getLength() - 1, 3);
			formattedText.getDocument().remove(formattedText.getDocument().getLength() - 1, 3);
		} catch (IOException | BadLocationException ignored) { }
	}
}