package pt.tecnico.mydrive.service;

import pt.tecnico.mydrive.exceptions.*;
import pt.tecnico.mydrive.domain.*;

public class CreateFileService extends MyDriveService{
	private long token;
	private String fileName;
	private String fileContent;	
	private String typeFile;
	
	public CreateFileService(long token, String name, String type ,String content ){
		this.token = token;
		this.fileName = name;
		this.fileContent = content;
		this.typeFile = type;
	}
	
	public CreateFileService(long token, String name, String type){
		this.token = token;
		this.fileName = name;
		this.typeFile = type;
		this.fileContent = "";
	}
	
	@Override
	protected void dispatch() throws NameAlreadyExistsException {
		
		Login log = getMyDrive().getSessionByToken(token);
		getMyDrive().refreshSession(log);
		User usr = log.getUser();
		Directory current = log.getCurrentDir();
		MyDrive md = getMyDrive();

		if(current.hasFile(fileName) != null){
			throw new NameAlreadyExistsException(fileName);
		}
		
		if(current.hasPermissionToWrite(usr));
		
		if(typeFile.equals("PlainFile")){
			new PlainFile(md, fileName, usr, usr.getUmask(), current, fileContent);
		}
		
		else if(typeFile.equals("Directory")){
			if(fileContent.equals(""))
				new Directory(md, usr, fileName, usr.getUmask(), current);
			else
				throw new IsDirectoryException(fileName);
		}
		else if(typeFile.equals("App"))
			new App(md, fileName, usr,  usr.getUmask(), current, fileContent);
		
		else if(typeFile.equals("Link")){
			
			try{ 
				
				md.getFileByPath(fileContent);
				
			} catch(FileDoesNotExistException e){
				throw new InvalidLinkContentException(fileContent);
				
			}
			
			new Link(md, fileName, usr, usr.getUmask(), current, fileContent);
		}
	}
	
	
	
}
