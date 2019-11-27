package pt.tecnico.mydrive.domain;

public class ExtensionAssociation extends ExtensionAssociation_Base {
    
    public ExtensionAssociation() {
        super();
    }
    
    public ExtensionAssociation(User u, String ext, PlainFile f) {
    	this.setExtension(ext);
    	this.setPlainFile(f);
    	this.setUser(u);
    }
    
}
