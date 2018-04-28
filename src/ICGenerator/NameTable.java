package ICGenerator;

import org.omg.CosNaming.NamingContextExtPackage.StringNameHelper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by yl on 2017/11/27.
 */
public class NameTable {
    //a hashmap of <string,arraylist int> //store the offset
    //string get(string)   pop(string)//at the end of generate it should call pop(s) all it new
    //newInt(s), newFloat(s), newArray(s)(will call N newInt) u should pass type and size,and also write size in the iccode
    // the same name has the same offset-chain to store the current meaning of the name
    private HashMap<String,ArrayList<TYPE>> nameTable;

    public String mainName="";
    int IntCursor =0;
    int FloatCursor =0;
    int ArrayCursor =0;
    int FuncCursor =0;
    int GIntCursor=0;
    int GFloatCursor =0;

    private NameTable(int intc,int floatc,int arrayc,int funcc,HashMap<String,ArrayList<TYPE>> t)
    {
        nameTable = (HashMap<String, ArrayList<TYPE>>)t .clone();
        for(String s: nameTable.keySet())
        {
            ArrayList<TYPE> li = nameTable.get(s);
            for(TYPE type: li )
            {
                if(type.getClass()==INTTYPE.class)
                    type.offset -= (intc+3);//int 数组需要多三个空间来存放ebp
                else if(type.getClass()==FLOATTYPE.class)
                    type.offset -= (floatc);
                else if(type.getClass()==ARRAYTYPE.class)
                    type.offset -= (arrayc);
            }
        }
        FuncCursor = funcc;
    }

    public NameTable()
    {
        nameTable = new HashMap<>();
    }

    public void set(String name,TYPE t)
    {
        if (!nameTable.containsKey(name)) {
            ArrayList<TYPE> newList = new ArrayList<>();
            newList.add(t);
            nameTable.put(name, newList);
        }
        else {
            ArrayList<TYPE> sList = nameTable.get(name);
            sList.add(t);
        }
    }

    public String get(String s)
    {

        ArrayList<TYPE> sList = nameTable.get(s);
        if(sList==null||sList.size()==0)
            return s;
        return sList.get(sList.size()-1).getSTR();
    }

    public void erase(String s)//erase will not collect the garbage
    {
        ArrayList<TYPE> sList = nameTable.get(s);
        if(sList==null)
            return;
        sList.remove(sList.size()-1);
    }

    //for func params to allocate
    public void allocInt(int size)
    {
        IntCursor +=size;
    }

    public void allocFloat(int size)
    {
        FloatCursor += size;
    }

    public void popInt()
    {
        IntCursor--;
    }

    public void popFloat()
    {
        FloatCursor--;
    }

    public String newInt(String s) {
        String res = "i_" + IntCursor;
        if (!nameTable.containsKey(s)) {
            ArrayList<TYPE> newList = new ArrayList<>();
            newList.add(new INTTYPE().setOffset(IntCursor));
            nameTable.put(s, newList);
        }
        else {
            ArrayList<TYPE> sList = nameTable.get(s);
            sList.add(new INTTYPE().setOffset(IntCursor));
        }
        ++IntCursor;
        return  res;
    }

    public String newFloat(String s) {
        String res = "f_" + FloatCursor;
        if (!nameTable.containsKey(s)) {
            ArrayList<TYPE> newList = new ArrayList<>();
            newList.add( new FLOATTYPE().setOffset(FloatCursor));
            nameTable.put(s, newList);
        }
        else {
            ArrayList<TYPE> sList = nameTable.get(s);
            sList.add(new FLOATTYPE().setOffset(FloatCursor));
        }
        ++FloatCursor;
        return  res;
    }

    public String newArray(String s,int type) {
        ARRAYTYPE art = new ARRAYTYPE().setArrayType(type);

        if (!nameTable.containsKey(s)) {
            ArrayList<TYPE> newList = new ArrayList<>();
            newList.add(art.setOffset(ArrayCursor) );
            nameTable.put(s, newList);
        }
        else {
            ArrayList<TYPE> sList = nameTable.get(s);
            sList.add(art.setOffset(ArrayCursor));
        }

        ++ArrayCursor;
        return  art.getSTR();
    }

    //newFunc调用之后还要new 出func然后再添加到FunctionArray中，
    public String newFunc(String s)
    {
        String res = "func_" + FuncCursor;
        if (!nameTable.containsKey(s)) {
            ArrayList<TYPE> newList = new ArrayList<>();
            newList.add(new FUNCTYPE().setOffset(FuncCursor));
            nameTable.put(s, newList);
        }
        else {
            ArrayList<TYPE> sList = nameTable.get(s);
            sList.add(new FUNCTYPE().setOffset(FuncCursor));
        }

        ++FuncCursor;
        return  res;
    }

    public String newGInt(String s)
    {
        String res = "gi_" + GIntCursor;
        if (!nameTable.containsKey(s)) {
            ArrayList<TYPE> newList = new ArrayList<>();
            newList.add(new GINTTYPE().setOffset(GIntCursor));
            nameTable.put(s, newList);
        }
        else {
            ArrayList<TYPE> sList = nameTable.get(s);
            sList.add(new GINTTYPE().setOffset(GIntCursor));
        }

        ++GIntCursor;
        return  res;
    }

    public String newGFloat(String s)
    {
        String res = "gf_" + GFloatCursor;
        if (!nameTable.containsKey(s)) {
            ArrayList<TYPE> newList = new ArrayList<>();
            newList.add(new GFLOATTYPE().setOffset(GFloatCursor));
            nameTable.put(s, newList);
        }
        else {
            ArrayList<TYPE> sList = nameTable.get(s);
            sList.add(new GFLOATTYPE().setOffset(GFloatCursor));
        }

        ++GFloatCursor;
        return  res;
    }

    public NameTable copyTable()//copy and set EsP for block,and pass the new nametable to its subNode,after it should generate reset the ebp iccode
    {
        NameTable newT = new NameTable(IntCursor,FloatCursor,ArrayCursor,FuncCursor,nameTable);//copy的时候不需要Gint和Gfloat
        return  newT;
    }

    public ArrayList<String> getHint(String s)
    {
        ArrayList<String> res = new ArrayList<>();
        for(String str : nameTable.keySet())
        {
            if(str.contains(s))
                res.add(new String(str));
        }
        return res;
    }


    public void print(FileOutputStream fs) throws IOException//print the Gint and the Gfloat size;
    {
        String s="";
        s+=mainName;
        s+="\n";
        s+=(GIntCursor);
        s+="_";
        s+=(GFloatCursor);
        s+="\n";

        fs.write(s.getBytes());
    }

    //use to reset the ebp for blockNode,iebp will add this value
    public int getIntCursor()
    {
        return IntCursor;
    }

    public int getFloatCursor()
    {
        return  FloatCursor;
    }

    public int getArrayCursor()
    {
        return ArrayCursor;
    }

    public void reset()//blockNode 需要调用reset回到正确值
    {
        for(String s: nameTable.keySet())
        {
            ArrayList<TYPE> li = nameTable.get(s);
            for(TYPE type: li )
            {
                if(type.getClass()==INTTYPE.class)
                    type.offset += (IntCursor+3);
                else if(type.getClass()==FLOATTYPE.class)
                    type.offset += (FloatCursor);
                else if(type.getClass()==ARRAYTYPE.class)
                    type.offset += (ArrayCursor);
            }
        }
    }
}
