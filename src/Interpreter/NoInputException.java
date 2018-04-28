package Interpreter;

/**
 * Created by yl on 2017/12/10.
 */
public class NoInputException extends Exception {
    String message="";
    public NoInputException(String mes)
    {
        message = mes;
    }
    public String getMessage()
    {
        return message;
    }
}
