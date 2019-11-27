package pt.tecnico.mydrive.presentation;
import pt.tecnico.mydrive.service.*;

public class Execute extends MdCommand {

    public Execute(Shell sh) {
    	super(sh, "do", "Executes a file.");
    	}
    public void execute(String[] args) {

    	if((args.length == 0) || (args.length > 2)){
    		throw new RuntimeException("USAGE: "+name()+" path" +" [args]");
    	}

    	long token = shell().getCurrentToken();

    	if (args.length == 1) {
	 	 ExecuteFileService efs = new ExecuteFileService(token, args[0], null);
	      	efs.execute();
	
	   		
		}

		else if (args.length == 2) {
			
	    	 ExecuteFileService efs = new ExecuteFileService(token, args[0], args[1]);
	    	 efs.execute();
			
		}
	}
}

