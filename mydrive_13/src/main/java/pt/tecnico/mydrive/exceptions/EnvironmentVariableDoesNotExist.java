package pt.tecnico.mydrive.exceptions;

public class EnvironmentVariableDoesNotExist extends MyDriveException {

    private static final long serialVersionUID = 1L;

    private String _name;

    public EnvironmentVariableDoesNotExist(String name) {
        _name = name;
    }

    public String getName() {
        return _name;
    }

    @Override
    public String getMessage() {
        return "Environment variable " + _name + " does not exist";
    }
}
