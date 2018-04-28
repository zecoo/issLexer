package Main;

import FileTree.*;
import ICGenerator.Generator;
import Interpreter.ICInterpreter;
import Nodes.GlobalNode;
import Nodes.Node;
import Util.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.border.MatteBorder;
import javax.swing.text.BadLocationException;

public class Main extends SelfAdaption implements ActionListener {

    private String filePath = "test.txt";

    private JPanel leftPanel = new JPanel();
    private JPanel rightPanel = new JPanel();
    // leftPanel = fileTree + toolbar
    private JScrollPane dirPanel = new JScrollPane();
    private JPanel toolBar = new JPanel();
    // rightPanel = editArea + debugArea
    JTextPane textPane;
    JScrollPane codePane = new JScrollPane();
    private static JTextArea consoleArea = new JTextArea();
    private static JTextArea nodeTreeArea = new JTextArea();
    private static JTabbedPane proAndConPanel = new JTabbedPane();
    private static JPanel debugBtnPanel = new JPanel();

    public static String errorMsg = null;
    public static String nodeInfo = null;
    public static int consoleLineNo = 0;

    public static void main(String[] args) throws IOException, BadLocationException {
        new Main();
    }

    private Main() throws IOException, BadLocationException {
        initCodeArea();
        initDebugArea();
        initToolBarArea();
        addComponent(rightPanel, 0, 0, 1, 1, 1, 1);
        addComponent(leftPanel, 1, 0, 1, 1, 2, 2);
        JFrame mainFrame = new JFrame("Java SWING Examples");
        Container container = mainFrame.getContentPane();
        container.add(rightPanel, BorderLayout.CENTER);
        container.add(leftPanel, BorderLayout.WEST);
        mainFrame.setSize(1080,720);
        mainFrame.setVisible(true);
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    public void executeParser() throws Exception {
        CMMLexer cmm = new CMMLexer();
        File file = new File(filePath);
        BufferedReader bf = new BufferedReader(new FileReader(file));
        String s;
        int i=0;
        int res=0;
        while ((s= bf.readLine())!=null)
        {
            ++i;
            res=cmm.TokenAnalyzer(s,i);
            if(res>=0)
                continue;
            else
                break;
        }
        //print errorCode and the lineNum and the token

        Node node = new GlobalNode(0);
        int t=0; int temp=0;
        while (t<cmm.tokens.size())
        {
            temp =node.matchNextToken(cmm.tokens.get(t));
            if(temp ==1){   //matched and read next token{
                ++t;
                continue;
            }
            else if(temp ==2){  //need to do match again
                continue;
            }
            else {  //print the lineNum and the token
                int line = cmm.tokens.get(t).getTokenLine();
                String content = cmm.tokens.get(t).getContent();
                String testStr = " : Error found around line " + line + ".     Please check around 【 " + content + " 】\n";
                errorMsg = String.format("%-50s%s",filePath,testStr);
                System.out.println(errorMsg);
                consoleLineNo++;
                showErrorMsg();

            }
            break;
        }

        if(t==cmm.tokens.size()){
            //all tokens are matched
            String testStr = " : Congratulation! No error found!\n";
            errorMsg = String.format("%-50s%s",filePath,testStr);
            showErrorMsg();
            setNodeInfo(node.print());
            showNodeMsg();
        }


        Generator g = new Generator();
        node.generate(g);
        g.write("test.cmmclass");
        ICInterpreter icInterpreter = new ICInterpreter("test.cmmclass");
    }

    private void initToolBarArea() {
        BoxLayout leftLayout = new BoxLayout(leftPanel, BoxLayout.Y_AXIS);
        JButton openBtn = new JButton("OPEN");
        JButton saveBtn = new JButton("SAVE");
        openBtn.addActionListener(this);
        saveBtn.addActionListener(this);
        saveBtn.setBounds(50,50,50,50);
        openBtn.setBounds(50,100,50,50);
        toolBar.add(openBtn);
        toolBar.add(saveBtn);
        leftPanel.setLayout(leftLayout);
        leftPanel.setBorder(BorderFactory.createTitledBorder("File"));
        leftPanel.add(toolBar);
        dirPanel.setPreferredSize(new Dimension(200,680));
        leftPanel.add(dirPanel);
        initFileTree();
    }

    private void initFileTree(){
        FileTree fileTree = new FileTree(new FileTree.ExtensionFilter(".tmp"));
        fileTree.addTreeSelectionListener(e -> {
            FileTreeNode newNode = (FileTreeNode) fileTree.getLastSelectedPathComponent();
            filePath = newNode.getFile().getPath();
            if (newNode.isLeaf()) {
                try {
                    CustomizedTextPane.setFilePath(filePath);
                    setFilePath(filePath);
                    initCodeArea();
                } catch (IOException | BadLocationException e1) {
                    e1.printStackTrace();
                }
            }
        });
        dirPanel.setViewportView(fileTree);
    }

    private void initCodeArea() throws IOException, BadLocationException {
        textPane = new CustomizedTextPane().createTextPane();
        LinePainter paintedTextPane = new LinePainter(textPane, Color.LIGHT_GRAY);
//        textPane.addCaretListener(new CaretListener() {
//            @Override
//            public void caretUpdate(CaretEvent e) {
//                //System.out.println(e.getMark());
//                try {
//                    currentLineNo = textPane.modelToView(textPane.getCaretPosition()).y/16+1;
//                    System.out.println("currentLineNumber is "+ currentLineNo);
//                } catch (BadLocationException ex) {
//                }
//            }
//        });
        textPane.setBorder(new MatteBorder(0,10,0,0,Color.WHITE));
        codePane.setViewportView(textPane);
        TextLineNumber textLineNumber = new TextLineNumber(textPane);
        codePane.setRowHeaderView(textLineNumber);
        codePane.setBorder(BorderFactory.createTitledBorder("Editor"));
        codePane.setPreferredSize(new Dimension(1000,450));
        BoxLayout rightLayout = new BoxLayout(rightPanel, BoxLayout.Y_AXIS);
        rightPanel.setLayout(rightLayout);
    }

    private void initDebugArea() {

        consoleArea.setEditable(true);
        consoleArea.setBorder(new MatteBorder(0,6,0,0,Color.WHITE));
        proAndConPanel.setBorder(BorderFactory.createTitledBorder("Output"));
        proAndConPanel.setPreferredSize(new Dimension(1000,250));
        proAndConPanel.add(new JScrollPane(consoleArea), "Console");
        JPanel debugPanel = new JPanel();
        debugPanel.setLayout( new BoxLayout(debugPanel, BoxLayout.X_AXIS));
        debugPanel.add(debugBtnPanel);
        debugPanel.add(proAndConPanel);
        rightPanel.add(codePane);
        rightPanel.add(debugPanel);
        consoleArea.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode()==KeyEvent.VK_ENTER) {
                    try {
                        getCommand();
                        consoleLineNo++;
                    } catch (BadLocationException e1) {
                        e1.printStackTrace();
                    }
                }
            }
            @Override
            public void keyReleased(KeyEvent e) {
            }
        });
        // initOtherArea
        initDebugBtn();
        initTreeNodeArea();
    }

    private void initTreeNodeArea() {
        nodeTreeArea.setEditable(false);
        nodeTreeArea.setBorder(new MatteBorder(0,6,0,0,Color.WHITE));
        proAndConPanel.add(new JScrollPane(nodeTreeArea), "Tree");
    }

    public void initDebugBtn(){
        JButton runBtn = createBtnWithIcon("./src/Pics/run.png");
        JButton stepInBtn = createBtnWithIcon("./src/Pics/step-into.png");
        JButton stopBtn = createBtnWithIcon("./src/Pics/stop.png");
        runBtn.addActionListener(listener ->{
            try {
                executeParser();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        stepInBtn.addActionListener(listener -> {
            TextLineNumber.currentLineForeground = Color.RED;
        });
        stopBtn.addActionListener(listener -> {
            // todo: stop action
        });
        debugBtnPanel.add(runBtn);
        debugBtnPanel.add(stepInBtn);
        debugBtnPanel.add(stopBtn);
        debugBtnPanel.setLayout(new BoxLayout(debugBtnPanel, BoxLayout.Y_AXIS ));
    }

    public void showErrorMsg() {
        consoleArea.append(getErrorMsg());
        consoleLineNo++;
    }

    public void showNodeMsg() {
        nodeTreeArea.setText("");
        nodeTreeArea.append(getNodeInfo());
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public static String getErrorMsg() {
        return errorMsg;
    }

    public static String getNodeInfo() {
        return nodeInfo;
    }

    public static void setNodeInfo(String info) {
        nodeInfo = info;
    }

    public static String getCommand() throws BadLocationException {
        String text = consoleArea.getText();
        text.replace("\n","");
        int startOffset = consoleArea.getLineStartOffset(consoleLineNo);
        int endOffset = consoleArea.getLineEndOffset(consoleLineNo);
        String command = text.substring(startOffset,endOffset);
        System.out.println("Read from console:  " + command);
        return command;
    }

    private JButton createBtnWithIcon(String icon) {
        JButton btn = new JButton("", new ImageIcon(icon));
        btn.setPreferredSize(new Dimension(35, 35));// 设置按钮大小
        btn.setContentAreaFilled(false);// 设置按钮透明
        btn.setMargin(new Insets(0, 0, 0, 0));// 按钮内容与边框距离
        //btn.addMouseListener(new MyMouseListener(this));
        return btn;
    }

    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals("OPEN")){
            String fileChoosePath = "/Users/22kon/Documents/LingusticCS/Java";
            JFileChooser fileChooser = new JFileChooser(fileChoosePath);
            JFrame f = null;
            int result = fileChooser.showOpenDialog(f);
            if (result == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                FileTree.setTreeRootPath(file.getParent());
                initFileTree();
            } else if (result == JFileChooser.CANCEL_OPTION) {
                System.out.println("file not exists");
            }
        }
        else if (e.getActionCommand().equals("SAVE")) {
            String s1 = textPane.getText();
            System.out.println("File saved");
            //获取textarea里面的内容。
            try {
                FileWriter fw=new FileWriter(new File(filePath));   //文件写入
                fw.write(s1);
                fw.flush();
            } catch (IOException ex) {
                System.out.println(ex);
            }
        }
    }
}


