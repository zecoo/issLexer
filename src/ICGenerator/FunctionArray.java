package ICGenerator;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by yl on 2017/11/28.
 */
public class FunctionArray {
    ArrayList<Function> funcs;

    public FunctionArray()
    {
        funcs = new ArrayList<Function>();
    }

    public void addFunc(Function f)
    {
        funcs.add(f);
    }

    public Function getFunc(String name) {
        String[] s = name.split("_");
        if(!s[0].equals("func"))
            return null;
        else
            return funcs.get(Integer.parseInt(s[1]));
    }//use to match the params

    public Function getCurrentFunc() {
        if(funcs==null||funcs.size()==0)
            return null;
        return funcs.get(funcs.size()-1);
    }

    public void print(FileOutputStream fs) throws IOException//print the startline and the retType foreach in the array
    {
        for(int i=0;i<funcs.size();i++)
        {
            fs.write(funcs.get(i).toString().getBytes());
        }
    }
}
