package Interpreter;

/**
 * Created by yl on 2017/12/2.
 */
public class block {
    public int codeLine;
    public int startIcLine;
    public int endIcLine;

    public block(int c,int s ,int e)
    {
        codeLine =c;
        startIcLine = s;
        endIcLine = e;
    }
}
