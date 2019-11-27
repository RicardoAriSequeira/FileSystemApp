package pt.tecnico.mydrive.exceptions;

public class IsLinkException extends MyDriveException {

    private static final long serialVersionUID = 1L;

    private String _fileName;

    public IsLinkException(String fileName) {
        _fileName = fileName;
    }

    @Override
    public String getMessage() {
        return "File " + _fileName + " " + "is a Link!\n";
    }
}
