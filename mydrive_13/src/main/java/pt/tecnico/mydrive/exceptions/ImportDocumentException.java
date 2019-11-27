package pt.tecnico.mydrive.exceptions;

public class ImportDocumentException extends MyDriveException {

    private static final long serialVersionUID = 1L;

    public ImportDocumentException() {
        super("Error importing from XML");
    }
}
