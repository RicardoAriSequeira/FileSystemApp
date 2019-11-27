package pt.tecnico.mydrive.exceptions;

public class UserDoesNotHavePermissionException extends MyDriveException {

    private static final long serialVersionUID = 1L;

    private String _username;

    public UserDoesNotHavePermissionException(String username) {
        _username = username;
    }

    public String getUsername() {
        return _username;
    }

    @Override
    public String getMessage() {
        return "User " + _username + " does not have permission for this action";
    }
}
