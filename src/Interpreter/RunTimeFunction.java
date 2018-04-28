package Interpreter;

/**
 * Created by yl on 2017/12/2.
 */
public class RunTimeFunction {
    int startLine;
    int backAddr;
    Float result;

    public RunTimeFunction(Integer sLine)
    {
        startLine = sLine;
    }

    public Float getResult()
    {
        return result;
    }

    public void setResult(Float o)
    {
        result = o;
    }

    public int getStartLine()
    {
        return startLine;
    }

    public void setBackLine(int b)
    {
        backAddr = b;
    }

    public int getBackAddr()
    {
        return backAddr;
    }
}
