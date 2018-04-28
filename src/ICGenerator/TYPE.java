package ICGenerator;

/**
 * Created by yl on 2017/11/28.
 */
public class TYPE {
    String type = "";
    int offset = 0;
    public  String getSTR()
    {
        return type+offset;
    }
    public TYPE setOffset(int o){
        offset = o;
        return this;
    }
}
