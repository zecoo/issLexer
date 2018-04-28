package ICGenerator;

/**
 * Created by yl on 2017/11/28.
 */
public class ARRAYTYPE extends TYPE{
    int arrayType;

    public ARRAYTYPE setArrayType(int arrt)
    {
        arrayType = arrt;
        return this;
    }

    public String getSTR()
    {
        String str = "";
        if(arrayType == 6)
        {
            str += "ai_";
            str += offset;
        }
        else if(arrayType == 7)
        {
            str += "af_";
            str += offset;
        }
        else
            return null;
        return str;
    }

    public ARRAYTYPE()
    {
    }
    
}
