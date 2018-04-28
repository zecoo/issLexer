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
public class StatementNode extends Node {

    public StatementNode(int l)
    {
        name = new String();
        cursor_t =-1;
        cursor_val =0;
        subNode = new ArrayList<>();
        values = new ArrayList<Token>();
        level = l;
        type = "StatementNode";
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
                if(t.equals(6)||t.equals(7))//声明变量
                {
                    subNode.add(new VariableNode(level+1));
                    return subNode.get(subNode.size()-1).matchNextToken(t);
                }
                else if(t.equals(4))//read
                {
                    values.add(t);
                    cursor_t = 1;
                    return 1;
                }
                else if(t.equals(5))//write
                {
                    values.add(t);
                    cursor_t = 5;
                    return 1;
                }
                else if(t.equals(25))//赋值语句
                {
                    values.add(t);
                    cursor_t = 9;
                    return 1;
                }
                else if(t.equals(3))//while
                {
                    values.add(t);
                    cursor_t = 12;
                    return 1;
                }
                else if(t.equals(1))//if
                {
                    cursor_t = 17;
                    return 2;
                }
                else if(t.equals(26))
                {
                    values.add(t);
                    cursor_t = 24;
                    return 1;
                }
                break;
            case 1:
                matched = true;
                return 2;

            //
            //read
            case 2:
                if(t.equals(16))
                {
                    values.add(t);
                    return 1;
                }
                break;
            case 3:
                if(t.equals(25))
                {
                    values.add(t);
                    return 1;
                }
                break;
            case 4:
                if(t.equals(17))
                {
                    values.add(t);
                    return 1;
                }
                break;
            case 5:
                if(t.equals(18))
                {
                    matched = true;
                    return 1;
                }
                break;

            //
            //write
            case 6:
                if(t.equals(16))
                {
                    values.add(t);
                    return 1;
                }
                break;
            case 7:
                if(t.equals(25))
                {
                    values.add(t);
                    return 1;
                }
                break;
            case 8:
                if(t.equals(17))
                {
                    values.add(t);
                    return 1;
                }
                break;
            case 9:
                if(t.equals(18))
                {
                    matched = true;
                    return 1;
                }
                break;

            //
            //赋值语句
            case 10:
                if(t.equals(12))
                {
                    values.add(t);
                    return 1;
                }
                else if(t.equals(21))
                {
                    values.add(t);
                    cursor_t = 19;
                    return 1;
                }
                break;
            case 11:
                subNode.add(new ExpressionNode(level+1));
                return subNode.get(subNode.size()-1).matchNextToken(t);
            case 12:
                if(t.equals(18))
                {
                    matched = true;
                    return 1;
                }
                break;

            //
            //while
            case 13:
                if(t.equals(16))
                {
                    values.add(t);
                    return 1;
                }
                break;
            case 14:
                subNode.add(new ConditionNode(level+1));
                return subNode.get(subNode.size()-1).matchNextToken(t);
            case 15:
                if(t.equals(17))
                {
                    values.add(t);
                    return 1;
                }
                break;
            case 16:
                subNode.add(new BlockNode(level+1));
                return subNode.get(subNode.size()-1).matchNextToken(t);
            case 17:
                matched = true;
                return 2;//如果上一个是一个node结束，并且结尾没有非终结符，则读入的内容应该是属于下一个产生式的，返回2以再次读入并归结这条产生式

            //
            //if
            case 18:
                subNode.add(new ConditionContentNode(level+1));
                return subNode.get(subNode.size()-1).matchNextToken(t);
            case 19:
                matched = true;
                return 2;

            //赋值语句中的数组
            case 20:
                subNode.add(new ExpressionNode(level+1));
                return subNode.get(subNode.size()-1).matchNextToken(t);
            case 21:
                if(t.equals(22))
                {
                    values.add(t);
                    return 1;
                }
                break;
            case 22:
                if(t.equals(12)) {
                    values.add(t);
                    return 1;
                }
                break;
            case 23:
                subNode.add(new ExpressionNode(level+1));
                return subNode.get(subNode.size()-1).matchNextToken(t);
            case 24:
                if(t.equals(18))
                {
                    matched = true;
                    return 1;
                }
                break;

            //return
            case 25:
                if(t.equals(25)||t.equals(24)||t.equals(23))
                {
                    values.add(t);
                    return 1;
                }
                else if(t.equals(18))
                    {
                        matched =true;
                        return 1;
                    }
                break;
            case 26:
                if(t.equals(18))
                {
                    matched = true;
                    return 1;
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

        if(values.size()==0)//直接交给子节点生成
            subNode.get(0).generate(g);
        else if(values.get(0).equals(4)||values.get(0).equals(5))//read,write
        {
            String varName  = nt.get(values.get(2).getContent());
            if(varName.equals(values.get(2).getContent()))
                throw new Exception("identifier not defined: " + values.get(2).getContent() +"at line:" + values.get(2).getTokenLine());
            blocks.newBlock(values.get(0).getTokenLine());
            if(values.get(0).equals(4))
                blocks.write("read "+varName);
            else if(values.get(0).equals(5))
                blocks.write("prt "+varName);
        }
        else if(values.get(0).equals(3))//while
        {
            blocks.newBlock(values.get(0).getTokenLine());
            int startLine = blocks.getCurrentIcLine();//goto的值为ConditionNode生成的比较语句，每次返回都会返回到这些执行比较操作的语句
            subNode.get(0).generate(g);//生成ConditionNode
            blocks.write("goto");//反填的goto不加空格
            blocks.setBackf();
            subNode.get(1).generate(g);
            blocks.finishBackf(1);//当执行上一个goto的时候，下面的goto返回比较语句就会被跳过
            blocks.write("goto " + startLine);//用于返回
        }
        else if(values.get(0).equals(25))//赋值语句
        {
            if(values.get(1).equals(12))//非数组赋值
            {
                String varName  = nt.get(values.get(0).getContent());
                if(varName.equals(values.get(0).getContent()))
                    throw new Exception("identifier not defined: " + values.get(0).getContent() +"at line:" + values.get(0).getTokenLine());
                else if(varName.contains("ai_")||varName.contains("af_")||varName.contains("func_"))
                    throw new Exception("Not A Suitable Identifier: " + values.get(0).getContent() +"at line:" + values.get(0).getTokenLine());
                subNode.get(0).generate(g);//生成exp的右值内容
                blocks.newBlock(values.get(0).getTokenLine());
                blocks.write("pop ebx");
                blocks.write("move "+varName+" ebx");
            }
            else if(values.get(3).equals(12))
            {
                String varName  = nt.get(values.get(0).getContent());
                if(varName.equals(values.get(0).getContent()))
                    throw new Exception("Array not Defined: " + values.get(0).getContent() +"at line:" + values.get(0).getTokenLine());
                else if(!(varName.contains("af_")||varName.contains("ai_")))
                {
                    throw new Exception("Not a Array: " + values.get(0).getContent() +"at line:" + values.get(0).getTokenLine());
                }
                subNode.get(1).generate(g);
                subNode.get(0).generate(g);//从右到左生成
                blocks.write("pop ebx");
                blocks.write("pop eax");
                varName +="_eax";
                blocks.write("move "+varName+" ebx");//从ebx写入数组的值
            }
        }else if(values.get(0).equals(26))//return
        {
            Function f = g.functionArray.getCurrentFunc();
            int type = f.getRetType();
            String varName =nt.get(values.get(1).getContent());
            if(values.get(1).equals(23))
            {
                if(type!=23)
                    throw new Exception("Wrong ReturnType: at line " +values.get(0).getTokenLine());
            }
            else if(values.get(1).equals(24))
            {
                if(type!=24)
                    throw new Exception("Wrong ReturnType: at line " +values.get(0).getTokenLine());
            }
            else if(values.get(1).equals(25))
            {
                if(type == 23) {
                    if (!varName.contains("i") || varName.contains("ai"))
                        throw new Exception("Wrong ReturnType: at line " + values.get(0).getTokenLine());
                }
                else if(type == 24)
                {
                    if (!varName.contains("f") || varName.contains("af"))
                        throw new Exception("Wrong ReturnType: at line " + values.get(0).getTokenLine());
                }
            }


            blocks.newBlock(values.get(0).getTokenLine());
            blocks.write("move CF "+varName);
        }
    }


}
