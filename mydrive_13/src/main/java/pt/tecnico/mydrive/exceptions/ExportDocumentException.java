package pt.tecnico.mydrive.exceptions;

public class ExportDocumentException extends MyDriveException {

    private static final long serialVersionUID = 1L;

    public ExportDocumentException() {
        super("Error exporting to XML");
    }
}
