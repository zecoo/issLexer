package Main;

import java.util.ArrayList;

/**
 * Created by yl on 2017/10/6.
 */
public class StringProcessor {

    public boolean isMultiAnnotaion = false;

public ArrayList<String> init(String inChars) {

    int upbound = 0;
    int downbound = 0;
    int length = inChars.length();
    boolean isToken = false;

    ArrayList<String> tokens = new ArrayList<String>();

    while (upbound < length) {
        while(isMultiAnnotaion && inChars.charAt(upbound)!='*')
        {
            upbound++;
            downbound = upbound;
            //如果目前在注释中,则继续读取，直到碰到*
            if(upbound >=length )//如果整行读取完毕，则退出
                return tokens;
        }
        switch (inChars.charAt(upbound)) {
            case '\n':
            case '\r':
            case '\t':
            case ' '://碰到空格，如果当前是在读取token时，则终止此token
                if (isToken)
                {
                    tokens.add(inChars.substring(downbound, upbound));
                    isToken = false;
                }
                upbound++;
                downbound = upbound;
                break;
            case '/':
                if (upbound + 1 < length && inChars.charAt(upbound + 1) == '*') {//多行注释，还需要继续读取
                    isMultiAnnotaion = true;
                    if (isToken)
                    {
                        tokens.add(inChars.substring(downbound, upbound));
                        isToken = false;
                    }
                    upbound=upbound+2;
                    downbound = upbound;
                } else if (upbound + 1 < length && inChars.charAt(upbound + 1) == '/') {//单行注释，直接返回此行的分析结果
                    if (downbound < upbound)
                        tokens.add(inChars.substring(downbound, upbound));
                    return tokens;
                } else//其余情况，则当作前一个token的终止和一个token
                {
                    if (isToken)
                    {
                        tokens.add(inChars.substring(downbound, upbound));
                        isToken = false;
                    }
                    tokens.add("/");
                    upbound++;
                    downbound = upbound;
                }
                break;
            case '*':
                if (isMultiAnnotaion||upbound + 1 < length && inChars.charAt(upbound + 1) == '/')//在注释的模式下,如果*后面接/，则视为取消注释
                {
                    isMultiAnnotaion = false;
                    upbound=upbound+2;
                    downbound = upbound;
                }
                else//否则当作前一个token的终止和一个token
                {
                    if (isToken)
                    {
                        tokens.add(inChars.substring(downbound, upbound));
                        isToken = false;
                    }
                    tokens.add("*");
                    upbound++;
                    downbound = upbound;
                }
                break;
            default:
                //如果是合法字符，则读入作token
                if (    inChars.charAt(upbound) == '_' ||
                        inChars.charAt(upbound) == '.'||
                        (inChars.charAt(upbound)>='a' && inChars.charAt(upbound) <= 'z')||
                        (inChars.charAt(upbound)>='A' && inChars.charAt(upbound)<='Z')  ||
                        (inChars.charAt(upbound) >= '0' && inChars.charAt(upbound) <= '9') )
                {
                    upbound++;
                    isToken = true;;
                }
                else  //否则当作前一个token的终止和一个token
                {
                    if (isToken)
                    {
                        tokens.add(inChars.substring(downbound, upbound));
                        isToken = false;
                    }
                    tokens.add( String.valueOf( inChars.charAt(upbound)) );
                    upbound++;
                    downbound = upbound;
                }

             }
        }
    return tokens;
}


}
