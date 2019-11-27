package pt.tecnico.mydrive.domain;

import pt.tecnico.mydrive.exceptions.*;
import org.joda.time.DateTime;

public class Login extends Login_Base {
    
    public Login() {
        super();

    }
    
    public Login(MyDrive mdrive){
    	User user = mdrive.getUserByName("nobody");
    	setUser(user);
    	setCurrentDir((Directory) mdrive.getFileByPath(user.getHomedir()));
    	setToken(mdrive.createToken());
    	setMyDrive(mdrive);
    	setLastAccess(new DateTime());
    }
    
    public Login(MyDrive mdrive, String username, String password) throws WrongPasswordException{
    	User user = mdrive.getUserByName(username); 
    	if(user == null)
    		throw new UserDoesNotExistException(username);
    	
    	if(!(user.getPassword().equals(password)))
            throw new WrongPasswordException();

    	setUser(user);
    	setCurrentDir((Directory) mdrive.getFileByPath(user.getHomedir()));
    	setToken(mdrive.createToken());
    	setMyDrive(mdrive);
    	setLastAccess(new DateTime());
    }

    
    @Override
    public void setToken(long token) throws CannotModifyLoginException {
    	if(!(this.getToken() == 0L))
    		throw new CannotModifyLoginException();
    	super.setToken(token);
    }
    
    @Override
    public void setUser(User user) throws CannotModifyLoginException {
    	if(!(this.getUser() == null))
    		throw new CannotModifyLoginException();
    	super.setUser(user);
    }
    

    
    public void addVariable(String name, String value){
    	for(EnvironmentVariable var: this.getLoginVariablesSet()){
    		if(var.getName().equals(name)){
    			var.setValue(value);
    			return;
    		}
    	}
    	EnvironmentVariable loginVar = new EnvironmentVariable(name, value);
    	this.addLoginVariables(loginVar);
    }

}
