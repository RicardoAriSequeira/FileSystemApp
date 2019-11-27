package pt.tecnico.mydrive.presentation;
import pt.tecnico.mydrive.service.*;

public class ChangeDirectory extends MdCommand {

    public ChangeDirectory(Shell sh) {
    	super(sh, "cwd");
    	}

    public void execute(String[] args) {

    	if(args.length > 1){
    		throw new RuntimeException("USAGE: "+name()+" [path]");
    	}

    	long token = shell().getCurrentToken();

    	if (args.length == 0) {
	     	ChangeDirectoryService cds = new ChangeDirectoryService(token, null);
	    	cds.execute();
		 System.out.println(cds.result());
	   		
		}

	else if (args.length == 1) {
			
		ChangeDirectoryService cds = new ChangeDirectoryService(token, args[0]);
		 cds.execute();
		 System.out.println(cds.result());
		}



    	}
}
