package FileTree;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Created by Administrator on 2016/3/29.
 */
public class FileTreeNode extends DefaultMutableTreeNode {

    private static final long serialVersionUID = 1L;
    private String fileName;
    private Icon fileIcon;
    static FileSystemView fileSystemView = FileSystemView.getFileSystemView();

    private boolean explored = false;
    private java.io.FileFilter filter = null;

    public FileTreeNode (File file, java.io.FileFilter filter) {
        if (filter == null) {
            this.filter = new AllFileFilter();
        } else {
            this.filter = filter;
        }
        setUserObject(file);
    }

    public boolean getAllowsChildren() {
        return isDirectory();
    }
    public boolean isDirectory() {
        return !isLeaf();
    }
    public boolean isLeaf() {
        return getFile().isFile();
    }
    public File getFile() {
        return (File) getUserObject();
    }
    public boolean isExplored() {
        return explored;
    }
    public void setExplored(boolean b) {
        explored = b;
    }
    public String getFileName() {
        return fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    public Icon getFileIcon() {
        return fileIcon;
    }
    public void setFileIcon(Icon fileIcon) {
        this.fileIcon = fileIcon;
    }

    public String toString() {
        if (getFile() instanceof File)
            return fileSystemView.getSystemDisplayName((File) getFile());
        else
            return getFile().toString();
    }

    public void explore() {
        if (!explored) {
            explored = true;
            File file = getFile();
            // 如果这里使用 file.listFiles(filter) 有BUG
            File[] children = file.listFiles();
            if (children == null || children.length == 0) {
                return;
            }
            // 过滤后排序,选加入排序后的目录, 再加入排序后的文件
            ArrayList<File> listDir = new ArrayList<File>();
            ArrayList<File> listFile = new ArrayList<File>();
            for (int i = 0; i < children.length; ++i) {
                File f = children[i];
                if (filter.accept(f)) {
                    if (f.isDirectory()) {
                        listDir.add(f);
                    } else {
                        listFile.add(f);
                    }
                }
            }
            Collections.sort(listDir);
            Collections.sort(listFile);
            for (int i = 0; i < listDir.size(); i++) {
                add(new FileTreeNode((File) listDir.get(i), filter));
            }
            for (int i = 0; i < listFile.size(); i++) {
                add(new FileTreeNode((File) listFile.get(i), filter));
            }
        }
    }

    public static class AllFileFilter implements java.io.FileFilter {
        public boolean accept(File pathname) {
            return true;
        }
    }
}