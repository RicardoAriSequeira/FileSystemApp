package pt.tecnico.mydrive.domain;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;

import org.jdom2.Element;
import org.joda.time.DateTime;

import pt.tecnico.mydrive.exceptions.*;


public class App extends App_Base {
    
    public App() {
        super();
    }

    public App(MyDrive mydrive, String name, User owner,
			 String permissions ,Directory dir, String content){
    	
		setName(name);
		setUser(owner);
		setFileID(mydrive.getDataID());
		setPermissions(permissions);
		setDirectory(dir);
		setContent(content);
		setMydrive(mydrive);
		mydrive.setDataID(mydrive.getDataID() + 1);
		setLastModified(new DateTime());
		dir.addDirfiles(this);
		}
	
	@Override
	public void setContent(String content) throws InvalidAppContentException{
		this.checkValidJavaName(content);
		super.setContent(content);
	}
    
    
	public void xmlImport(Element linkElement, User user, String permissions, String content,
		Directory dir){ 
		try{
			setName(new String(linkElement.getChildText("name").getBytes("UTF-8")));
			setPermissions(permissions);
			if(content == null){
				setContent("");
			}else{
				setContent(content);
			}
			setLastModified(new DateTime());
			setUser(user);
			setDirectory(dir);
		}catch(UnsupportedEncodingException e){
			throw new ImportDocumentException();
		}
	}

    @Override
    public String showContent(){
    	return this.getContent();
    }
    
    public void exec(String[] args) throws ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException{
    	pt.tecnico.mydrive.presentation.Shell.run(this.getContent(), args);
    }
    
    public Element xmlExport(){
    	Element appElement = super.xmlExport();
    	appElement.setName("app");
    	appElement.getChild("contents").setName("method");
    	
    	return appElement;
    }
    
}
   
