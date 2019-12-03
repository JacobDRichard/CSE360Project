import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS;

class GUI extends JFrame implements ActionListener {
	private static final int FRAME_WIDTH = 1200;
	private static final int FRAME_HEIGHT = 800;

	private JPanel topPanel;
	private JPanel textPanel;
	private JPanel buttonPanel;

	private JEditorPane importedText;
	private JScrollPane importedTextScroll;
	private LineNumberMargin lineNumbers;

	private JEditorPane formattedText;
	private JScrollPane formattedTextScroll;

	private JEditorPane console;
	private JScrollPane consoleScroll;

	private JButton importButton;
	private JButton exportButton;

	private Formatter format;
	private File importFile;

	GUI(String title) {
		// General window properties
		setTitle(title);
		setSize(FRAME_WIDTH, FRAME_HEIGHT);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);

		// Components for the window
		createLayout();
		createTextAreas();
		createButtons();

		// Adding everything together
		finishLayout();
	}

	/**
	 * Instantiates the general layout components of the GUI
	 */
	private void createLayout() {
		topPanel = new JPanel(new BorderLayout());
		textPanel = new JPanel(new GridLayout(1, 2));
		buttonPanel = new JPanel(new GridLayout(1, 2));
	}

	/**
	 * Instantiates and configures the text areas of the GUI
	 */
	private void createTextAreas() {
		// Imported text from the input text file, shows commands, no formatting
		importedText = new JEditorPane();
		importedText.setEditable(false);
		importedText.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
		importedTextScroll = new JScrollPane(importedText, VERTICAL_SCROLLBAR_ALWAYS, HORIZONTAL_SCROLLBAR_NEVER);
		importedTextScroll.getVerticalScrollBar().setLocation(importedTextScroll.getX() + 10, importedTextScroll.getY());
		lineNumbers = new LineNumberMargin(importedText);
		importedTextScroll.setRowHeaderView(lineNumbers);

		// Preview of the formatted text with commands removed
		formattedText = new JEditorPane();
		formattedText.setEditable(false);
		formattedText.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
		formattedTextScroll = new JScrollPane(formattedText, VERTICAL_SCROLLBAR_ALWAYS, HORIZONTAL_SCROLLBAR_NEVER);
		formattedTextScroll.getVerticalScrollBar().setLocation(formattedTextScroll.getX() + 10, formattedTextScroll.getY());

		// Console that will display status messages, preview of found commands, etc.
		console = new JEditorPane();
		console.setEditable(false);
		console.setFont(new Font("monospaced", Font.PLAIN, 12));
		consoleScroll = new JScrollPane(console, VERTICAL_SCROLLBAR_ALWAYS, HORIZONTAL_SCROLLBAR_NEVER);
		consoleScroll.setPreferredSize(new Dimension(FRAME_WIDTH, 150));
	}

	/**
	 * Instantiates the buttons on the GUI
	 */
	private void createButtons() {
		importButton = new JButton("Import");
		importButton.addActionListener(this);

		exportButton = new JButton("Export");
		exportButton.addActionListener(this);
	}

	/**
	 * Adds all components to their respective layout component
	 */
	private void finishLayout() {
		buttonPanel.add(importButton);
		buttonPanel.add(exportButton);

		textPanel.add(importedTextScroll);
		textPanel.add(formattedTextScroll);
		topPanel.add(BorderLayout.CENTER, textPanel);
		topPanel.add(BorderLayout.SOUTH, buttonPanel);

		getContentPane().add(BorderLayout.CENTER, topPanel);
		getContentPane().add(BorderLayout.SOUTH, consoleScroll);
	}

	/**
	 * Handles all action events for the GUI components
	 *
	 * @param   e   ActionEvent passed through by any action event on the component
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		String action = e.getActionCommand();

		if(action.equals("Import")) {
			JFileChooser jfc = new JFileChooser(System.getProperty("user.dir"));

			jfc.setAcceptAllFileFilterUsed(false);
			jfc.addChoosableFileFilter(new FileFilter() {
				public String getDescription() {
					return "Text Files (*.txt)";
				}

				public boolean accept(File f) {
					if (f.isDirectory()) return true;
					else return f.getName().toLowerCase().endsWith(".txt");
				}
			});

			if(jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				importFile = jfc.getSelectedFile();

				// Remove all text from previous import
				importedText.setText("");
				formattedText.setText("");

				format = new Formatter(importFile);
				// Split into two, 0 is errors (if any), 1 is format toString()
				String[] response = format.parse(importedText, formattedText).split("-");

				if(response[0].equals(""))
					console.setText("Successfully imported all commands and text from " + importFile.getName() + "\n\n" + response[1]);
				else
					console.setText(response[0] + "\n" + response[1]);
			}
		} else if (action.equals("Export")) {
			if(format != null) {
				try {
					if (format.hasError()) {
						String[] options = {"Continue", "Cancel"};
						int choice = JOptionPane.showOptionDialog(null, "The imported file contains errors, would you like to continue exporting?",
								"Continue Exporting?", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

						if (choice != 0) return;
					}

					// Creates new string with -formatted after name.
					String newFileName = importFile.getName().substring(0, importFile.getName().length() - 4) + "-formatted.txt";

					File exportFile = new File(newFileName);

					// Writes formatted text to file
					BufferedWriter writer = new BufferedWriter(new FileWriter(newFileName));
					writer.write(formattedText.getText());
					writer.close();

					exportFile.createNewFile();

					console.setText("Export successfully completed.");
				} catch (IOException ignored) { }
			}
		}
	}
}