package pt.tecnico.mydrive.domain;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;

import java.util.ArrayList;
import java.util.List;

import org.jdom2.Element;
import org.joda.time.DateTime;

import pt.tecnico.mydrive.exceptions.ImportDocumentException;
import pt.tecnico.mydrive.exceptions.IsDirectoryException;
import pt.tecnico.mydrive.exceptions.IsLinkException;
import pt.tecnico.mydrive.exceptions.LinkLoopException;


public class Link extends Link_Base {
    
	private List<File> _fileArray = new ArrayList<File>();
	
    public Link() {
        super();
    }
    
    public Link(MyDrive mydrive, String name, User owner,
			 String permissions ,Directory dir, String content){
		setName(name);
		setUser(owner);
		setFileID(mydrive.getDataID());
		setPermissions(permissions);
		setDirectory(dir);
		super.setContent(content);
		setMydrive(mydrive);
		mydrive.setDataID(mydrive.getDataID() + 1);
		setLastModified(new DateTime());
		dir.addDirfiles(this);
		}
	
    public void exec(String[] args) throws ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
    	this.getLinkedFile().exec(args);
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
    	File file = MyDrive.getInstance().getFileByPath(getContent());
    	
    	if(file.getFullPath().equals(this.getFullPath())){
    		throw new LinkLoopException();
    	}
    	
    	if(_fileArray.contains(file)){
    		throw new LinkLoopException();
    	}
    	
    	_fileArray.add(file);
    	
    	return file.showContent();
    }
    
    public File getLinkedFile(){
        File file = MyDrive.getInstance().getFileByPath(getContent());
        
        if(file.getFullPath().equals(this.getFullPath())){
            throw new LinkLoopException();
        }
        
        if(_fileArray.contains(file)){
            throw new LinkLoopException();
        }
        
        _fileArray.add(file);
        
        return file;
    }
    
    
    
    public Element xmlExport(){
    	Element linkElement = super.xmlExport();
    	linkElement.setName("link");
    	
    	linkElement.getChild("contents").setName("value");
    	
    	return linkElement;
    }
    
    @Override
    public void setContent(String content){
	    throw new IsLinkException(this.getName());
    }
    
    @Override
    public String getContent(){
	    return super.getContent();
    }
    

}
