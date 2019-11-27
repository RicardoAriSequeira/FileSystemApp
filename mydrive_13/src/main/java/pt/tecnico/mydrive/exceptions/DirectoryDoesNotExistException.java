package pt.tecnico.mydrive.exceptions;

public class DirectoryDoesNotExistException extends MyDriveException {

    private static final long serialVersionUID = 1L;

    private String _directory;

    public DirectoryDoesNotExistException(String directory) {
        _directory = directory;
    }

    public String getDirectory() {
        return _directory;
    }

    @Override
    public String getMessage() {
        return "Directory " + _directory + " does not exist";
    }
}
