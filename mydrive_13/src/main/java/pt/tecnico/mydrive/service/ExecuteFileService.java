package pt.tecnico.mydrive.service;

import java.lang.reflect.InvocationTargetException;

import pt.tecnico.mydrive.domain.*;
import pt.tecnico.mydrive.exceptions.*;

public class ExecuteFileService extends MyDriveService {

    private long _token;
    private String _path;
    private String _args;
    private Login _session;
    
    public ExecuteFileService(long token, String path, String args) {
    	this._path = path;
    	this._token = token;
    	this._args = args;
    	this._session = MyDrive.getInstance().getSessionByToken(token);
    	
    }

    public final void dispatch() throws IsDirectoryException, FileDoesNotExistException, UserDoesNotHavePermissionException {
        MyDrive md = MyDrive.getInstance();
    	md.refreshSession(_session);
    	String [] aux = null;
    	String truePath = _path;

	if(!_path.substring(0,1).equals("/")){
		if(_session.getCurrentDir().getFullPath().equals("/"))
			truePath = "/" + _path;
		else truePath = _session.getCurrentDir().getFullPath() + "/" + _path;
	}	
    	File f = md.getFileByPath(truePath);
    	
    	if(_args != null){
    		aux = _args.split(" ");
    	}
    	
    	if(f.getName().contains(".") && !f.getName().equals(".") && !f.getName().equals("..")){
    		// Dummy method
    		f = getMyDrive().execAssociation((PlainFile) f);
    	}
    		
    	
    	if(f.getContent().contains("$")){
    		// Dummy method
    		f = getMyDrive().resolveEnvironmentLink(f);
    	}
    	
    	f.hasPermissionToExecute(_session.getUser());
    	
    	try {
    		f.exec(aux);
		} catch (ClassNotFoundException | SecurityException | NoSuchMethodException
				| IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
			
			e.printStackTrace();
		}
    	
   		
    }
 }
   


