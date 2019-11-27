package pt.tecnico.mydrive.domain;

import java.lang.reflect.InvocationTargetException;

import org.jdom2.Element;

import pt.tecnico.mydrive.exceptions.*;

public abstract class File extends File_Base {
	
    public File() {
        super();
    }
    
    @Override
    public void setName(String name){
    	 if(name.contains("/") || name.contains("\0")){
             throw new InvalidFileNameException(name);
    	 }
    	 super.setName(name);
    }
    
    @Override
    public void setMydrive(MyDrive mydrive){
    	if(mydrive == null){
    		super.setMydrive(mydrive);
    	}
    	mydrive.addMydrivefiles(this);
    }
    
    public boolean canBeCreated(String name, Directory dir) {
    	String pathToParent = dir.getFullPath() + "/" + name;
    	if(pathToParent.length()<=1024)
    		return true;
    	return false;
    }
    
    public void setRootDirName(String name){
   	  super.setName(name);
   }
    
    public boolean isOwner(User u) {
    	return (this.getUser().getUsername().equals(u.getUsername()));
    }
    
    public String getContent(){ return null; }
    
    public abstract String showContent();
    
    public abstract void setContent(String content);
    
    public abstract void exec(String[] args) throws ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException;
    
    public void deleteFile() {
        MyDrive md = MyDrive.getInstance();
    	getDirectory().getDirfilesSet().remove(this);
		getUser().getUserfilesSet().remove(this);
		md.getMydrivefilesSet().remove(this);
		deleteDomainObject();
    }

    public String getPath() {
    	String result = "";
    	File current = this;
    	if(this.getDirectory().getName().equals("/"))
    		return "/";
    	while(!(current.getDirectory().getName().equals("/"))){
    		result = "/" + current.getDirectory().getName() + result;
    		current = current.getDirectory();
    	}
    	return result;
    }

    public String getFullPath() {
        String parentPath = this.getPath();
            if(parentPath.equals("/") && this.getName().equals("/")){
                        return parentPath;
                }

                if(this.getDirectory().getName().equals("/")){
                    String fullPath = parentPath + this.getName();
                    return fullPath;

                }

                String fullPath = parentPath + "/" + this.getName();
                    return fullPath;
            }

            public int listSize(){
                return 0;
            }


    public Element xmlExport() {
        Element fileElement = new Element("file");
        fileElement.setAttribute("id",Integer.toString(this.getFileID()));
        
        Element pathElement = new Element("path");
        pathElement.setText(this.getPath());
        fileElement.addContent(pathElement);
        
        Element nameElement = new Element("name");
        nameElement.setText(this.getName());
        fileElement.addContent(nameElement);
        
        Element ownerElement = new Element("owner");
        ownerElement.setText(this.getUser().getUsername());
        fileElement.addContent(ownerElement);
        
        Element permElement = new Element("perm");
        permElement.setText(this.getPermissions());
        fileElement.addContent(permElement);
        
        return fileElement;
        
    }
    
    public boolean hasPermissionToRead(User u) {return hasPermissionOperation(u, 0, 'r');} 
    public boolean hasPermissionToWrite(User u) {return hasPermissionOperation(u, 1, 'w');}
    public boolean hasPermissionToExecute(User u) {return hasPermissionOperation(u, 2, 'x');}  
    public boolean hasPermissionToDelete(User u) {return hasPermissionOperation(u, 3, 'd');}
    
    private boolean hasPermissionOperation(User u, int index, char c) {
    	
    	if (u.isRoot()) return true;
    	
    	else if(u.isGuest()) throw new UserDoesNotHavePermissionException(u.getName());
    	
    	else if ((this.isOwner(u)) &&
    			 (u.getUmask().charAt(index) == c) &&
    			 (this.getPermissions().charAt(index) == c))
    		return true;
    	
    	else if ((!this.isOwner(u)) &&
    			 (this.getPermissions().charAt(index + 4) == c) &&
				 (u.getUmask().charAt(index + 4) == c))
			return true;
    	
    	throw new UserDoesNotHavePermissionException(u.getName());
    	
    }
    
	public File resolveEnvironmentLink(File mockedLink){
    	//TODO: mockup
    	return null;
    }
    
    public void checkValidJavaName(String s) throws InvalidAppContentException{
    	if(!(s.matches("([a-zA-Z_$][a-zA-Z\\d_$]*\\.)*[a-zA-Z_$][a-zA-Z\\d_$]*"))){
			throw new InvalidAppContentException();
		}
    }
}
