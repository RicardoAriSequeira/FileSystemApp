package pt.tecnico.mydrive.service;


import pt.tecnico.mydrive.exceptions.*;
import pt.tecnico.mydrive.domain.*;

public class WriteFileService extends MyDriveService {
	String _content;
	String _fileName;
	long _token;
	public WriteFileService(long token, String fileName, String content) {
		_content = content;
		_fileName = fileName;
		_token = token;
	}

	@Override
	protected void dispatch() throws MyDriveException, FileDoesNotExistException/*, IsLinkException,IsDirectoryException*/{
		File path_file = null;
		Directory currentDir = null;
		String path = null;
		
		Login log = MyDrive.getInstance().getSessionByToken(_token);
		getMyDrive().refreshSession(log);
		
		if(_fileName.startsWith("/")){
    		path_file = log.getMyDrive().getFileByPath(_fileName);	
    	}
    	else{
    		currentDir = log.getCurrentDir();
    		path = currentDir.getFullPath() + "/" + _fileName;
    		path_file = currentDir.getFileByRelativePath(_fileName);
    		/*for(File f: currentDir.getDirfilesSet()){
    			if(f.getName().equals(_fileName)){
    				path_file = f;
    				break;
    			}
    		}*/	
    		
    	}
		
		/*File writableFile = null;
		MyDrive mydrive = MyDrive.getInstance();
		Login session = mydrive.getSessionByToken(_token);
		
		}*/
    	
		if(path_file==null){
			throw new FileDoesNotExistException(_fileName);
		}
		
		// Dummy method
    	if(path_file.getContent().contains("$")){
    		path_file = getMyDrive().resolveEnvironmentLink(path_file);
    	}
		
		path_file.hasPermissionToWrite(log.getUser());
		path_file.setContent(_content);
	}

}
