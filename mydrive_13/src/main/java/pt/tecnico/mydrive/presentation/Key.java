package pt.tecnico.mydrive.presentation;


public class Key extends MdCommand {
	Shell _sh;

    public Key(Shell sh) {
    	super(sh, "token", "Change token updating session");
    	_sh = sh;
    }

    public void execute(String[] args) {
    
    	if (args.length > 1)
    	    throw new RuntimeException("USAGE: " + name() + " [username]");
    	
    	else if(args.length == 0){
    		long token = shell().getCurrentToken();
    
    		System.out.println(token + " " +  _sh.getUsernameByToken(token)  );
    		
    	}else if(args.length == 1){
    		String username = args[0];
    		long token = shell().getTokenByUsername(username);
    		
    		shell().updateSession(token);
			System.out.println(token);
    	}	
	}
}

