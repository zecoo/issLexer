package Nodes;

import ICGenerator.Generator;
import Token.Token;

import java.util.ArrayList;

/**
 * Created by yl on 2017/12/3.
 */
public class GlobalNode extends Node {
    public GlobalNode(int l)
    {
        name = new String();
        cursor_t =-1;
        cursor_val =0;
        subNode = new ArrayList<>();
        values = new ArrayList<Token>();
        level = l;
        type = "GlobalNode";
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
                subNode.add(new FunctionNode(level+1));
                return subNode.get(subNode.size()-1).matchNextToken(t);
            case 1:
                cursor_t =-1;
                return 2;
            default:
                break;
        }
        return 0;
    }

    @Override
    public void generate(Generator g) throws Exception {
        for(Node node :subNode)
            node.generate(g);
    }
}
