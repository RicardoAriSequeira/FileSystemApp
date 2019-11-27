package pt.tecnico.mydrive.exceptions;

public class InvalidAppContentException extends MyDriveException {

	private static final long serialVersionUID = 1L;

	    public  InvalidAppContentException() {
	    }

	    @Override
	    public String getMessage() {
	        return "App content is not a java fully qualified name.";
	    }
}
