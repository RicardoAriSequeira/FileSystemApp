package pt.tecnico.mydrive.exceptions;

public class LinkLoopException extends MyDriveException {

    private static final long serialVersionUID = 1L;

    @Override
    public String getMessage() {
        return "Link loop detected!";
    }
}
