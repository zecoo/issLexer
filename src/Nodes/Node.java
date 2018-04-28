package Nodes;

import java.util.ArrayList;

import ICGenerator.Generator;
import Token.Token;
/**
 * Created by yl on 2017/10/28.
 */
public abstract class Node {
    String name;
    public int cursor_t;//token
    public int cursor_val;
    public boolean matched = false;
    public ArrayList<Node> subNode;
    public ArrayList<Token> values;
    public int level;
    public String type="Node";

    public abstract int matchNextToken(Token t) throws Exception;//递归匹配
    public Node getNextNode()//没什么用
    {
        int size = subNode.size();
        if(cursor_t<size) {
            ++cursor_t;
            return subNode.get(cursor_t);
        }
        else
            return null;
    }
    public String print()//递归打印
    {
        String s = new String("");
        for(int i=1;i<level;i++)
        {
            name+="  ";
        }

        name += type;
        s +=name;
        s+="\n";
        for(Node node : subNode)
        {
            s+=node.print();
        }
        return s;
    }
    public boolean getStatus()
    {
        return  matched;
    }

    public abstract void generate(Generator g) throws Exception;//用以生成中间骂时使用
}
