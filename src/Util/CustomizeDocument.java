package Util;

import Util.SyntaxMgr;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.StyleConstants;

public class CustomizeDocument extends DefaultStyledDocument {
    private int type = -1;// 数据连接类型

    AttributeSet myAttributeSet = null;

    public CustomizeDocument(int type) {
        this.type = type;
    }
    public void insertString(int offset, String str, AttributeSet a)
            throws BadLocationException {
        this.myAttributeSet = a;
        super.insertString(offset, str, a);
        setSyntaxColor(offset, str.length());
    }
    public void remove(int offs, int len) throws BadLocationException {
        super.remove(offs, len);
        setSyntaxColor(offs);
    }
    private String getPositionChar(int offset) {
        String str = "";
        try {
            str = getText(offset, 1);
        } catch (BadLocationException ex) {
            // ex.printStackTrace(System.out);
        }
        return str;
    }
    //从指定的位置开始，倒推到第一个遇到空格位置
    private String getBeforeBlankString(int offset) {
        String str = "";
        if (offset <= 0)
            return "";

        str = getPositionChar(offset);
        if (SyntaxMgr.isSpaceChar(str))
            return "";

        String r = getBeforeBlankString(offset - 1);
        return r + str;
    }
     //从指定的位置开始，顺推到第一个遇到空格位置
    private String getAfterBlankString(int offset) {
        String str = "";
        if (offset > getLength())
            return "";
        str = getPositionChar(offset);
        if (SyntaxMgr.isSpaceChar(str))
            return "";
        String r = getAfterBlankString(offset + 1);
        return str + r;
    }
     // 根据Position，向前判断，向后判断，设置颜色,返回设置颜色末尾的位置
    private int setSyntaxColor(int offset) {
        if (offset <= 0)
            return offset;// 如果设置的位置不存在，可以不用考虑
        if (myAttributeSet == null)
            return offset;// 如果myAttributeSet为null,可以不用考虑
        String ifSyntax = "";
        String before = getBeforeBlankString(offset - 1);
        String after = getAfterBlankString(offset);
        ifSyntax = (before + after).trim();
        int start = offset - before.length();
        int tmp_len = ifSyntax.length();

        if (start < 0 || tmp_len <= 0)
            return offset;// 如果设置颜色的字符串为空，返回

        // 设置颜色
        StyleConstants.setForeground((MutableAttributeSet) myAttributeSet,
                SyntaxMgr.isSyntax(type, ifSyntax));

        setCharacterAttributes(start, tmp_len, myAttributeSet, true);
        return start + tmp_len;
    }
    private int setSyntaxColor(int offset, int len) throws BadLocationException {
        // 如果范围不存在，不考虑
        if (offset <= 0 || len < 0)
            return offset;
        int tmp_offset = offset;
        tmp_offset = setSyntaxColor(tmp_offset);
        while (++tmp_offset < offset + len) {
            // tmp_offset = setSyntaxColor(tmp_offset);
            tmp_offset = doMiddleWord(tmp_offset);
        }
        tmp_offset = setSyntaxColor(tmp_offset);// 设置循环完后的最后一个单词
        return tmp_offset;
    }
    //处理paste时中间word的显示
    private int doMiddleWord(int offset) {
        String str = getAfterBlankString(offset);
        String ifSyntax = str.trim();
        int tmp_len = ifSyntax.length();
        if (offset <= 0 || tmp_len <= 0)
            return offset;// 如果设置颜色的字符串为空，返回
        // 设置颜色
        StyleConstants.setForeground((MutableAttributeSet) myAttributeSet,
                SyntaxMgr.isSyntax(type, ifSyntax));

        setCharacterAttributes(offset, tmp_len, myAttributeSet, true);

        return offset + tmp_len;

    }

}