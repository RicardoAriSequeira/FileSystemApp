package pt.tecnico.mydrive.exceptions;

public class IsNotDirectoryException extends MyDriveException {

    private static final long serialVersionUID = 1L;

    private String _fileName;

    public IsNotDirectoryException(String fileName) {
        _fileName = fileName;
    }

    @Override
    public String getMessage() {
        return "File " + _fileName + " " + "is not a Directory!\n";
    }
}
