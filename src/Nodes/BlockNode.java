package Nodes;

import ICGenerator.Generator;
import ICGenerator.NameTable;
import ICGenerator.icBlockList;
import Token.Token;

import java.util.ArrayList;

/**
 * Created by yl on 2017/10/29.
 */
public class BlockNode extends Node {

    private int cursor_sub=0;//当产生式中的非终结符不确定时

    public BlockNode(int l)
    {
        name = new String();
        cursor_t =-1;
        cursor_val =0;
        subNode = new ArrayList<>();
        values = new ArrayList<Token>();
        level = l;
        type = "BlockNode";
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
                if(t.equals(19))//匹配大括号
                {
                    values.add(t);
                    return 1;
                }
                break;
            case 1:
                if(t.equals(19))//如果仍是大括号，则新的Block
                {
                   subNode.add(new BlockNode(level+1));
                    return subNode.get(subNode.size()-1).matchNextToken(t);
                }
                else
                {
                    subNode.add(new StatementNode(level+1));//否则为statement
                    return subNode.get(subNode.size()-1).matchNextToken(t);
                }

            case 2:
                if(t.equals(20))
                {//如果是反括号，则此block完成
                    values.add(t);
                    matched =true;
                    return 1;
                }
                else
                {//如果不是反括号，则视为下一个产生式的内容,此时返回2，重新读入
                    ++cursor_sub;
                    cursor_t =0;//go back
                    return 2;
                }

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

        Generator gt = g.copy();

        blocks.newBlock( values.get(0).getTokenLine());
        blocks.write("setEBP");


        for(Node n : subNode)
        {
            n.generate(gt);
        }

        nt.reset();
        blocks.newBlock(values.get(1).getTokenLine());
        blocks.write("resEBP");
    }


}
