package Nodes;

import ICGenerator.Generator;
import ICGenerator.NameTable;
import ICGenerator.icBlockList;
import Token.Token;

import java.util.ArrayList;

/**
 * Created by yl on 2017/10/29.
 */
public class ExpressionNode extends Node {

    public ExpressionNode(int l)
    {
        name = new String();
        cursor_t =-1;
        cursor_val =0;
        subNode = new ArrayList<>();
        values = new ArrayList<Token>();
        level = l;
        type = "ExpressionNode";
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
                subNode.add(new TermNode(level+1));
                return subNode.get(subNode.size()-1).matchNextToken(t);
            case 1:
                if(t.equals(8)||t.equals(9)||t.equals(10)||t.equals(11))
                {
                    values.add(t);
                    return 1;
                }
                else
                {
                    matched = true;
                    return 2;
                }
            case 2:
                subNode.add(new ExpressionNode(level+1));
                return subNode.get(subNode.size()-1).matchNextToken(t);
            case 3:
                matched =true;
                return 2;
            default:
                break;
        }

        --cursor_t;//匹配失败，复位cursor_t
        return 0;
    }


    @Override
    public void generate(Generator g) throws Exception {
        NameTable nt = g.nameTable;
        icBlockList blocks = g.blocks;
        if (1 == subNode.size()) {
            subNode.get(0).generate(g);//only term,term result store in ecx
            blocks.write("move ebx ecx");
            blocks.write("push ebx");
        } else if (2 == subNode.size()) {
            subNode.get(1).generate(g);//add or sub ops
            subNode.get(0).generate(g);
            String operator = null;
            if (values.get(0).getContent().equals("+"))
                operator = "add ";
            else if (values.get(0).getContent().equals("-"))
                operator = "sub ";
            blocks.newBlock(values.get(0).getTokenLine());
            blocks.write("pop ebx");
            blocks.write(operator + "ecx ebx");
            blocks.write("push ecx");
        }
    }
}
