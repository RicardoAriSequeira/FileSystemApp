package pt.tecnico.mydrive.exceptions;

public class FileDoesNotExistException extends MyDriveException {

    private static final long serialVersionUID = 1L;

    private String _name;

    public FileDoesNotExistException(String name) {
        _name = name;
    }

    public String getName() {
        return _name;
    }

    @Override
    public String getMessage() {
        return "File or Directory " + _name + " does not exist";
    }
}
