package pt.tecnico.mydrive.domain;

import java.lang.reflect.InvocationTargetException;
import java.util.Set;

import org.jdom2.Element;
import org.joda.time.DateTime;

import pt.tecnico.mydrive.exceptions.*;

public class Directory extends Directory_Base {
    
    public Directory(MyDrive mydrive,User owner, String name,
    				String permissions) {
        super();

        setRootDirName(name);
        setUser(owner);
        setFileID(mydrive.getDataID());
        setLastModified(new DateTime());
        setPermissions(permissions);
        setMydrive(mydrive);
        mydrive.setDataID(mydrive.getDataID()+1);
    }
    
    public void exec(String[] args) throws IsDirectoryException, ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
    	throw new IsDirectoryException(this.getFullPath());
    }
    
    public Directory(MyDrive mydrive,User owner, String name, 
			String permissions, Directory dir) throws InvalidFileNameException{

		super();

		if(!canBeCreated(name ,dir)){
    		throw new PathTooLongException();
    	}
		setName(name);
		setUser(owner);
		setFileID(mydrive.getDataID());
		setLastModified(new DateTime());
		setPermissions(permissions);
		setDirectory(dir);
		setMydrive(mydrive);
		mydrive.setDataID(mydrive.getDataID()+1);
		dir.addDirfiles(this);
    }
      
    @Override
    public void deleteFile(){
    	if(getFullPath().equals("/") || getFullPath().equals("/home") ||
    	   getFullPath().equals("/home/root")){
    		throw new FileCantBeRemovedException(getFullPath());
    	}
    	for(File file: getDirfilesSet()){
    		file.deleteFile();
    	}
    	super.deleteFile();
    }
    
    
    public void setRootDirName(String name){
    	super.setRootDirName(name);
    }
    
    public void list(){
    	Directory parentDir = getDirectory();
    	
    	System.out.println(getClass().getSimpleName() + " " +
                getPermissions() + " " + Integer.toString(listSize())
                + " " + getUser().getName() + " " + Integer.toString(getFileID()) + " " 
                + getLastModified().toString("yyyy-MM-dd HH:mm:ss") + " " + ".");

    	System.out.println(parentDir.getClass().getSimpleName() + " " +
               parentDir.getPermissions() + " " + Integer.toString(parentDir.listSize())
                + " " + parentDir.getUser().getName() + " " + Integer.toString(parentDir.getFileID()) + " " 
                + parentDir.getLastModified().toString("yyyy-MM-dd HH:mm:ss") + " " + "..");
        
    	
    	for(File file : getDirfilesSet()){


            if(file.getClass().getSimpleName().equals("Link")){
               System.out.println(file.getClass().getSimpleName() + " " + file.getPermissions() + " " + Integer.toString(file.listSize()) + " " +
                 file.getUser().getName()+ " " + Integer.toString(file.getFileID())+ " " + file.getLastModified().toString("yyyy-MM-dd HH:mm:ss") + " " + file.getName() + "->" + file.showContent() );
            }
            else{
			System.out.println(file.getClass().getSimpleName() + " " +
					file.getPermissions() + " " + Integer.toString(file.listSize())
					+ " " + file.getUser().getName() + " " + Integer.toString(file.getFileID()) + " " 
					+ file.getLastModified().toString("yyyy-MM-dd HH:mm:ss") + " " + file.getName());}
        }
    }
 
    public int listSize(){
        return getDirfilesSet().size();
    }
    
    
    @Override
    public String showContent(){
	    throw new IsDirectoryException(this.getName());
    }
    
    @Override
    public String getContent(){
	    throw new IsDirectoryException(this.getName());
    }

	public void xmlImport(Element dirElement, User user, String name) throws ImportDocumentException{
		setName(name);
		String perm = dirElement.getChildText("perm");
		if(perm == null){
			setPermissions("rwxdr-x-");
		}else{
			setPermissions(perm);
		}
		setLastModified(new DateTime());
		setUser(user);
		
	}
     

    public Element xmlExport(){
        Element dirElement = super.xmlExport();
        dirElement.setName("dir");
        
        return dirElement;
    }
    
    public File hasFile(String name){
        
        Set<File> files = this.getDirfilesSet();
        
        for(File i : files)
            if (i.getName().equals(name)) return i;
        
        return null;
    }
    
    public void setContent(String content){
	    throw new IsDirectoryException(this.getName());
    }
    
    /*public Directory getFileOrDir(String name){
    	if(name.equals(".."))
    		return this.getDirectory();
    	if(name.equals("."))
    		return this;
    	
    		
    	
    }*/
    
    /*public File getFileByRelativePath(String path) throws FileDoesNotExistException{
		String[] file_names = path.split("/", 2);
		File file = null;
		Directory dir= null;
		
		if(path.indexOf("/") == 0){
			for (File f : this.getDirfilesSet()){
				if(f.getName().equals(file_names[0])){
					file = f;
					break;
				}
			}
			return file;
		}
		if(file_names[0].equals("..")){
			return this.getDirectory().getFileByRelativePath(file_names[1]);
		}
		if(file_names[0].equals(".")){
			return this.getFileByRelativePath(file_names[1]);
		}
		else{
			for (File f : this.getDirfilesSet()){
				if(f.getName().equals(file_names[0])){
					dir = (Directory) f;
					break;
				}
				else
					throw new FileDoesNotExistException(file_names[0]);
			}
			return dir.getFileByRelativePath(file_names[1]);
		}
	}*/
    
    public File getFileByRelativePath(String relativePath){
        
        Directory currentDir = this;
        String currentPath = this.getFullPath();
        String pathParts[] = relativePath.split("/");
        
        for(String s : pathParts){
         if(s.equals("..")){
        	 currentDir = currentDir.getDirectory();
        	 currentPath = currentDir.getFullPath();
         }else if(!s.equals("."))
        	 currentPath = currentPath + "/" + s;
         }
     
        return MyDrive.getInstance().getFileByPath(currentPath);
       }
    

}
