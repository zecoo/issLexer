package FileTree;

import java.io.*;
import java.util.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.*;
import javax.swing.tree.*;

@SuppressWarnings("serial")
public class FileTree extends JTree implements Serializable {

   public static String treeRootPath = "/Users/22kon/Desktop";
   public static String treeFilePath = "";

   private static final FileSystemView fileSystemView = FileSystemView.getFileSystemView();

   FileTreeNode root;

    private DefaultTreeModel treeModel;
    public FileTree(java.io.FileFilter filter) {
        //File testFile = fileSystemView.getDefaultDirectory();
        File testFile = new File(treeRootPath);
        root = new FileTreeNode(testFile, filter);
        treeModel = new DefaultTreeModel(root);
        root.explore();
        treeModel.nodeStructureChanged(root);
        this.setModel(treeModel);
        addTreeExpansionListener(new FileTree.JFileTreeExpansionListener());
        setCellRenderer(new JFileTreeCellRenderer());
    }

    public FileTreeNode getSelectFileTreeNode() {
        TreePath path = getSelectionPath();
        if (path == null || path.getLastPathComponent() == null) {
            return null;
        }
        return (FileTreeNode) path.getLastPathComponent();
    }
    public void setSelectFileTreeNode(FileTreeNode f) throws Exception {
        this.setSelectFile(f.getFile());
    }

    public static void setSelectFilePath(String string) {
        treeFilePath = string;
    }

    public static String getSelectFilePath() {
        return treeFilePath;
    }

    public void setSelectFile(File f) throws Exception {
        FileTreeNode node = this.expandFile(f);
        TreePath path = new TreePath(node.getPath());
        System.out.println(path.toString() + "in FileTree");
        this.scrollPathToVisible(path);
        this.setSelectionPath(path);
        this.repaint();
    }

    public static void setTreeRootPath(String string){
        treeRootPath = string;
    }

    private String getRootPath() {
        return treeRootPath;
    }

    public FileTreeNode expandFile(File f) throws Exception {
        if (!f.exists()) {
            throw new java.io.FileNotFoundException(f.getAbsolutePath());
        }
        Vector<File> vTemp = new Vector<File>();
        File fTemp = f;
        String fileName = fTemp.getName();
        int index = fileName.lastIndexOf('.');
        if (index > 0 && index < fileName.length() - 1) {
            while (fTemp != null) {
                vTemp.add(fTemp);
                fTemp = fileSystemView.getParentDirectory(fTemp);
            }
        }
        FileTreeNode nParent = (FileTreeNode) treeModel.getRoot();
        for (int i = vTemp.size() - 1; i >= 0; i--) {
            fTemp = (File) vTemp.get(i);
            nParent.explore();
            for (int j = 0; j < nParent.getChildCount(); j++) {
                FileTreeNode nChild = (FileTreeNode) nParent.getChildAt(j);
                if (nChild.getFile().equals(fTemp)) {
                    nParent = nChild;
                }
            }
        }

        return nParent;
    }
    class JFileTreeCellRenderer extends DefaultTreeCellRenderer {
        public Component getTreeCellRendererComponent(JTree tree, Object value,
                                                      boolean sel, boolean expanded, boolean leaf, int row,
                                                      boolean hasFocus) {
            super.getTreeCellRendererComponent(tree, value, sel, expanded,
                    leaf, row, hasFocus);
            try {
                closedIcon = fileSystemView.getSystemIcon(((FileTreeNode) value)
                        .getFile());
                openIcon = closedIcon;
                setIcon(closedIcon);
            } catch (Exception ex) {
            }
            return this;
        }
    }
    class JFileTreeExpansionListener implements TreeExpansionListener {
        public JFileTreeExpansionListener() {
        }
        public void treeExpanded(TreeExpansionEvent event) {
            FileTree fileTree = (FileTree) event.getSource();
            TreePath path = event.getPath();
            if (path == null || path.getLastPathComponent() == null)
                return;
            setCursor(new Cursor(Cursor.WAIT_CURSOR));
            FileTreeNode node = (FileTreeNode) path.getLastPathComponent();

                treeFilePath = node.getFile().getPath();
                setSelectFilePath(treeFilePath);
                System.out.println(treeFilePath + " in selection");
                node.explore();
                JTree tree = (JTree) event.getSource();
                DefaultTreeModel treeModel = (DefaultTreeModel) tree.getModel();

            treeModel.nodeStructureChanged(node);
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

        }

        public void treeCollapsed(TreeExpansionEvent event) {
        }
    }

    public static class AllFileFilter implements java.io.FileFilter {
        public boolean accept(File pathname) {
            return true;
        }
    }
    public static class ExtensionFilter implements java.io.FileFilter {
        String extension;
        public ExtensionFilter(String extension) {
            this.extension = extension.toLowerCase();
        }
        public boolean accept(File pathname) {
            if (pathname.isDirectory()) {
                return true;
            }
            String name = pathname.getName().toLowerCase();
            if (!name.endsWith(extension)) {
                return true;
            }
            return false;
        }
    }

}
