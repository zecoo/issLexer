package Util;

import javax.swing.*;
import javax.swing.text.*;

import java.awt.*;              //for layout managers and more

import java.io.*;
import java.util.ArrayList;

public class CustomizedTextPane extends JPanel {

    // todo: set absolute path
    public static String filePath = "test.txt";

    public String currentLineNumber = null;
    JTextPane textPane;

    public CustomizedTextPane() throws IOException, BadLocationException {
        setLayout(new BorderLayout());
        //Create a text pane.
        textPane = createTextPane();
        JScrollPane paneScrollPane = new JScrollPane(textPane);
        paneScrollPane.setVerticalScrollBarPolicy(
                        JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        paneScrollPane.setPreferredSize(new Dimension(250, 155));
        paneScrollPane.setMinimumSize(new Dimension(10, 10));
        add(paneScrollPane);
    }

    public JTextPane createTextPane() throws IOException {
        JTextPane textPane = new JTextPane();
        textPane.setStyledDocument(new CustomizeDocument(0));
        setTabs(textPane,  4);
        StyledDocument doc = textPane.getStyledDocument();
        addStylesToDocument(doc);
        File testFile = new File(getFilePath());
        FileReader fileReader = new FileReader(testFile);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        ArrayList<String> list = new ArrayList<>();
        String line;
        while ((line = bufferedReader.readLine()) != null){
            list.add(line);
            // buffered reader will drop '\n' while reading. So add it back
            list.add("\n");
        }
        bufferedReader.close();
        String[] initString = list.toArray(new String[list.size()]);
        try {
            for (int i=0; i < initString.length; i++) {
                doc.insertString(doc.getLength(), initString[i],
                                 doc.getStyle("regular"));
            }
        } catch (BadLocationException ble) {
            System.err.println("Couldn't insert initial text into text pane.");
        }
        return textPane;
    }

    protected void addStylesToDocument(StyledDocument doc) {

        //Initialize some styles.
        Style def = StyleContext.getDefaultStyleContext().
                        getStyle(StyleContext.DEFAULT_STYLE);

        Style regular = doc.addStyle("regular", def);
        StyleConstants.setFontFamily(def, "Courier Prime");
        StyleConstants.setFontSize(def, 15);
        StyleConstants.setBold(def, true);

        StyleConstants.setForeground(def, Color.BLUE);
    }


    public String getFilePath() {
        return filePath;
    }
    public static void setFilePath(String path) {
        filePath = path;
    }

    public static void setTabs( final JTextPane textPane, int charactersPerTab) {
        FontMetrics fm = textPane.getFontMetrics( textPane.getFont() );
//          int charWidth = fm.charWidth( 'w' );
        int charWidth = fm.charWidth( ' ' );
        int tabWidth = charWidth * charactersPerTab;
//      int tabWidth = 100;

        TabStop[] tabs = new TabStop[5];

        for (int j = 0; j < tabs.length; j++)
        {
            int tab = j + 1;
            tabs[j] = new TabStop( tab * tabWidth );
        }

        TabSet tabSet = new TabSet(tabs);
        SimpleAttributeSet attributes = new SimpleAttributeSet();
        StyleConstants.setTabSet(attributes, tabSet);
        int length = textPane.getDocument().getLength();
        textPane.getStyledDocument().setParagraphAttributes(0, length, attributes, false);
    }
}
