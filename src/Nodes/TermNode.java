package Nodes;

import ICGenerator.Generator;
import ICGenerator.NameTable;
import ICGenerator.icBlockList;
import Token.Token;

import java.util.ArrayList;

/**
 * Created by yl on 2017/10/29.
 */
public class TermNode extends Node {

    public TermNode(int l)
    {
        name = new String();
        cursor_t =-1;
        cursor_val =0;
        subNode = new ArrayList<>();
        values = new ArrayList<Token>();
        level = l;
        type = "TermNode";
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
                subNode.add(new FactorNode(level+1));
                return subNode.get(subNode.size()-1).matchNextToken(t);
            case 1:
                if(t.equals(10)||t.equals(11))
                {
                    values.add(t);
                    return 1;
                }
                else
                {
                    matched = true;//返回false，此结点终结，并要求传入者后退一次再次传入。
                    return 2;
                }
            case 2:
                subNode.add(new TermNode(level+1));
                return subNode.get(subNode.size()-1).matchNextToken(t);
            case 3:
                matched =true;//匹配完成之后，再次传入token进入此分支，要求外部后退重新传给别的节点
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
        if(1 == subNode.size()){
            subNode.get(0).generate(g);//only a factor
            blocks.write("move ecx eax");//result store in ecx
        }
        else if(2 == subNode.size()){
            subNode.get(1).generate(g);
            subNode.get(0).generate(g);
            String operator = null;
            if(values.get(0).getContent().equals("/"))//factor and term
                operator = "div ";
            else if(values.get(0).getContent().equals("*"))
                operator = "mul ";
            blocks.newBlock(values.get(0).getTokenLine());
            blocks.write(operator + "ecx eax");
        }
    }
}
