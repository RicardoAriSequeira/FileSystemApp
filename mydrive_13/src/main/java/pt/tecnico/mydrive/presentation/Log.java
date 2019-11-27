package pt.tecnico.mydrive.presentation;

import pt.tecnico.mydrive.service.LoginService;

public class Log extends MdCommand {
		Shell _sh;
	
    public Log(Shell sh) {super(sh, "login",
    		"start session if username and password are correct (can also start a Guest session for the username 'nobody')");
    _sh = sh;
    }
    
    		
    public void execute(String[] args) {
    	if (args.length < 1)
    	    throw new RuntimeException("USAGE: "+name()+" username");
    	
    	else if (args.length == 1) {
    	    LoginService ls = new LoginService(args[0],null);
    	    ls.execute();
    	    _sh.addToken(args[0], ls.result());
    	    _sh.updateSession(ls.result());
    	} else {
    		LoginService ls = new LoginService(args[0], args[1]);
    	    ls.execute();
    	    _sh.addToken(args[0], ls.result());
    	    _sh.updateSession(ls.result());
    	}
	}
}

