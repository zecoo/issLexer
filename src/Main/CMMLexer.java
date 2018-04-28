package Main;

import ICGenerator.Generator;
import Interpreter.ICInterpreter;
import Nodes.GlobalNode;
import Nodes.Node;
import Token.Token;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

/**
 * Created by yl on 2017/10/7.
 */
public class CMMLexer {
private StringProcessor sp;

public CMMLexer()
{
    sp = new StringProcessor();
}

    public ArrayList<Token> tokens = new ArrayList<>();

    public int TokenAnalyzer(String s,int l){
        ArrayList<String> strings = sp.init(s);

        if(strings==null || strings.size()==0)
            return 0;

        int ident=0;
        for(int i =0;i< strings.size();i++)
        {
            Token t= new Token(strings.get(i),l);
            ident=TokenToSyn(strings.get(i));
            if(ident<0)
                return ident;
            t.setIdentifier(ident);
            tokens.add(t);
        }

        return 0;
    }

    private Integer TokenToSyn(String token) {
        if (token.length() <= 0 || token==null)
            return -1;

        int length = token.length();

        //关键字
        switch (token.charAt(0)) {
            case 'i':
                if (token.equals("if"))
                    return 1;
                else if (token.equals("int"))
                    return 6;
                break;
            case 'e':
                if (token.equals("else"))
                    return 2;
                break;
            case 'w':
                if (token.equals("while"))
                    return 3;
                else if (token.equals("write"))
                    return 5;
                break;
            case 'r':
                if (token.equals("read"))
                    return 4;
                else if (token.equals("real"))
                    return 7;
                break;
            case '+':
                return 8;
            case '-':
                return 9;
            case '*':
                return 10;
            case '/':
                return 11;
            case '=':
                if (length == 1)
                    return 12;
                else if (token.equals("=="))
                    return 14;
                break;
            case '<':
                return 13;
            case '>':
                return 15;
            case '(':
                return 16;
            case ')':
                return 17;
            case ';':
                return 18;
            case '{':
                return 19;
            case '}':
                return 20;
            case '[':
                return 21;
            case ']':
                return 22;
            default:
                break;
        }

        //数字
        if (token.charAt(0) >= '0' && token.charAt(0) <= '9'){
            int countDot = 0;
            for (int i = 0; i < length; i++) {
                if (token.charAt(i) >= '0' && token.charAt(i) <= '9')
                    continue;
                else if (token.charAt(i) == '.' && countDot < 2)
                    countDot++;
                else
                    return -2;
            }

            if(countDot==0)
                return 23;
            else if (countDot==1 && token.charAt(token.length()-1)!='.')
                return 24;
        }

        //标识符
        if (token.charAt(0) == '_' || (token.charAt(0)>='a' && token.charAt(0) <= 'z')||(token.charAt(0)>='A' && token.charAt(0)<='Z'))
        {
            for(int i =0;i<length;i++)
            {
                if (token.charAt(0) == '_' ||
                        (token.charAt(0)>='a' && token.charAt(0) <= 'z')||
                        (token.charAt(0)>='A' && token.charAt(0)<='Z')  ||
                        (token.charAt(i) >= '0' && token.charAt(i) <= '9') )
                    continue;
                else
                    return -3;
            }
            return 25;
        }

        return -4;
    }

    //if come across an error, editor get the error token
    public Token getErrorToken()
    {
        return tokens.get(tokens.size()-1);
    }

//    public static void main(String[] args)throws Exception {
//        CMMLexer cmm = new CMMLexer();
//
//        Main main = new Main();
//
//        File file = new File("test.txt");
//        BufferedReader bf = new BufferedReader(new FileReader(file));
//        String s;
//        int i=0;
//        int res=0;
//        while ((s= bf.readLine())!=null)
//        {
//            ++i;
//            res=cmm.TokenAnalyzer(s,i);
//            if(res>=0)
//                continue;
//            else
//                break;
//        }
//        //print errorCode and the lineNum and the token
//
//        Node node = new GlobalNode(0);
//        int t=0; int temp=0;
//        while (t<cmm.tokens.size())
//        {
//            temp =node.matchNextToken(cmm.tokens.get(t));
//            if(temp ==1)//matched and read next token
//            {
//                ++t;
//                continue;
//            }
//            else if(temp ==2)//need to do match again
//            {
//                continue;
//            }
//            else//print the lineNum and the token
//                break;
//        }
//
//        if(t==cmm.tokens.size())//all tokens are matched
//            node.print();
//
//        Generator g = new Generator();
//        node.generate(g);
//        g.write("test.cmmclass");
//        ICInterpreter icInterpreter = new ICInterpreter("test.cmmclass");
//        icInterpreter.RunTo(16);
//    }
}
