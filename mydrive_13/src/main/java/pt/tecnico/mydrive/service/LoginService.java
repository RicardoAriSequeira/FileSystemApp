package pt.tecnico.mydrive.service;

import pt.tecnico.mydrive.domain.*;
import pt.tecnico.mydrive.exceptions.*;

public class LoginService extends MyDriveService {

    private long _token;
    private String _username;
    private String _password;
    private MyDrive _mydrive = MyDrive.getInstance();
    
    public LoginService(String username, String password) {
    	_username = username;
    	_password = password;
    	
    }

    public final void dispatch() throws UserDoesNotExistException, WrongPasswordException{
    	
   	    Login log;
   	    
    	if(_password == null) { log = new Login(_mydrive); }
    	
    	else { log = new Login(_mydrive,_username,_password); }
    	
    	_token = log.getToken();
    	
    	getMyDrive().clearOldSessions();
    	
    }
    
    public final long result() {
        return _token;
    }
  
}