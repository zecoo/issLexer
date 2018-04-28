package Token;

/**
 * Created by yl on 2017/10/26.
 */
public class Token {
    private String Content;
    private int identifier;
    private int line;

    public Token(String s,int l)
    {
        Content = s;
        line =l;
    }

    public boolean setIdentifier(int i)
    {
        identifier = i;
        return true;
    }

public String getContent()
{
    return Content;
}

public int getIdentifier()
{
    return identifier;
}

    public int getTokenLine()
    {
        return line;
    }

    public boolean equals(String s)
    {
        return Content.equals(s);
    }

    public boolean equals(int i)
    {
        return i==identifier;
    }
}
