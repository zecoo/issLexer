package Nodes;

import ICGenerator.*;
import Token.Token;

import java.util.ArrayList;

/**
 * Created by yl on 2017/12/3.
 */
public class FunctionNode extends Node{

    public FunctionNode(int l)
    {
        name = new String();
        cursor_t =-1;
        cursor_val =0;
        subNode = new ArrayList<>();
        values = new ArrayList<Token>();
        level = l;
        type = "FunctionNode";
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
                if(t.equals(6)||t.equals(7))
                {
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
                break;
            case 2:
                if(t.equals(16))
                {
                    values.add(t);
                    return 1;
                }
                else if(t.equals(18))//定义全局变量
                {
                    matched = true;
                    return 1;
                }
                break;
            case 3:
                if(t.equals(6)||t.equals(7))
                {
                    values.add(t);
                    return 1;
                }
                else if(t.equals(17))
                {
                    values.add(t);
                    cursor_t =10;
                    return 1;
                }
                break;
            case 4:
                if(t.equals(25))
                {
                    values.add(t);
                    return 1;
                }
                break;
            case 5:
                if(t.equals(27))//comma
                {
                    values.add(t);
                    cursor_t = 2;
                    return 1;
                }
                else if(t.equals(17))
                {
                    values.add(t);
                    cursor_t =10;
                    return 1;
                }
                break;

            case 11:
                subNode.add(new BlockNode(level+1));
                return subNode.get(subNode.size()-1).matchNextToken(t);
            case 12:
                matched =true;
                return 2;

            default:
                break;
        }

        --cursor_t;//匹配失败，复位cursor_t
        return 0;
    }


    @Override
    public void generate(Generator g) throws Exception {//只支持int

        NameTable nt = g.nameTable;
        icBlockList blocks = g.blocks;
        FunctionArray fa = g.functionArray;
        if(values.get(values.size()-1).equals(25))//全局变量声明
        {
           if(values.get(0).equals(6))
               nt.newGInt(values.get(1).getContent());
            else if(values.get(0).equals(7))
                nt.newGFloat(values.get(1).getContent());
            return;
        }

        int IntCurr = nt.getIntCursor();//function处，intcurr一定为0

        blocks.newBlock(values.get(0).getTokenLine());//函数的生成块
        nt.newFunc(values.get(1).getContent());//加入到名字表中
        int retT = values.get(0).getIdentifier();
        int startL = blocks.getCurrentIcLine();//获取函数的中间码的起点
        ArrayList<Integer> params = new ArrayList<>();
        ArrayList<String> pNames = new ArrayList<>();
        int intsum =0;
        int realSum = 0;
        for(int i=3;i<values.size()-1;i+=3)//避开反括号
        {
            params.add(new Integer(values.get(i).getIdentifier()));//添加参数类型
            pNames.add(values.get(i+1).getContent());//添加参数名称
            if(values.get(i).equals(6))
            {
                nt.set(values.get(i+1).getContent(),new INTTYPE().setOffset(-intsum-1));//向名字表中添加-4，-5，-6等来存取参数，int的-1，-2，-3用于存储ebp信息
                ++intsum;
            }
            if(values.get(i).equals(7))
            {
                nt.set(values.get(i+1).getContent(),new FLOATTYPE().setOffset(-realSum-1));
                ++realSum;
            }
        }
        Function f = new Function(startL,params,retT);
        fa.addFunc(f);

        subNode.get(0).generate(g);//生成block的对应的内容
        for(int i=0;i<pNames.size()-1;i++)
        {
            nt.erase(pNames.get(i));//函数编译完成后从名字表中取出所有的参数
        }

        for(int i=0;i<intsum;i++)
        {
            blocks.write("popi");
        }
        for (int i=0;i<realSum;i++)
        {
            blocks.write("popf");
        }
        String s="";
        s+="goto ";
       if(!values.get(1).getContent().equals("main"))
         blocks.write(s + nt.get(values.get(1).getContent()));//这个block是BLOCKNODE中未完成的块儿
        else
            nt.mainName = nt.get(values.get(1).getContent());//写入main函数名称
    }
}
