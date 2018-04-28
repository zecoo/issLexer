package Nodes;

import ICGenerator.Generator;
import ICGenerator.NameTable;
import ICGenerator.icBlockList;
import Token.Token;

import java.util.ArrayList;
import Token.*;
/**
 * Created by yl on 2017/10/28.
 */
public class VariableNode extends Node{

    public VariableNode(int l)
    {
        name = new String();
        cursor_t =-1;
        cursor_val =0;
        subNode = new ArrayList<>();
        values = new ArrayList<Token>();
        level = l;
        type = "VariableNode";
    }

    @Override
    public int matchNextToken(Token t) throws Exception {
        for(Node node:subNode)
        {
            if(!node.getStatus())
                return node.matchNextToken(t);
        }

        ++cursor_t;
        switch (cursor_t)
        {
            case 0:
                if(t.equals(6)||t.equals(7)) {
                    values.add(t);
                    return 1;
                }
                break;
            case 1:
                if(t.equals(25))
                {
                    values.add(t);
                    return 1;
                }
                else if(t.equals(21))
                {
                    cursor_t =2;
                    values.add(t);
                    return 1;
                }
                break;
            case 2:
                if(t.equals(18))
                {
                    matched = true;
                    return 1;
                }
                break;


            case 3:
                if(t.equals(23) ||t.equals(25))
                {
                    subNode.add(new ExpressionNode(level + 1));
                    return subNode.get(subNode.size()-1).matchNextToken(t);
                }
                throw new Exception("wrong expression at line:" + t.getTokenLine());
            case 4:
                if(t.equals(22))
                {
                    values.add(t);
                    return 1;
                }
                break;

            case 5:
                if(t.equals(25))
                {
                    values.add(t);
                    return 1;
                }
                break;
            case 6:
                if(t.equals(18))
                {
                    matched = true;
                    return 1;
                }
                break;
            default:
                break;
        }

        --cursor_t;
        return 0;
    }


    @Override
    public void generate(Generator g) throws Exception {
        NameTable nt = g.nameTable;
        icBlockList blocks = g.blocks;
        int type = values.get(0).getIdentifier();
        String w = "new ";
        String isDefined = nt.get(values.get(1).getContent());
        if(!isDefined.equals(values.get(1).getContent()))
        {//已定义
            String[] s = isDefined.split("_");
            if(!s[s.length-1].contains("-"))
                throw new Exception("Variable has been defined: " +values.get(1).getContent()+"error at line:" +values.get(1).getTokenLine());
        }

        if(values.get(1).equals(25))
        {
            if(type == 6)
                w+=nt.newInt(values.get(1).getContent());
            else if (type ==7)
                w+=nt.newFloat(values.get(1).getContent());
            blocks.newBlock(values.get(0).getTokenLine());
            blocks.write(w);
        }
        else if(values.get(1).equals(21))
        {
            subNode.get(0).generate(g);
            blocks.newBlock(values.get(0).getTokenLine());
            blocks.write("pop ebx");
            String arrName = nt.newArray(values.get(3).getContent(),type);
            blocks.write(w+arrName+" " +"ebx");
        }

    }


}
