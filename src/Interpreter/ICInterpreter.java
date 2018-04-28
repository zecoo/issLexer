package Interpreter;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by yl on 2017/12/2.
 */
public class ICInterpreter {
    ICInput input;
    ArrayList<Integer> _Int;
    ArrayList<Float> _Float;
    ArrayList<RunTimeArray> _Array;
    ArrayList<Integer> _GInt;
    ArrayList<Float> _GFloat;
    ArrayList<RunTimeFunction> functions;
    ArrayList<block> blocks;
    ArrayList<String> codes;
    String currentFunction;

    int CodeCursor=-1;
    int IntEBP=0;
    int FloatEBP=0;
    int ArrayEBP=0;
    Float EAX = new Float(0.0);
    Float EBX = new Float(0.0);
    Float ECX = new Float(0.0);
    String outPut = new String("");
    String inPut;

    public ICInterpreter(String path) throws Exception {
        input = new ICInput();
        input.init(path);

        _Int = new ArrayList<>();
        _Float = new ArrayList<>();
        _Array = new ArrayList<>();
        _GInt = new ArrayList<>(input.GIntSize);
        _GFloat = new ArrayList<>(input.GFloatSize);
        currentFunction = input.mainName;

        for(int i=0;i<input.GIntSize;i++)
        {
            _GInt.add(0);
        }
        for(int i=0;i<input.GFloatSize;i++)
        {
            _GFloat.add(new Float(0.0));
        }

        functions = input.funcs;
        blocks = input.block_list;
        codes = input.ICCode;
        execute("call "+currentFunction);//进入main函数
    }

    private int findICLine(int cLine)
    {
        for(block b:blocks)
            if(b.codeLine>=cLine)
                return b.startIcLine;
        return 0;
    }



    private void callFunc(String func) throws Exception {//call func 也需要-1，执行完成后有++
        String[] s= func.split("_");
        if(!s[0].equals("func"))
            throw new Exception("wrong operation: "+func + " at icLine:" + CodeCursor);
        int fun = Integer.parseInt(s[1]);
        if(fun>functions.size()||fun<0)
            throw new Exception("wrong operation: "+func + " at icLine:" + CodeCursor);
        else {
            currentFunction = func;//设置当前执行函数
            functions.get(fun).backAddr = ++CodeCursor;//设置返回地址
            CodeCursor = functions.get(fun).startLine-1;//跳转到函数起始位置
        }
    }

    private Float getVar(String s) throws Exception {
        String[] names = s.split("_");
        switch (names[0])
        {
            case "ai":
                RunTimeArray rai = _Array.get(Integer.parseInt(names[1])+ArrayEBP);
                return new Float(rai.getInt(getVar(names[2]).intValue()));//数组的末尾一定是一个ebx,并进行越界检查
            case "af":
                RunTimeArray raf = _Array.get(Integer.parseInt(names[1])+ArrayEBP);
                return raf.getFloat(getVar(names[2]).intValue());
            case "i":
                return new Float(_Int.get(Integer.parseInt(names[1])+IntEBP));
            case "gi":
                return new Float(_GInt.get(Integer.parseInt(names[1])));
            case "f":
                return _Float.get(Integer.parseInt(names[1])+FloatEBP);
            case "gf":
                return _GFloat.get(Integer.parseInt(names[1]));
            case "func":
                return functions.get(Integer.parseInt(names[1])).getResult();
            case "eax":
                return EAX;
            case "ebx":
                return EBX;
            case "ecx":
                return ECX;
            case "CF":
                return getVar(currentFunction);
            default:
                break;
        }
        try {
            Float f = Float.parseFloat(s);
            return f;
        }catch (Exception e)
        {
            throw new Exception("format wrong: "+s);
        }
    }

    private void setVar(String s,Float o) throws Exception {
        String[] names = s.split("_");
        switch (names[0])
        {
            case "ai":
                RunTimeArray rai = _Array.get(Integer.parseInt(names[1])+ArrayEBP);
                rai.setInt(getVar(names[2]).intValue(),((Float)o).intValue());//支持ebx下标数组
                break;
            case "af":
                RunTimeArray raf = _Array.get(Integer.parseInt(names[1])+ArrayEBP);
                raf.setFloat(getVar(names[2]).intValue(),(Float)o);
            break;
            case "i":
                _Int.set(Integer.parseInt(names[1])+IntEBP,((Float)o).intValue());
                break;
            case "gi":
                _GInt.set(Integer.parseInt(names[1]),((Float)o).intValue());
                break;
            case "f":
                _Float.set(Integer.parseInt(names[1])+FloatEBP,(Float)o);
                break;
            case "gf":
                _GFloat.set(Integer.parseInt(names[1]),(Float)o);
                break;
            case "func":
                functions.get(Integer.parseInt(names[1])).setResult(o);
                break;
            case "eax":
                EAX = o;
                break;
            case "ebx":
                EBX = o;
                break;
            case "ecx":
                ECX = o;
                break;
            case "CF":
                setVar(currentFunction,o);
                break;
            default:
                break;
        }
    }

    private void newOps(String fir,String sec) throws Exception {
        String[] names = fir.split("_");
        switch (names[0])
        {
            case "i":
                if(sec==null)
                    _Int.add(0);
                else
                    _Int.add(getVar(sec).intValue());
                return;
            case "f":
                if(sec==null)
                    _Float.add(new Float(0.0));
                else
                    _Float.add(getVar(sec));
                return;
            case "ai":
                _Array.add(new RunTimeArray(6,getVar(sec).intValue()));
                return;
            case "af":
                _Array.add(new RunTimeArray(7,getVar(sec).intValue()));
               return;
            default:
                return;
        }

    }

    private void compareOps(String fir,String sec,String third) throws Exception {

        switch (fir)
        {
            case "==":
                if(getVar(sec).equals(getVar(third)))
                    ++CodeCursor;
                break;
            case "<":
                if((getVar(sec))<(getVar(third)))
                    ++CodeCursor;
                break;
            case "<=":
                if((getVar(sec))<=(getVar(third)))
                    ++CodeCursor;
                break;
            case ">":
                if((getVar(sec))>(getVar(third)))
                    ++CodeCursor;
                break;
            case ">=":
                if((getVar(sec))>=(getVar(third)))
                    ++CodeCursor;
                break;
        }
    }

    private void executeSinOps(String fir)
    {
        switch (fir)
        {
            case "setEBP":
                _Int.add(new Integer(ArrayEBP));
                _Int.add(new Integer(FloatEBP));
                _Int.add(new Integer(IntEBP));
                ArrayEBP = _Array.size();//i_0就是新作用域的第一个
                FloatEBP = _Float.size();
                IntEBP = _Int.size();
                break;
            case "resEBP":
                for(int i = _Array.size()-1;i>=ArrayEBP;i--)
                    _Array.remove(i);
                for(int i = _Float.size()-1;i>=FloatEBP;i--)
                    _Float.remove(i);
                for(int i = _Int.size()-1;i>=IntEBP;i--)
                    _Int.remove(i);
                ArrayEBP = _Int.get(IntEBP-3);
                FloatEBP = _Int.get(IntEBP-2);
                IntEBP = _Int.get(IntEBP-1);
                _Int.remove(_Int.size()-1);
                _Int.remove(_Int.size()-1);
                _Int.remove(_Int.size()-1);
                break;
            case "NUL"://空转指令
                break;
            case "popi":
                _Int.remove(_Int.size()-1);
                break;
            case "popf":
                _Float.remove(_Float.size()-1);
                break;
            default:
                break;
        }
    }

    private void executeBinOps(String fir,String sec) throws Exception {
        switch (fir)
        {
            case "setIE":
                IntEBP += getVar(sec);
                break;
            case "setFE":
                FloatEBP += getVar(sec);
                break;
            case "setAE":
                ArrayEBP += getVar(sec);
                break;
            case "goto"://goto需要-1，因为执行完成这条后会++codecursor
                if(!sec.contains("_"))
                    CodeCursor = getVar(sec).intValue()-1;
                else
                {
                    String[] num = sec.split("_");
                    if(!num[0].equals("func"))
                        CodeCursor = findICLine(getVar(num[1]).intValue())-1;
                    else
                        CodeCursor = functions.get(getVar(num[1]).intValue()).backAddr-1;//返回到函数的返回地址
                }
                break;
            case "call":
                callFunc(sec);
                break;
            case "new":
                newOps(sec,null);
                break;
            case "prt":
                if(sec.contains("i"))
                    outPut += getVar(sec).intValue();
                else
                    outPut +=getVar(sec);
                outPut+="\n";
                break;
            case "read":
                if(inPut ==null)
                    throw new NoInputException("Please input a num");
                else
                {
                    Float f = Float.parseFloat(inPut);
                    setVar(sec,f);
                }
                break;
            case "push":
                _Float.add(getVar(sec));
                return;
            case "pop":
                setVar(sec,_Float.get(_Float.size()-1));
                _Float.remove(_Float.size()-1);
            default:
                break;
        }
    }

    private void executeTriOps(String fir, String sec,String third) throws Exception
    {
        switch (fir)
        {
            case "add":
                setVar(sec,(Float)getVar(sec)+(Float)getVar(third));
                break;
            case "sub":
                setVar(sec,(Float)getVar(sec)-(Float)getVar(third));
                break;
            case "mul":
                setVar(sec,(Float)getVar(sec)*(Float)getVar(third));
                break;
            case "div":
                setVar(sec,(Float)getVar(sec)/(Float)getVar(third));
                break;
            case "move":
                    setVar(sec,getVar(third));
                break;
            case "new":
                newOps(sec,third);
                break;
            case "==":
            case "<=":
            case "<":
            case ">=":
            case ">":
                compareOps(fir,sec,third);
            default:
                break;
        }
    }

   private void execute(String s) throws Exception
   {
       String[] ops = s.split(" ");
       if(ops.length ==1)
            executeSinOps(ops[0]);
       else if(ops.length == 2)
            executeBinOps(ops[0],ops[1]);
       else if(ops.length == 3)
           executeTriOps(ops[0],ops[1],ops[2]);
       else
           throw new Exception("wrong operation: "+s+" at icLine:"+CodeCursor);

   }

    public int findCorrespondingLine(int ic)//binary search corresponding codeLine
    {
        int size = codes.size();
        if(size == 0)
            return 0;

        int lowerbound = 0;
        int upperbound = size -1;
        block b = blocks.get((lowerbound + upperbound)/2);
        while (lowerbound<upperbound)
        {
            if(ic<b.startIcLine)
                upperbound =(lowerbound + upperbound)/2;
            else if(ic>=b.endIcLine)
                lowerbound = (lowerbound + upperbound)/2;
            else
                break;

            b = blocks.get((lowerbound + upperbound)/2);
        }
        return b.codeLine;
    }

    public void RunTo(int codeLine) throws Exception
    {
        int i = findICLine(codeLine);
        if(i==0)
            throw new Exception("bad line");
        while (CodeCursor<i-1)//第i行不会被执行
        {
            ++CodeCursor;
            execute(codes.get(CodeCursor));
         }
    }

    public void Run() throws Exception
    {
        while (CodeCursor<codes.size()-1)
        {
            ++CodeCursor;
            execute(codes.get(CodeCursor));//如果执行失败，则还会在重新执行
        }
    }

    public int getCurrentLineNum()//返回当前执行到的行
    {
        return findCorrespondingLine(CodeCursor);
    }

    public void reset(String path) throws Exception {
        input = new ICInput();
        input.init(path);

        _Int = new ArrayList<>();
        _Float = new ArrayList<>();
        _Array = new ArrayList<>();
        _GInt = new ArrayList<>(input.GIntSize);
        _GFloat = new ArrayList<>(input.GFloatSize);

        functions = input.funcs;
        blocks = input.block_list;
        codes = input.ICCode;
    }

    public void reset() throws Exception {
        _Int = new ArrayList<>();
        _Float = new ArrayList<>();
        _Array = new ArrayList<>();
        _GInt = new ArrayList<>(input.GIntSize);
        _GFloat = new ArrayList<>(input.GFloatSize);
        currentFunction = input.mainName;

        for(int i=0;i<input.GIntSize;i++)
        {
            _GInt.add(0);
        }
        for(int i=0;i<input.GFloatSize;i++)
        {
            _GFloat.add(new Float(0.0));
        }

        CodeCursor=0;
        IntEBP = 0;
        FloatEBP = 0;
        ArrayEBP = 0;
        execute("call "+currentFunction);//进入main函数
    }

    public String getOutPut()
    {
        return outPut;
    }

    public void inPutString(String input)
    {
        inPut = input;
    }
}
