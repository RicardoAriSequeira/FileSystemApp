package pt.tecnico.mydrive.service;

import java.util.Set;


import pt.tecnico.mydrive.domain.Directory;
import pt.tecnico.mydrive.domain.File;
import pt.tecnico.mydrive.domain.Login;
import pt.tecnico.mydrive.exceptions.FileDoesNotExistException;
import pt.tecnico.mydrive.exceptions.UserDoesNotHavePermissionException;

public class ReadFileService extends MyDriveService {

	private String content; 
	private String filename; 
	private long token;	

    public ReadFileService(long token, String filename) { 
    	this.token  = token;
    	this.filename  = filename;
    }

    public final void dispatch() throws FileDoesNotExistException, UserDoesNotHavePermissionException {
    	Login log = getMyDrive().getSessionByToken(token);
    	getMyDrive().refreshSession(log);
    	File file = null;
    	
    	Directory currentDirectory = log.getCurrentDir();
    	Set<File> directoryFiles = currentDirectory.getDirfilesSet();
   	   	
    	for(File f : directoryFiles){
    		if(f.getName().equals(filename)){
    			file = f;
    			break;
    		}
    	}
    	
    	if(file == null){
    		throw new FileDoesNotExistException(filename);
    	}
    	
    	// Dummy method
    	if(file.getContent().contains("$")){
    		file = getMyDrive().resolveEnvironmentLink(file);
    	}
    	
    	if(file.hasPermissionToRead(log.getUser())){
    		content = file.showContent();
    	}
    	
    }

     public final String result() {
        return content;
    }

  
}