package pt.tecnico.mydrive.exceptions;

public class InvalidUsernameException extends MyDriveException {

    private static final long serialVersionUID = 1L;

    private String _username;

    public InvalidUsernameException(String username) {
        _username = username;
    }

    public String getInvalidUsername() {
        return _username;
    }

    @Override
    public String getMessage() {
        return "Invalid username format: " + _username;
    }
}
