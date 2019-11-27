    package pt.tecnico.mydrive.domain;
    import java.math.BigInteger;
    import java.util.*;
    import org.apache.logging.log4j.LogManager;
    import org.apache.logging.log4j.Logger;

    import pt.tecnico.mydrive.exceptions.*;

import org.jdom2.Element;
import org.joda.time.DateTime;
import org.jdom2.Document;

import pt.ist.fenixframework.FenixFramework;

    public class MyDrive extends MyDrive_Base {

    static final Logger log = LogManager.getRootLogger();

    public static MyDrive getInstance(){
    	MyDrive mydrive = FenixFramework.getDomainRoot().getMydrive();
    	if(mydrive == null){
    		mydrive = new MyDrive();
    		try{ new Root(); }catch(NameAlreadyExistsException e){}
    		try{ new Guest(mydrive); }catch(NameAlreadyExistsException e){}
    	}
    	
    	return mydrive;
    }

    private MyDrive(){
    	setDataID(0);
    	setRoot(FenixFramework.getDomainRoot());
    }
    
    public void cleanup(){
    	User root = this.getUserByName("root");
    	
    	for(User u : this.getMydriveusersSet()){
    		root.removeUser(u);
    	}
   }

    
    public User getUserByName(String username) {
        for (User user : getMydriveusersSet()) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    public File getFileByName(String name){
	    for(File file : getMydrivefilesSet()){
	    	if(file.getName().equals(name)){
	    		return file;
	    	}
	    }
	    return null;
    }

    public boolean hasUsername(String username) {
        return getUserByName(username) != null;
    }

   
    @Override
    public void addMydriveusers(User userToBeAdded) throws InvalidUsernameException, NameAlreadyExistsException { 
       if (userToBeAdded.badUsername(userToBeAdded.getUsername())){
             throw new InvalidUsernameException(userToBeAdded.getUsername());
         }

       if (hasUsername(userToBeAdded.getUsername())){
            throw new NameAlreadyExistsException(userToBeAdded.getUsername());
       }

        super.addMydriveusers(userToBeAdded);
    }

    
	public File getFileByPath(String path) throws FileDoesNotExistException{
	    	for(File file: getMydrivefilesSet()){
                            if(file.getFullPath().equals(path)){
                                return file;
                            }
	    	}
	    	throw new FileDoesNotExistException(path);
	    }
	
	public Boolean checkFileByPath(String path) {
    	for(File file: getMydrivefilesSet()){

    		if(file.getName().equals("/") && path.equals("/")){
    				return true;
    		}

    		if(file.getDirectory().getName().equals("/")){
    			String filePath = file.getPath() + file.getName();
    			if(filePath.equals(path)){
    				return true;
    			}
    		}

    		String filePath = file.getPath() + "/" + file.getName();
    		if(filePath.equals(path))
    			return true;
    	}
    	return false;
    }
	
	
	public void xmlImport(Element element){
		for(Element node : element.getChildren()){
			
			if(node.getName().equals("user")){
				String username = node.getAttributeValue("username");
				User u = getUserByName(username);
				String password = node.getChildText("password");
				String name = node.getChildText("name");
				String mask = node.getChildText("mask");
				
				if(u == null){
					u = new User(this, username, password, name, mask);
				}
				u.xmlImport(node);
				
			}else{
				File f = null;
				String content = "";
				String name = node.getChildText("name");
				String path = node.getChildText("path");
				String owner = node.getChildText("owner");
				User user = getUserByName(owner);
				String perm = node.getChildText("perm");
				Directory parentDir = (Directory) getFileByPath(path);

				if(!checkFileByPath(path + "/" + name)){
					if(node.getName().equals("dir")){
						new Directory(this, user, name, perm, parentDir);
					}else if(node.getName().equals("plain")){
						content = node.getChildText("contents");
						new PlainFile(this, name, user, perm, parentDir, content);
					}else if(node.getName().equals("app")){
						content = node.getChildText("method");
						new App(this, name, user, perm, parentDir, content);
					}else if(node.getName().equals("link")){
						content = node.getChildText("value");
						new Link(this, name, user, perm, parentDir, content);
					}
				}else{
					f = getFileByPath(path + "/" + name);
					if(node.getName().equals("dir")){
						((Directory) f).xmlImport(node, user, perm);
					}else if(node.getName().equals("plain")){
						((PlainFile) f).xmlImport(node, user, perm, content, parentDir);
					}else if(node.getName().equals("app")){
						((App) f).xmlImport(node, user, perm, content, parentDir);
					}else if(node.getName().equals("link")){
						((Link) f).xmlImport(node, user, perm, content, parentDir);
					}
				}
			}
		}
	}

    public Document xmlExport() {
            Element element = new Element("myDrive");
            Document doc = new Document(element);

            for(User u: getMydriveusersSet())
                if(!(u.getUsername().equals("root"))){
                element.addContent(u.xmlExport());
            }
        
            List<File> fileList = new ArrayList<File>(getMydrivefilesSet());
            Collections.sort(fileList, (o1, o2) -> ((o1.getFileID()) - (o2.getFileID())));
            
            for(File f: fileList){
		if((f.getFileID() != 0) && (f.getFileID() != 1))
                    if(!((f.getName().equals("/home")) && (f.getName().equals("/")))){
            		  element.addContent(f.xmlExport());
                }
        }
            return doc;
    }
    
    
    public String getListUsers() {
    	
    	String usersList = "";
    	Set<User> users = this.getMydriveusersSet();
    	
    	for (User u : users) usersList = usersList + u.getName() + '\n';
    	
    	return usersList;
    }	
    
    
    public Directory getHomeDirectory() {
    	return (Directory) this.getFileByPath("/home");
    }
    
    public Login getSessionByToken(long token) throws SessionNotFoundException {
    	for(Login log: getLoginSet()){
    		if(log.getToken() == token){
    			return log;
    		}
    	
    	}
            throw new SessionNotFoundException(token);
        
    }
    
    public Login getGuestSession() {
    	
    	Set<Login> logins = getLoginSet();
    	
    	for(Login l : logins){
    		if(l.getUser().getName().equals("Guest")){
    			return l;
    		}
    	}
    	
    	return null;
    	
    }
    public int checkSession(Login l){
        if(l.getUser().isRoot()){
            return l.getLastAccess().compareTo(new DateTime().minusMinutes(10));
        }

        else {
    	return l.getLastAccess().compareTo(new DateTime().minusHours(2));
        }
    }
    
    public void refreshSession(Login l) throws InvalidSessionException{
    	if((this.checkSession(l) <= 0) && (!l.getUser().isGuest())){
    		throw new InvalidSessionException();
    	}
    	l.setLastAccess(new DateTime());
    }
    
    public void clearOldSessions(){
        for(Login l: this.getLoginSet()){
            if( ((l.getUser().isRoot()) && (l.getLastAccess().compareTo(new DateTime().minusMinutes(10))) <= 0) || (l.getLastAccess().compareTo(new DateTime().minusHours(2)) <= 0) ){
            	l.getCurrentDir().getLoginSet().remove(l);
            	l.getUser().getLoginSet().remove(l);
            	this.getLoginSet().remove(l);
          
            	//l.deleteDomainObject();
            } 
        }
    }
    public long createToken(){
    	List<Long> tokens = new ArrayList<Long>();
    	if(!(getLoginSet().isEmpty())){
    		for(Login l: getLoginSet()){
    			tokens.add(l.getToken());
    		}
    	}
    	long token = new BigInteger(64, new Random()).longValue();
    	
    	while(tokens.contains(token))
    		token = new BigInteger(64, new Random()).longValue();
    	return token;
    }
    
	public File resolveEnvironmentLink(File mockedLink){
    	//TODO: mockup
    	return null;
    }
	
	public PlainFile execAssociation(PlainFile file){
    	//TODO: mockup
    	return null;
    }
    
}
