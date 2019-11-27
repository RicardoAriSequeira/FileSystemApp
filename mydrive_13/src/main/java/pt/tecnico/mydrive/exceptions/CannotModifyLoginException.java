package pt.tecnico.mydrive.exceptions;

public class CannotModifyLoginException extends MyDriveException {

	private static final long serialVersionUID = 1L;

	    public  CannotModifyLoginException() {
	    }

	    @Override
	    public String getMessage() {
	        return "You cannot modify a session after it has been created.";
	    }
}
