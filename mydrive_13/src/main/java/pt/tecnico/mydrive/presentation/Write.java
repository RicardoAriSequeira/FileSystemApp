package pt.tecnico.mydrive.presentation;

import pt.tecnico.mydrive.service.*;

public class Write extends MdCommand {

    public Write(Shell sh) {
    	super(sh, "update", "updates the content of a file in the given path");
    	}
    
    public void execute(String[] args) {
    	if(args.length != 2){
    		throw new RuntimeException("USAGE: " + name() + " path content");
    	}
    	
    	long token = shell().getCurrentToken();
    	WriteFileService service = new WriteFileService(token,args[0], args[1]);
    	service.execute();
    	
	}
}

