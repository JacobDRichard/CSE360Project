import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

/**
 * @author Yu Fu, Amodini Pathak, Jacob Richard, Connor Wardell
 * @version 1.0, 3 Dec. 2019
 * Arizona State University
 * CSE 360: Intro to Software Engineering (85141)
 * Team Project
 *
 * LineNumberMargin.java allows all JTextComponents to have a left side margin
 * that will number the lines of text in the component.
 */
class LineNumberMargin extends JComponent implements DocumentListener, CaretListener, ComponentListener {
	private JTextComponent textComp;

	LineNumberMargin(JTextComponent editor) {
		this.textComp = editor;

		editor.getDocument().addDocumentListener(this);
		editor.addComponentListener(this);
		editor.addCaretListener(this);
	}

	/**
	 * Draws the line number margin onto the left of the text component
	 *
	 * @param   g   Graphics object created by repaint
	 */
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		int start = textComp.viewToModel(new Point(0, g.getClipBounds().y));
		int end = textComp.viewToModel(new Point(0, g.getClipBounds().y + g.getClipBounds().height));

		while (start <= end) {
			try {
				Element root = textComp.getDocument().getDefaultRootElement();
				String lineNumber = root.getElement(root.getElementIndex(start)).getStartOffset() == start ? Integer.toString(root.getElementIndex(start) + 1) : null;
				FontMetrics fontMetrics = textComp.getFontMetrics(textComp.getFont());
				int y = textComp.modelToView(start).y + textComp.modelToView(start).height - fontMetrics.getDescent();
				if (lineNumber != null)
					g.drawString(String.format("%3s", lineNumber), getInsets().left + 2, y);

				start = Utilities.getRowEnd(textComp, start) + 1;
			} catch (BadLocationException ignored) { }
		}
	}

	/**
	 * Repaint only for this object (LineNumberMargin)
	 */
	private void repaintMargin() {
		SwingUtilities.invokeLater(this::repaint);
	}

	/**
	 * Resizes the margin, called after any component size changes due to window resizing
	 */
	private void updateSize() {
		Dimension size = new Dimension(28, textComp.getHeight());
		setPreferredSize(size);
		setSize(size);
	}

	/**
	 * Object callback on text insertion
	 *
	 * @param   e   DocumentEvent passed through by any text insertion
	 */
	@Override
	public void insertUpdate(DocumentEvent e) {
		repaintMargin();
	}

	/**
	 * Object callback on text deletion
	 *
	 * @param   e   DocumentEvent passed through by any text deletion
	 */
	@Override
	public void removeUpdate(DocumentEvent e) {
		repaintMargin();
	}

	/**
	 * Object callback on document change
	 *
	 * @param   e   DocumentEvent passed through by any document change
	 */
	@Override
	public void changedUpdate(DocumentEvent e) {
		repaintMargin();
	}

	/**
	 * Object callback on caret move
	 *
	 * @param   e   CaretEvent passed through by any caret move
	 */
	@Override
	public void caretUpdate(CaretEvent e) {
		repaintMargin();
	}

	/**
	 * Object callback on size change
	 *
	 * @param   e   ComponentEvent passed through by any size change
	 */
	@Override
	public void componentResized(ComponentEvent e) {
		updateSize();
		repaintMargin();
	}

	/**
	 * Object callback on component visibility
	 *
	 * @param   e   ComponentEvent passed through by component visibility
	 */
	@Override
	public void componentShown(ComponentEvent e) {
		updateSize();
		repaintMargin();
	}

	/**
	 * Object callback on component position
	 *
	 * @param   e   ComponentEvent passed through the move of the component
	 */
	@Override
	public void componentMoved(ComponentEvent e) { }

	/**
	 * Object callback on component visibility
	 *
	 * @param   e   ComponentEvent passed through by component visibility
	 */
	@Override
	public void componentHidden(ComponentEvent e) { }
}