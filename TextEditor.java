import javax.swing.*;
import javax.swing.undo.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class TextEditor extends JFrame implements ActionListener {
    private JTextArea textArea;
    private JFileChooser fileChooser;
    private String currentFile;
    private Font currentFont;
    private UndoManager undoManager;

    public TextEditor() {
        setTitle("Notepad");
        setSize(600, 400);

        textArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(textArea);
        add(scrollPane);

        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem newMenuItem = new JMenuItem("New");
        JMenuItem openMenuItem = new JMenuItem("Open");
        JMenuItem saveMenuItem = new JMenuItem("Save");
        JMenuItem saveAsMenuItem = new JMenuItem("Save As");
        JMenuItem exitMenuItem = new JMenuItem("Exit");

        newMenuItem.addActionListener(this);
        openMenuItem.addActionListener(this);
        saveMenuItem.addActionListener(this);
        saveAsMenuItem.addActionListener(this);
        exitMenuItem.addActionListener(this);

        fileMenu.add(newMenuItem);
        fileMenu.add(openMenuItem);
        fileMenu.add(saveMenuItem);
        fileMenu.add(saveAsMenuItem);
        fileMenu.add(exitMenuItem);
        menuBar.add(fileMenu);

        JMenu formatMenu = new JMenu("Format");
        JMenuItem fontMenuItem = new JMenuItem("Font");
        JMenuItem fontSizeMenuItem = new JMenuItem("Font Size");
        JMenuItem boldMenuItem = new JMenuItem("Bold");
        JMenuItem italicMenuItem = new JMenuItem("Italic");

        fontMenuItem.addActionListener(this);
        fontSizeMenuItem.addActionListener(this);
        boldMenuItem.addActionListener(this);
        italicMenuItem.addActionListener(this);

        formatMenu.add(fontMenuItem);
        formatMenu.add(fontSizeMenuItem);
        formatMenu.add(boldMenuItem);
        formatMenu.add(italicMenuItem);
        menuBar.add(formatMenu);

        JMenu editMenu = new JMenu("Edit");
        JMenuItem undoMenuItem = new JMenuItem("Undo");
        JMenuItem redoMenuItem = new JMenuItem("Redo");
        JMenuItem cutMenuItem = new JMenuItem("Cut");
        JMenuItem copyMenuItem = new JMenuItem("Copy");
        JMenuItem pasteMenuItem = new JMenuItem("Paste");

        undoMenuItem.addActionListener(this);
        redoMenuItem.addActionListener(this);
        cutMenuItem.addActionListener(this);
        copyMenuItem.addActionListener(this);
        pasteMenuItem.addActionListener(this);

        editMenu.add(undoMenuItem);
        editMenu.add(redoMenuItem);
        editMenu.add(cutMenuItem);
        editMenu.add(copyMenuItem);
        editMenu.add(pasteMenuItem);
        menuBar.add(editMenu);

        setJMenuBar(menuBar);
        fileChooser = new JFileChooser();
        currentFont = new Font("Arial", Font.PLAIN, 12);
        undoManager = new UndoManager();
        textArea.getDocument().addUndoableEditListener(undoManager);

        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        if (command.equals("New")) {
            textArea.setText("");
            currentFile = null;
        } else if (command.equals("Open")) {
            int returnVal = fileChooser.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) // By selecting OK
            {
                File file = fileChooser.getSelectedFile();
                try {
                    FileReader reader = new FileReader(file);
                    textArea.read(reader, null); // Reading file line by line
                    reader.close();
                    currentFile = file.getAbsolutePath(); // Setting path
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        } else if (command.equals("Save")) {
            if (currentFile != null) {
                try {
                    FileWriter writer = new FileWriter(currentFile);
                    textArea.write(writer);
                    writer.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            } else {
                saveAs();
            }
        } else if (command.equals("Save As")) {
            saveAs();
        } else if (command.equals("Exit")) {
            System.exit(0);
        } else if (command.equals("Font")) {
            selectFont();
        } else if (command.equals("Font Size")) {
            selectFontSize();
        } else if (command.equals("Bold")) {
            toggleBold();
        } else if (command.equals("Italic")) {
            toggleItalic();
        } else if (command.equals("Undo")) {
            if (undoManager.canUndo()) {
                undoManager.undo();
            }
        } else if (command.equals("Redo")) {
            if (undoManager.canRedo()) {
                undoManager.redo();
            }
        } else if (command.equals("Cut")) {
            textArea.cut();
        } else if (command.equals("Copy")) {
            textArea.copy();
        } else if (command.equals("Paste")) {
            textArea.paste();
        }
    }

    private void saveAs() {
        int returnVal = fileChooser.showSaveDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                FileWriter writer = new FileWriter(file);
                textArea.write(writer);
                writer.close();
                currentFile = file.getAbsolutePath();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void selectFont() {
        String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        String selectedFontName = (String) JOptionPane.showInputDialog(
                this,
                "Select Font",
                "Font",
                JOptionPane.PLAIN_MESSAGE,
                null,
                fonts,
                currentFont.getFontName());
        if (selectedFontName != null) {
            currentFont = new Font(selectedFontName, currentFont.getStyle(), currentFont.getSize());
            textArea.setFont(currentFont);
        }
    }

    private void selectFontSize() {
        String sizeStr = JOptionPane.showInputDialog(this, "Enter Font Size:");
        if (sizeStr != null && !sizeStr.isEmpty()) {
            int size = Integer.parseInt(sizeStr);
            if (size > 0) {
                currentFont = currentFont.deriveFont((float) size);
                textArea.setFont(currentFont);
            }
        }
    }

    private void toggleBold() {
        int style = currentFont.getStyle();
        if ((style & Font.BOLD) == Font.BOLD) {
            style &= ~Font.BOLD; // Clear the bold
        } else {
            style |= Font.BOLD; // Set the bold
        }
        currentFont = currentFont.deriveFont(style);
        textArea.setFont(currentFont);
    }

    private void toggleItalic() {
        int style = currentFont.getStyle();
        if ((style & Font.ITALIC) == Font.ITALIC) {
            style &= ~Font.ITALIC; // Clear the italic
        } else {
            style |= Font.ITALIC; // Set the italic
        }
        currentFont = currentFont.deriveFont(style);
        textArea.setFont(currentFont);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TextEditor());
    }
}
