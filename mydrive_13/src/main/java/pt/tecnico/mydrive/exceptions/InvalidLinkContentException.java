package pt.tecnico.mydrive.exceptions;

public class InvalidLinkContentException extends MyDriveException {

    private static final long serialVersionUID = 1L;

    private String _content;

    public InvalidLinkContentException(String content) {
        _content = content;
    }

    @Override
    public String getMessage() {
        return "Invalid link content: " + _content + ".";
    }
}
