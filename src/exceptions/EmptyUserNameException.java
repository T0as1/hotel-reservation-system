package exceptions;
public class EmptyUserNameException extends RuntimeException {
    public EmptyUserNameException(String message) {
        super(message);
    }
}
