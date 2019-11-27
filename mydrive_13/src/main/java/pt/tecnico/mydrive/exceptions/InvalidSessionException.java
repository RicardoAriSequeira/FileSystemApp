package pt.tecnico.mydrive.exceptions;

public class InvalidSessionException extends MyDriveException {

    private static final long serialVersionUID = 1L;

    public InvalidSessionException() {
    }

    @Override
    public String getMessage() {
        return "Invalid session!";
    }
}
