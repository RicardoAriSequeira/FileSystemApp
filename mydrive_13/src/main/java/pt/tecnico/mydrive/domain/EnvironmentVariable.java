package pt.tecnico.mydrive.domain;

public class EnvironmentVariable extends EnvironmentVariable_Base {
    
    public EnvironmentVariable() {
        super();
    }
    
    public EnvironmentVariable(String name, String value){
    	setName(name);
    	setValue(value);
    }
    
}
