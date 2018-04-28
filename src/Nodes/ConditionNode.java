package Nodes;

import ICGenerator.Generator;
import ICGenerator.NameTable;
import ICGenerator.icBlockList;
import Token.Token;

import java.util.ArrayList;

/**
 * Created by yl on 2017/10/29.
 */
public class ConditionNode extends Node {

    public ConditionNode(int l)
    {
        name = new String();
        cursor_t =-1;
        cursor_val =0;
        subNode = new ArrayList<>();
        values = new ArrayList<Token>();
        level = l;
        type = "ConditionNode";
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
                subNode.add(new ExpressionNode(level+1));
                return subNode.get(subNode.size()-1).matchNextToken(t);
            case 1:
                if(t.equals(13)||t.equals(14)||t.equals(15))
                {
                    values.add(t);
                    return 1;
                }
                break;
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

        subNode.get(1).generate(g);//先生成右侧exp，结果一定被写在栈顶
        subNode.get(0).generate(g);//再生成左侧的exp，结果也被push到栈顶

        blocks.newBlock(values.get(0).getTokenLine());
        blocks.write("pop eax");//左侧pop到eax中
        blocks.write("pop ebx");//右侧pop到ebx
        blocks.write(values.get(0).getContent() + " eax ebx");
    }


}
