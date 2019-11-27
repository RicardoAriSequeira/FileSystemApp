package pt.tecnico.mydrive.service;

import pt.tecnico.mydrive.domain.*;
import pt.tecnico.mydrive.exceptions.IsNotDirectoryException;
import pt.tecnico.mydrive.exceptions.UserDoesNotHavePermissionException;

public class ChangeDirectoryService extends MyDriveService {

    private String path;
    private Login session;
    private String res;

    public ChangeDirectoryService(long token, String path) {
        this.path = path;
        this.session = getMyDrive().getSessionByToken(token);
        this.res = session.getCurrentDir().getFullPath();
    }
    
    public void setPath(String s) {
    	path = s;
    }

    public final void dispatch() {
    	
    	getMyDrive().refreshSession(session);
    	
    	if(path == null){
    			session.setCurrentDir(session.getUser().getTrueHomedir());
    			res = session.getCurrentDir().getFullPath();
    		    return;
    	}
    	
    	
    	if (path.equals(".")) {
    		return;
    	}
    	
    	if (path.equals("..")) {
    		
    		if (session.getCurrentDir().getDirectory().hasPermissionToRead(session.getUser())) {
    			session.setCurrentDir(session.getCurrentDir().getDirectory());
    			res = session.getCurrentDir().getFullPath();
    			
    		} else {
    			res = null;
    			throw new UserDoesNotHavePermissionException(session.getUser().getUsername());
    		}
    		
    		return;
    		
		} 
    	
    	else {
    		
    		if (!path.startsWith("/")){
    			
    			if (!session.getCurrentDir().getFullPath().equals("/"))
    			
    				setPath(session.getCurrentDir().getFullPath() + "/" + path);
    			
    			else 
    				setPath(session.getCurrentDir().getFullPath() + path);
    			
    		}
    		
    		File f = getMyDrive().getFileByPath(path);
    		
    		if (f.getClass().getSimpleName().equals("Directory")) {
    			
    			if (f.hasPermissionToRead(session.getUser())) {
    				session.setCurrentDir((Directory) f);
    				res = session.getCurrentDir().getFullPath();
    				
    			}
    			
    			else {
    				res = null;
    				throw new UserDoesNotHavePermissionException(session.getUser().getUsername());
    			}
    			
    		} else {
    			res = null;
    			throw new IsNotDirectoryException(path);
    		}
    		
    	}
    }
    
    public final String result() {
        return res;
    }
  
}