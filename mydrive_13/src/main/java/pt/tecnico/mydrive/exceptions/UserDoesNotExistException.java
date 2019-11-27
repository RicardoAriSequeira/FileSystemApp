package pt.tecnico.mydrive.exceptions;

public class UserDoesNotExistException extends MyDriveException {

    private static final long serialVersionUID = 1L;

    private String _username;

    public UserDoesNotExistException(String username) {
        _username = username;
    }

    public String getUsername() {
        return _username;
    }

    @Override
    public String getMessage() {
        return "User " + _username + " does not exist";
    }
}
