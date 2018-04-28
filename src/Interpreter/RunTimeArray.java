package Interpreter;

import java.util.ArrayList;

/**
 * Created by yl on 2017/12/2.
 */
public class RunTimeArray {
    ArrayList<Integer> ints;
    ArrayList<Float> floats;
    int size;

    public RunTimeArray(int type,int s)
    {
        size =s;
        if(type == 6) {
            ints = new ArrayList<>(size);
            for(int i=0;i<size;i++)
            {
                ints.add(0);
            }
        }else if(type == 7) {
            floats = new ArrayList<>(size);
            for(int i=0;i<size;i++)
            {
                floats.add(new Float(0));
            }
        }
    }

    public Integer getInt(int index) throws Exception {
        if(ints==null)
            return null;
        if(index>ints.size())
            throw new Exception("CMMARRAY out of bound");
        return ints.get(index);
    }

    public Float getFloat(int index) throws Exception {
        if(floats==null)
            return null;
        if(index>floats.size())
            throw new Exception("CMMARRAY out of bound");
        return floats.get(index);
    }

    public void setInt(int index, Integer i) throws Exception {
        if(index>ints.size())
            throw new Exception("CMMARRAY out of bound");
        ints.set(index,i);
    }

    public void setFloat(int index, Float f) throws Exception {
        if(index>ints.size())
            throw new Exception("CMMARRAY out of bound");
        floats.set(index,f);
    }
}
