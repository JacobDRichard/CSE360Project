import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

class LineNumberMargin extends JComponent implements DocumentListener, CaretListener, ComponentListener {
	private JTextComponent textComp;

	LineNumberMargin(JTextComponent editor) {
		this.textComp = editor;

		editor.getDocument().addDocumentListener(this);
		editor.addComponentListener(this);
		editor.addCaretListener(this);
	}

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

	private void repaintMargin() {
		SwingUtilities.invokeLater(this::repaint);
	}

	private void updateSize() {
		Dimension size = new Dimension(28, textComp.getHeight());
		setPreferredSize(size);
		setSize(size);
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		repaintMargin();
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		repaintMargin();
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		repaintMargin();
	}

	@Override
	public void caretUpdate(CaretEvent e) {
		repaintMargin();
	}

	@Override
	public void componentResized(ComponentEvent e) {
		updateSize();
		repaintMargin();
	}

	@Override
	public void componentShown(ComponentEvent e) {
		updateSize();
		repaintMargin();
	}

	@Override
	public void componentMoved(ComponentEvent e) { }

	@Override
	public void componentHidden(ComponentEvent e) { }
}