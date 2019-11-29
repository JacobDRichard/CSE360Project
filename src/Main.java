import static javax.swing.ScrollPaneConstants.*;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import LineNumberMargin;




public class Main {
	private static File importFile;

	private static void copyFile(File source, File dest) throws IOException {
		Files.copy(source.toPath(), dest.toPath());
	}


	public static void main(String[] args) {
		// General window attributes
		JFrame frame = new JFrame("Text Formatter");
		frame.setSize(1200, 800);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);

		// Layout
		JPanel topPanel = new JPanel(new BorderLayout());
		JPanel textPanel = new JPanel(new GridLayout(1, 2));

		// Imported text that isn't formatted
		JEditorPane importedText = new JEditorPane();
		importedText.setEditable(true); // TODO: FALSE
		importedText.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
		JScrollPane importedTextScroll = new JScrollPane(importedText, VERTICAL_SCROLLBAR_ALWAYS, HORIZONTAL_SCROLLBAR_NEVER);
		importedTextScroll.getVerticalScrollBar().setLocation(importedTextScroll.getX() + 10, importedTextScroll.getY()); // This gets the scroll bar off the text...

		LineNumberMargin test = new LineNumberMargin(importedText);
		importedTextScroll.setRowHeaderView(test);

		// Preview of the formatted text on import (if no errors?)
		JEditorPane formattedText = new JEditorPane();
		formattedText.setEditable(true); // TODO: FALSE
		formattedText.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
		JScrollPane formattedTextScroll = new JScrollPane(formattedText, VERTICAL_SCROLLBAR_ALWAYS, HORIZONTAL_SCROLLBAR_NEVER);
		formattedTextScroll.getVerticalScrollBar().setLocation(formattedTextScroll.getX() + 10, formattedTextScroll.getY());

		// Buttons
		JPanel buttonPanel = new JPanel(new GridLayout(1, 2));
		JButton importButton = new JButton("Import");
		importButton.addActionListener(e -> {

			
			JFileChooser jfc = new JFileChooser(System.getProperty("user.dir"));

			jfc.setAcceptAllFileFilterUsed(false);
			jfc.addChoosableFileFilter(new FileFilter() {
				public String getDescription() {
					return "Text Files (*.txt)";
				}

				public boolean accept(File f) {
					if (f.isDirectory())
					{
						
						return true;
					}
					else
					{
						return f.getName().toLowerCase().endsWith(".txt");
					}
				}
			});

			if(jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				importFile = jfc.getSelectedFile();
				
				// Remove all text from previous import
				importedText.setText("");
				formattedText.setText("");

				// TODO: This is where parsing and formatting happens, after import is successful
				try {
					BufferedReader bufferedReader = new BufferedReader(new FileReader(importFile));

					String line;
					String formattedLine;
					String command;
					while((line = bufferedReader.readLine()) != null) {
						// TODO: Start parsing here, find command, use switch (probably), error on default?
						formattedLine = line;
						command = "0000";
						if (formattedLine.length() >= 1)
						{
							
							
							if (formattedLine.substring(0,1).compareTo("-") == 0)
							{
								if (formattedLine.length() < 3)
								{
									
									command = formattedLine.substring(0 , 2);
								}
								else if (formattedLine.length() >= 3)
								{
									
									command = formattedLine.substring(0 , formattedLine.length());
								}
							}
							
						}
						/*
							Here is the switch statement to read the different commands.
							-b is an example of a command with a numerical argument.
				
						*/
						switch (command.substring(0,2)) {
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
					importedText.getDocument().remove(importedText.getDocument().getLength() - 1, 3); //seems to delete last 3 characters? changed -3 to -1
					
				
					formattedText.getDocument().remove(formattedText.getDocument().getLength() - 1, 3);


				} catch (IOException | BadLocationException ignored) { }
			}
		});
		JButton exportButton = new JButton("Export");
		exportButton.addActionListener(e -> {
			// TODO: Do the export stuff, don't need a file chooser, just use importFile, add -formatted after name, before file extension.
			// importFile is the FULL path to the file from the file chooser
			

			try{
				System.out.println("test1");
				String newFileName = importFile.getName().substring(0, importFile.getName().length() - 4)+ "-formatted.txt";
				//creates new string with -formatted after name.

				File exportFile = new File(newFileName);

				
				BufferedWriter writer = new BufferedWriter(new FileWriter(newFileName));
				writer.write(formattedText.getText());
				writer.close();
				//writes formatted text to file
			
				
				exportFile.createNewFile();




				System.out.println(importFile.getName());
			}

			catch(IOException except)
			{

			}
			// TODO: This is the error popup, choice will be 0 for continue, 1 for cancel. Only make it show once you deem errors are found
			String[] options = {"Continue", "Cancel"};
			int choice = JOptionPane.showOptionDialog(null, "The imported file contains errors, would you like to continue exporting?",
					"Continue Exporting?", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
		});
		
		buttonPanel.add(importButton);
		buttonPanel.add(exportButton);

		textPanel.add(importedTextScroll);
		textPanel.add(formattedTextScroll);
		topPanel.add(BorderLayout.CENTER, textPanel);
		topPanel.add(BorderLayout.SOUTH, buttonPanel);

		// Console (to display errors, formatting, etc.)
		JEditorPane console = new JEditorPane();
		console.setEditable(true);
		console.setFont(new Font("monospaced", Font.PLAIN, 12));
		JScrollPane consoleScroll = new JScrollPane(console, VERTICAL_SCROLLBAR_ALWAYS, HORIZONTAL_SCROLLBAR_NEVER);
		consoleScroll.setPreferredSize(new Dimension(1200, 150));

		frame.getContentPane().add(BorderLayout.CENTER, topPanel);
		frame.getContentPane().add(BorderLayout.SOUTH, consoleScroll);

		// Make the window visible
		frame.setVisible(true);
	}
}