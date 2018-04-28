package FileTree;

import java.awt.Color;
import java.awt.Component;
import java.io.File;

import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.plaf.ColorUIResource;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 * Created by Administrator on 2016/3/29.
 */
public class FileTreeCellRenderer extends  DefaultTreeCellRenderer {

    private static final long serialVersionUID = 1L;

    public FileTreeCellRenderer(){
    }
    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        FileTree fileTree = (FileTree) tree;
        FileTreeNode fileNode = (FileTreeNode) value;
        File file = (File) fileNode.getUserObject();

        JLabel label = (JLabel)super.getTreeCellRendererComponent(tree,value,selected,expanded,leaf,row,hasFocus);
        label.setText(fileNode.getFileName());
        label.setIcon(fileNode.getFileIcon());
        label.setOpaque(false);
        super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf,
                row, hasFocus);

        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        if (node.getUserObject().toString().trim().startsWith("Error")) {
            setForeground(Color.RED);
        }
        return label;
    }

    @Override
    public void setBackground(Color bg) {
        if (bg instanceof ColorUIResource){
            bg = null;
        }
        super.setBackground(bg);
    }
}