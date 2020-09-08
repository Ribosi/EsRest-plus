package Excepetion;

public class CriteriaException extends Exception {
    public CriteriaException(String message){
        super(message);
    }

    public CriteriaException(String message,Throwable throwable){
        super(message,throwable);
    }
}
