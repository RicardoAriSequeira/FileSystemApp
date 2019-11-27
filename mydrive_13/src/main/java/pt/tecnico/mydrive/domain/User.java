package pt.tecnico.mydrive.domain;

import java.io.UnsupportedEncodingException;

import org.jdom2.Element;

import pt.tecnico.mydrive.exceptions.*;

public class User extends User_Base {

	public User(){
		super();
	}
    
    public User(MyDrive mydrive, String username, String password, String name, String mask) {
	    super();

	    setUsername(username);
	    setPassword(password);
	    setName(name);
	    setUmask(mask);
	    setMyDrive(mydrive);
	    setUp();
	}

    public Directory getTrueHomedir(){
        MyDrive _mydrive = MyDrive.getInstance();   
        return (Directory) _mydrive.getFileByPath(this.getHomedir());
    }
    
    public void setUp(){
    	Directory home = (Directory) MyDrive.getInstance().getFileByPath("/home");
    	Directory userDir =  new Directory(MyDrive.getInstance(), this, this.getUsername(),
    									   "rwxdr---",
    									   home);

    	this.setHomedir(userDir.getFullPath());
    	this.addUserfiles(userDir);
    }


    public void setTrueHomedir(Directory homeDir){
        this.setHomedir(homeDir.getFullPath());
    }

    
    public void removeUser(User user){
    	throw new InvalidUsernameException("");
    }
    
    
	protected void deleteObject() {
		deleteDomainObject();
	}
    
    
    @Override
    public void setMyDrive(MyDrive mydrive){
    	if(mydrive == null)
    		super.setMyDrive(mydrive);
    	else
    		mydrive.addMydriveusers(this);
    }
    

    public boolean badUsername(String username){

        return(!(username.matches("^[a-zA-Z0-9]*$") && (username.length() >= 3)));
    }

    @Override
    public void setUsername(String username){
 
        if(badUsername(username)){
            throw new InvalidUsernameException(username);
        }
        super.setUsername(username);
    }

    @Override
    public void setPassword(String password){
 
        if(password.length() >= 8 || this.getUsername().equals("root")|| this.getUsername().equals("nobody")){
        	super.setPassword(password);
        	}
        else
        	throw new PasswordTooShortException();
        
    }
    
    public void xmlImport(Element userElement) throws ImportDocumentException{

    	try {
    		setUsername(new String(userElement.getAttributeValue("username").getBytes("UTF-8")));
    		if(userElement.getChildText("name") == null){
    			setName(this.getUsername());
    		}else{
    			setName(new String(userElement.getChildText("name").getBytes("UTF-8")));
    		}
    		
    		if(userElement.getChildText("password") == null){
    			setPassword(this.getUsername());
    		}else{
    			setPassword(new String(userElement.getChildText("password").getBytes("UTF-8")));
    		}
    		
    		if(userElement.getChildText("mask") == null){
    			setUmask("rwxd-----");
    		}else{
        		setUmask(new String(userElement.getChildText("mask").getBytes("UTF-8")));
    		}

    	}catch (UnsupportedEncodingException e){
    		throw new ImportDocumentException();
    	}
    }

    public Element xmlExport() {
        Element userElement = new Element("user");
        userElement.setAttribute("username", getUsername());
        
        Element passwordElement = new Element("password");
        passwordElement.setText(getPassword());
        userElement.addContent(passwordElement);

        Element nameElement = new Element("name");
        nameElement.setText(getName());
        userElement.addContent(nameElement);

        Element homeElement = new Element("home");
        homeElement.setText(getHomedir());
        userElement.addContent(homeElement);
        Element maskElement = new Element("mask");
        maskElement.setText(getUmask());
        userElement.addContent(maskElement);

        return userElement;

    }

    public boolean isRoot() {
    	return (this.getUsername().equals("root"));
    }
    
    public boolean isGuest() {
    	return (this.getUsername().equals("nobody"));
    }
    
}
