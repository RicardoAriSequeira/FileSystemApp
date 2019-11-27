package pt.tecnico.mydrive.exceptions;

public class SessionNotFoundException extends MyDriveException {

    private static final long serialVersionUID = 1L;

    private long _token;

    public SessionNotFoundException(long token) {
        _token = token;
    }

    @Override
    public String getMessage() {
        return "Token " + _token + " " + "has no available session!\n";
    }
}
