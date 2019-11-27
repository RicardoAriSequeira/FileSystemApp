package pt.tecnico.mydrive.service;

import pt.tecnico.mydrive.domain.*;

import pt.tecnico.mydrive.exceptions.FileDoesNotExistException;
import pt.tecnico.mydrive.exceptions.UserDoesNotHavePermissionException;

public class DeleteFileService extends MyDriveService{
	
	private long token;
	private String filename;

	public DeleteFileService(long token, String file){
		this.filename = file;
		this.token = token;
	}
	
	@Override
	protected void dispatch() throws FileDoesNotExistException, UserDoesNotHavePermissionException {
		Login log = getMyDrive().getSessionByToken(token);
        
		getMyDrive().refreshSession(log);
        
		Directory currentDirectory = log.getCurrentDir();
		File file = currentDirectory.hasFile(filename);
		if(file == null)
			throw new FileDoesNotExistException(filename);
		if(file.hasPermissionToDelete(log.getUser()))
			file.deleteFile();
	}
}