package pt.tecnico.mydrive.exceptions;

public class IllegalPasswordChangeException extends MyDriveException {

	private static final long serialVersionUID = 1L;
	
	    private String _username;

	    public IllegalPasswordChangeException(String username) {
	        _username = username;
	    }

	    public String getInvalidUsername() {
	        return _username;
	    }

	    @Override
	    public String getMessage() {
	        return "Cannot change " + _username + " password!";
	    }

}
