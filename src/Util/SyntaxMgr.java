package Util;

import java.awt.*;
import java.util.Objects;
import java.util.regex.Pattern;

public class SyntaxMgr {
    static boolean isSpaceChar(String str) {
        return (" ".equals(str)|| "\n".equals(str));
    }

    static Color getSpaceColor() {
        return Color.black;
    }



    static Color isSyntax(int type, String str) {

        if (str.equals("if") || str.equals("else") ||  str.equals("while")) {
            return Color.blue.darker();
        }
        else if (str.equals("int") || str.equals("double") || str.equals("real")) {
            return Color.GREEN.darker();
        }
        else if (str.equals("read") || str.equals("write") ){
            return Color.ORANGE;
        }

        else if (str.equals("printf") || str.equals("return")){
            return Color.MAGENTA.darker();
        }
        return Color.BLACK;
    }
}
