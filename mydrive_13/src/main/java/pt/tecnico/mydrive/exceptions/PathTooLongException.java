package pt.tecnico.mydrive.exceptions;

public class PathTooLongException extends MyDriveException {

    private static final long serialVersionUID = 1L;

    public PathTooLongException() {
        super("Path to file has more than 1024 characters");
    }
}
