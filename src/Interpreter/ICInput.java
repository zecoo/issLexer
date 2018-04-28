package Interpreter;

import ICGenerator.Function;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by yl on 2017/12/2.
 */
public class ICInput {
    public int GIntSize;
    public int GFloatSize;
    public ArrayList<RunTimeFunction> funcs;
    public ArrayList<block> block_list;
    public ArrayList<String> ICCode;
    String mainName="";

    public void init(String path) throws Exception {
        File file = new File(path);
        BufferedReader bf = new BufferedReader(new FileReader(file));
        String s;
        s=bf.readLine();//initMain
        initMain(s);
        s = bf.readLine();//read the size of Gint and GFloat
        initGVariables(s);

        s = bf.readLine();
        if(!s.equals("CMMBR"))
            throw new Exception("file has been corrupted, format wrong");

        while ((s=bf.readLine())!=null)//to next cmmbr
        {
            if(s.equals("CMMBR"))
                break;
            initFuncs(s);
        }

        while ((s=bf.readLine())!=null)//to the next br
        {
            if(s.equals("CMMBR"))
                break;
            initBlockList(s);
        }

        while ((s=bf.readLine())!=null)//to the next br
        {
            if(s.equals("CMMBR"))
                break;
            initICCode(s);
        }

    }

    private int initMain(String s)
    {
        mainName = s;
        return 1;
    }

    private int initGVariables(String s)
    {
        String[] vars = s.split("_");
        int Gintsize = Integer.parseInt(vars[0]);
        int Gfloatsize = Integer.parseInt(vars[1]);

        if(Gintsize>=0&&Gintsize<=256&&Gfloatsize>=0&&Gfloatsize<=256)
        {
            GFloatSize = Gfloatsize;
            GIntSize = Gintsize;
        }
        else 
            return 0;
        
        return 1;
    }

    private int initFuncs(String s)
    {
        if(funcs ==null)
            funcs = new ArrayList<>();

        Integer sLine = Integer.parseInt(s);
        if(sLine>=0)
            funcs.add(new RunTimeFunction(sLine));
        else
            return 0;

        return 1;
    }

    private int initBlockList(String s)
    {
        String[] blockInfo = s.split("_");
        Integer cLine = Integer.parseInt(blockInfo[0]);
        Integer sLine = Integer.parseInt(blockInfo[1]);
        Integer eLine = Integer.parseInt(blockInfo[2]);

        if(block_list==null)
            block_list = new ArrayList<>();

        if(cLine>0&&sLine>0&&eLine>0)
            block_list.add(new block(cLine,sLine,eLine));
        else
            return 0;

        return 1;
    }

    private int initICCode(String s)
    {
        if(ICCode==null)
            ICCode = new ArrayList<>();

        ICCode.add(s);
        return 1;
    }

}
