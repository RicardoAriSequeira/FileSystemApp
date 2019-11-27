package pt.tecnico.mydrive.exceptions;

public class IsDirectoryException extends MyDriveException {

    private static final long serialVersionUID = 1L;

    private String _fileName;

    public IsDirectoryException(String fileName) {
        _fileName = fileName;
    }

    @Override
    public String getMessage() {
        return "File " + _fileName + " " + "is a Directory!\n";
    }
}
