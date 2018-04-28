package Nodes;

import ICGenerator.Function;
import ICGenerator.Generator;
import ICGenerator.NameTable;
import ICGenerator.icBlockList;
import Token.Token;

import java.util.ArrayList;

/**
 * Created by yl on 2017/10/29.
 */
public class FactorNode extends Node {

    public FactorNode(int l)
    {
        name = new String();
        cursor_t =-1;
        cursor_val =0;
        subNode = new ArrayList<>();
        values = new ArrayList<Token>();
        level = l;
        type = "FactorNode";
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
                if(t.equals(23)||t.equals(24))
                {
                    values.add(t);
                    matched =true;
                    return 1;
                }
                else if(t.equals(25))
                {
                    values.add(t);
                    return 1;
                }
                break;
            case 1:
                if(t.equals(21)) {
                    values.add(t);
                    return 1;
                }
                else if(t.equals(16))
                {
                    values.add(t);
                    cursor_t = 3;
                    return 1;
                }
                else//int/real/identi匹配
                {
                    matched = true;
                    return 2;
                }
            case 2:
                if(t.equals(23) ||t.equals(25))
                {
                    subNode.add(new ExpressionNode(level + 1));
                    return subNode.get(subNode.size()-1).matchNextToken(t);
                }
                throw new Exception("wrong expression at line:" + t.getTokenLine());
            case 3:
                if( t.equals(22))
                {
                    values.add(t);
                    matched = true;
                    return 1;
                }//数组匹配完成。

                //function call
            case 4:
                if(t.equals(17))//反括号结束，没有参数
                {
                    values.add(t);
                    matched = true;
                    return 1;
                }
                else if(t.equals(23) || t.equals(24) ||t.equals(25))
                {
                    subNode.add(new ExpressionNode(level + 1));//expression node会匹配左括号
                    return subNode.get(subNode.size()-1).matchNextToken(t);
                }
                throw new Exception("wrong expression at line:" + t.getTokenLine());
            case 5:
                if(t.equals(27))
                {
                    values.add(t);
                    cursor_t = 3;//返回接受下一个参数
                    return 1;
                }
                else if(t.equals(17))
                {
                    values.add(t);
                    matched = true;
                    return 1;
                }
                break;
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
        if(!values.get(values.size() - 1).equals(17)) {//如果不是反括号
            for (Node node : subNode) {
                node.generate(g);
            }//对子节点求值,函数则在下方单独处理
        }
        if (values.get(values.size() - 1).equals(17)){//函数调用
            if (values.get(0).equals(25) && values.get(0).getContent().equals(nt.get(values.get(0).getContent()))) {
                throw new Exception("undefined Function: " + values.get(0).getContent() + " at line:" + values.get(0).getTokenLine());
            }//函数未定义
            else if(!nt.get(values.get(0).getContent()).contains("func_"))
            {
                throw new Exception("Not a Function: " + values.get(0).getContent() + " at line:" + values.get(0).getTokenLine());
            }
            blocks.newBlock(values.get(0).getTokenLine());
            //to do: function call
            // result should be moved to EAX，params in bx，should calculate and store in order
            Function f = g.functionArray.getFunc(nt.get(values.get(0).getContent()));
            if(f==null)
                throw new Exception("identifier is not a function:" +values.get(0).getContent() + " at line:" + values.get(0).getTokenLine());
            if(f.getParam().size()!=subNode.size())//调用参数的数目和函数声明的数目不相等
                throw new Exception("params Number wrong:"+values.get(0).getContent() + " at line:" + values.get(0).getTokenLine());

            for(int i=subNode.size()-1;i>=0;i--)//为参数开辟空间
            {
                subNode.get(i).generate(g);
                blocks.write("pop ebx");
                if(f.getParam().get(i).equals(6))
                    blocks.write("new i_-1 ebx");//for new，-1无实际含义
                else if(f.getParam().get(i).equals(7))
                    blocks.write("new f_-1 ebx");
            }//右至左写入参数到对应的运行栈上
            blocks.write("call "+nt.get(values.get(0).getContent()));
            blocks.write("move eax "+ nt.get(values.get(0).getContent()) );
        }
        else if (values.get(values.size() - 1).equals(22))//数组
        {
            if (values.get(0).equals(25) && values.get(0).getContent().equals(nt.get(values.get(0).getContent()))) {
                throw new Exception("undefined Array: " + values.get(0).getContent() + " at line:" + values.get(0).getTokenLine());
            }
            else if(!nt.get(values.get(0).getContent()).contains("ai_")&&!nt.get(values.get(0).getContent()).contains("af_"))
            {
                throw new Exception("Not a Array: " + values.get(0).getContent() + " at line:" + values.get(0).getTokenLine());
            }
            String arr =  nt.get(values.get(0).getContent());
            blocks.newBlock(values.get(0).getTokenLine());
            blocks.write("pop ebx");
            arr +="_ebx";
            blocks.write("move eax " + arr);//取出数组的值
        }
        else if(values.get(0).equals(25)&&values.size()==1){//之外的identifier
            if (values.get(0).equals(25) && values.get(0).getContent().equals(nt.get(values.get(0).getContent()))) {
                throw new Exception("undefined identifier: " + values.get(0).getContent() + " at line:" + values.get(0).getTokenLine());
            }
            else if(nt.get(values.get(0).getContent()).contains("ai_")||nt.get(values.get(0).getContent()).contains("af_")||nt.get(values.get(0).getContent()).contains("func_"))
                throw new Exception("Not A Suitable Identifier:: " + values.get(0).getContent() + " at line:" + values.get(0).getTokenLine());
            blocks.newBlock(values.get(0).getTokenLine());
            blocks.write("move " + "eax " + nt.get(values.get(0).getContent()));
        }
        else if((values.get(0).equals(23)||values.get(0).equals(24))&&values.size()==1)//数字
        {
            blocks.newBlock(values.get(0).getTokenLine());
            blocks.write("move " + "eax " + nt.get(values.get(0).getContent()));
        }
    }


}
