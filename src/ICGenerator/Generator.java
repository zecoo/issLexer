package ICGenerator;

import java.io.*;

/**
 * Created by yl on 2017/11/29.
 */
public class Generator {
    public icBlockList blocks;
    public NameTable nameTable;
    public FunctionArray functionArray;

    public FileOutputStream out;

    public Generator()
    {
        blocks = new icBlockList();
        nameTable = new NameTable();
        functionArray = new FunctionArray();
    }

    private Generator(icBlockList li,NameTable nt, FunctionArray fa)
    {
        blocks = li;
        nameTable = nt.copyTable();
        functionArray = fa;
    }
    public Generator copy()
    {
        Generator g = new Generator(blocks,nameTable,functionArray);
        return g;
    }

    public void write(String path) throws IOException
    {
        out = new FileOutputStream(new File(path));
        String br= "CMMBR\n";

        nameTable.print(out);
        out.write(br.getBytes());
        functionArray.print(out);
        out.write(br.getBytes());
        blocks.print(out);

        out.close();
    }
}
