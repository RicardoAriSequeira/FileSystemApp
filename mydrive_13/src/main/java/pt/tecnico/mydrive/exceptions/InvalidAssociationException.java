package pt.tecnico.mydrive.exceptions;

public class InvalidAssociationException extends MyDriveException {

    private static final long serialVersionUID = 1L;

    private String _ext;

    public InvalidAssociationException(String ext) {
        _ext = ext;
    }

    public String getInvalidFileName() {
        return _ext;
    }

    @Override
    public String getMessage() {
        return "There is no association for the extension: " + _ext;
    }
}
