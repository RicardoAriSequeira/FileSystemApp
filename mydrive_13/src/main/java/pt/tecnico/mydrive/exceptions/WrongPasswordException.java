package pt.tecnico.mydrive.exceptions;

public class WrongPasswordException extends MyDriveException {

    private static final long serialVersionUID = 1L;

    public WrongPasswordException() {
    }

    @Override
    public String getMessage() {
        return "Invalid password!";
    }
}
