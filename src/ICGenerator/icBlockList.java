package ICGenerator;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by yl on 2017/11/29.
 */
public class icBlockList {
    ArrayList<icBlock> BlockList;
    int currentIcLine=0;//从0开始，下一次调用write函数写入的就是currentICLine行的
    ArrayList<Integer> backF;

    public void newBlock(int codeLine)//each code line should have a newBlock
    {
        if(BlockList==null)
            BlockList = new ArrayList<>();
        if(BlockList.size()>0)
            BlockList.get(BlockList.size()-1).setEndIcLine(currentIcLine);
        BlockList.add(new icBlock(codeLine,currentIcLine));
    }

    public void write(String s)
    {
        BlockList.get(BlockList.size()-1).write(s);
        ++currentIcLine;//iccode start from 0
    }

    public int getCurrentIcLine()//这个是当前待写仍未写入的行号
    {
        return currentIcLine;
    }

    public void print(FileOutputStream fs) throws IOException {
        for(int i=0;i<BlockList.size();i++)
        {
            fs.write(BlockList.get(i).toString().getBytes());
        }
        String br = "CMMBR\n";
        fs.write(br.getBytes());
        for(int i=0;i<BlockList.size();i++)
        {
            BlockList.get(i).print(fs);
        }
    }

    public void  setBackf()//写入“goto” 之后不需要加空格，调用此方法则该行被标记
    {
        if(backF == null)
            backF = new ArrayList<>();
        backF.add(BlockList.size()-1);//记录下需要反填的block序号
        BlockList.get(BlockList.size()-1).setBlockBackf();//为该block设置反填
    }

    public void finishBackf(int c)//在if/else/while的结尾调用这个finish就会反填反括号下c+1行内容
    {
        String s = "";
        s+= currentIcLine+c;
        if(backF==null)
            return;
        for(Integer i: backF)
        {
            BlockList.get(i).finishBackf(s);
        }
        backF = null;
    }
}
