package Nodes;

import ICGenerator.Generator;
import ICGenerator.NameTable;
import ICGenerator.icBlockList;
import Token.Token;

import java.util.ArrayList;

/**
 * Created by yl on 2017/10/29.
 */
public class ConditionContentNode extends Node {

    public ConditionContentNode(int l)
    {
        name = new String();
        cursor_t =-1;
        cursor_val =0;
        subNode = new ArrayList<>();
        values = new ArrayList<Token>();
        level = l;
        type = "ConditionContentNode";
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
                if(t.equals(1))//if
                {
                    values.add(t);
                    return 1;
                }
                break;
            case 1:
                if(t.equals(16))//（
                {
                    values.add(t);
                    return 1;
                }
             break;
            case 2:
                subNode.add(new ConditionNode(level+1));//条件判断
                return subNode.get(subNode.size()-1).matchNextToken(t);
            case 3:
               if(t.equals(17))//）
               {
                   values.add(t);
                   return 1;
               }
               break;
            case 4:
                subNode.add(new BlockNode(level+1));
                return subNode.get(subNode.size()-1).matchNextToken(t);
            case 5:
               if(t.equals(2))//有else
               {
                   values.add(t);
                   return 1;
               }
               else//无else
               {
                   matched = true;//返回2，此结点终结，并要求传入者后退一次再次传入。
                   return 2;
               }
            case 6:
                subNode.add(new BlockNode(level+1));
                return subNode.get(subNode.size()-1).matchNextToken(t);
            case 7:
                matched = true;
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
        subNode.get(0).generate(g);//condition里比较操作一定要放在最后一个

        String w = "goto";

        blocks.newBlock(values.get(2).getTokenLine());//以condition后的反括号为起点
        blocks.write(w);
        blocks.setBackf();//此处设置之后，完成之后再设置就可以反填到当前语句的下下一句
        subNode.get(1).generate(g);//生成block

        if(subNode.size()==3)//if和else必须有block，等同于检测是否有else
        {
            blocks.newBlock(values.get(3).getTokenLine());//在else所在行开始新的代码
            blocks.finishBackf(1);//跳过下一句goto，来进入else
            blocks.write("goto");
            blocks.setBackf();
            subNode.get(2).generate(g);
            blocks.finishBackf(0);//执行下一行
        }
        else if(subNode.size()==2)
        {
            blocks.finishBackf(0);
        }
    }

}
