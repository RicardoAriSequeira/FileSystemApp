package pt.tecnico.mydrive.exceptions;

public class PasswordTooShortException extends MyDriveException {

	private static final long serialVersionUID = 1L;

	    public PasswordTooShortException() {
	    }

	    @Override
	    public String getMessage() {
	        return "Password needs to have at least 8 caracteres";
	    }
}
