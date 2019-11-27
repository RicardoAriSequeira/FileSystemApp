package pt.tecnico.mydrive.exceptions;

public class FileCantBeRemovedException extends MyDriveException {

    private static final long serialVersionUID = 1L;

    private String _filepath;

    public FileCantBeRemovedException(String filepath) {
        _filepath = filepath;
    }

    public String getInvalidFilepath() {
        return _filepath;
    }

    @Override
    public String getMessage() {
        return "File can't be removed: " + _filepath;
    }
}