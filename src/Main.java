import static javax.swing.ScrollPaneConstants.*;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class Main {
	private static File importFile;

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
						return true;
					else
						return f.getName().toLowerCase().endsWith(".txt");
				}
			});

			if(jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				importFile = jfc.getSelectedFile();

				// TODO: This is where parsing and formatting happens, after import is successful
				importedText.setText(jfc.getSelectedFile().toString());
			}
		});
		JButton exportButton = new JButton("Export");
		exportButton.addActionListener(e -> {
			// TODO: Do the export stuff, don't need a file chooser, just use importFile, add -formatted after name, before file extension.
			// importFile is the FULL path to the file from the file chooser

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