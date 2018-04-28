package ICGenerator;

import java.util.ArrayList;

/**
 * Created by yl on 2017/11/28.
 */
public class Function {
    int startLine;//起始的中间码行号，在写入前通过getCurrentLine获取并newFunc
    ArrayList<Integer> params;//参数类型检查，仅在编译过程中生效
    int retType;//返回值类型,用于编译器做类型匹配

    public Function(int s,ArrayList<Integer> p ,int r)
    {
        startLine =s;
        params = p;
        retType =r;
    }

    public boolean setStartLine(int s)
    {
        if(startLine ==0)
            startLine = s;
        else
            return false;
        return true;
    }

    public ArrayList<Integer> getParam()
    {
        return params;
    }

    public int getRetType()
    {
        return retType;
    }

    public String toString()
    {
        String s="";
        s+=startLine;
        s+="\n";
        return  s;
    }
}
