package pt.tecnico.mydrive.domain;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

import org.jdom2.Element;
import org.joda.time.DateTime;

import pt.tecnico.mydrive.exceptions.*;


public class PlainFile extends PlainFile_Base {
    
    public PlainFile() {
        super();
    }
    
    
    public PlainFile(MyDrive mydrive, String name, User owner,
    				 String permissions ,Directory dir, String content){

    	if(!canBeCreated(name ,dir)){
    		throw new PathTooLongException();
    	}
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
    
    public void exec(String[] args) throws ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
    	String content = this.showContent();
    	String[] entersplit = content.split("\n");
    	for(String s: entersplit){
    		String[] aux = s.split(" ");
    		this.checkValidJavaName(aux[0]);
    		String[] arguments = Arrays.copyOfRange(aux, 1, aux.length);
    		pt.tecnico.mydrive.presentation.Shell.run(aux[0], arguments);
    	}
    }
    
    
    public void xmlImport(Element plainElement, User user, String permissions, String content,
    					  Directory dir){ 
    	try{
	    	setName(new String(plainElement.getChildText("name").getBytes("UTF-8")));
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

    public int listSize(){
        return getContent().length();
    }
    
    public Element xmlExport(){
    	Element plainElement = super.xmlExport();
    	plainElement.setName("plain");
    	
    	Element contentsElement = new Element("contents");
    	contentsElement.setText(this.getContent());
    	plainElement.addContent(contentsElement);
    	
    	return plainElement;

    }
    
}
