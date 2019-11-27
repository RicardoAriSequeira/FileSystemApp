package pt.tecnico.mydrive.service;


import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

import pt.tecnico.mydrive.service.dto.FileDto;
import pt.tecnico.mydrive.exceptions.*;
import pt.tecnico.mydrive.domain.*;

public class ListDirectoryService extends MyDriveService {

    private List<FileDto> list;
    private long token;
    private String path = null;

    public ListDirectoryService(long token) {
    	this.token  = token;
    }
    
    public ListDirectoryService(long token, String path){
    	this.token  = token;
    	this.path = path;
    }

    public final void dispatch() throws FileDoesNotExistException, UserDoesNotHavePermissionException{
        
    	Login log = getMyDrive().getSessionByToken(token);
        getMyDrive().refreshSession(log);
        Directory currentDir = log.getCurrentDir(); 
        Directory parent = log.getCurrentDir().getDirectory(); 
         list =  new ArrayList<FileDto>();
  
        
        Directory current = null;
        Directory parentDir = null;

       
        
    	if(path==null || path.equals(".")){
    		current = log.getCurrentDir();
                        parentDir = current.getDirectory();
    	}
            else if(path.equals("..")){
                current = log.getCurrentDir().getDirectory();
                parentDir = current.getDirectory();
            }

    	else{
    		current = (Directory) log.getMyDrive().getFileByPath(path);
    		parentDir = current.getDirectory();
    	}
        
     

        if(!(log.getCurrentDir().hasPermissionToRead(log.getUser()))) {
                throw new UserDoesNotHavePermissionException(log.getUser().getUsername());
            }

        for ( File f : current.getDirfilesSet() ){

        if(!f.getName().equals("/")){

        	if(f.getClass().getSimpleName().equals("Link")){
        		Link h = (Link) f;
        		list.add( new FileDto(f.getClass().getSimpleName(), f.getPermissions(), Integer.toString(f.listSize()),
                 f.getUser().getName(), Integer.toString(f.getFileID()), f.getLastModified().toString("yyyy-MM-dd HH:mm:ss"), f.getName() + "->" + h.getContent() ));
        	}

        	else{
            list.add( new FileDto(f.getClass().getSimpleName(), f.getPermissions(), Integer.toString(f.listSize()),
                 f.getUser().getName(), Integer.toString(f.getFileID()), f.getLastModified().toString("yyyy-MM-dd HH:mm:ss"), f.getName() ));
        		}
           }
       }

 Collections.sort(list);

         list.add( 0, new FileDto(current.getClass().getSimpleName(), current.getPermissions(), Integer.toString(current.listSize()),
                 current.getUser().getName(), Integer.toString(current.getFileID()), current.getLastModified().toString("yyyy-MM-dd HH:mm:ss"), "." ));

        list.add( 1, new FileDto(parentDir.getClass().getSimpleName(), parentDir.getPermissions(), Integer.toString(parentDir.listSize()),
                 parentDir.getUser().getName(), Integer.toString(parentDir.getFileID()), parentDir.getLastModified().toString("yyyy-MM-dd HH:mm:ss"), ".." ));
         
        }


    public final List<FileDto> result() {
        return list;
    }
}