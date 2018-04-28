package ICGenerator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by yl on 2017/11/29.
 */
public class icBlock {
    int codeLine;
    int startIcLine;
    int endIcLine;
    ArrayList<String> content;
    ArrayList<Integer> backF;

    icBlock(int cl,int sLine)
    {
        startIcLine = sLine;
        codeLine = cl;
    }

    public void setEndIcLine(int end)
    {
        endIcLine = end;
    }

    public void write(String s)
    {
        if(content==null)
            content = new ArrayList<>();

        content.add(s);
    }

    public String toString()
    {
        String s ="";
        s+= codeLine;
        s+="_";
        s+=startIcLine;
        s+="_";
        s+=endIcLine;
        s+="\n";
        return s;
    }

    public void print(FileOutputStream fs) throws IOException {
        if(content==null)
            return;
        for(int i=0;i<content.size();i++)
        {
            fs.write((content.get(i)+"\n").getBytes());
        }
    }

    public void setBlockBackf()
    {
        if(backF==null)
            backF = new ArrayList<>();
        backF.add(content.size()-1);//记录下需要反填的位置即当前最后一个content，可能一个block内有多个
    }

    public void finishBackf(String f)
    {
        if(backF == null)
            return;
        for(Integer i:backF)
        {
            content.set(i,content.get(i)+" "+f);//goto加上反填值
        }
        backF = null;
    }
}
