package pt.tecnico.mydrive.presentation;

import pt.tecnico.mydrive.service.ListDirectoryService;
import pt.tecnico.mydrive.service.dto.FileDto;

public class List extends MdCommand {

    public List(Shell sh) {
    	super(sh, "list", "lists current directory (or specific directory if path given)");
    }
    
    public void print(FileDto d){
    	System.out.println(d.getType() + " " + d.getPermissions() + " " 
    	+ d.getSize() + " " + d.getOwner() + " "  + d.getId() + " " 
    	+ d.getDate() + " "  + d.getName());
    }
    
    public void execute(String[] args) {
    	
    	if(args.length > 1){
    		throw new RuntimeException("USAGE: " + name() + " [directory]");
    	}
    	
    	long token = shell().getCurrentToken();
    	ListDirectoryService lds = null;
    	
    	if (args.length == 0) {
    	    lds = new ListDirectoryService(token);
    	    lds.execute();    	    
    	}
    	else {
    	    lds = new ListDirectoryService(token, args[0]);
    	    lds.execute();
    	}
    	for (FileDto d: lds.result())
	    	print(d);
	}
}

