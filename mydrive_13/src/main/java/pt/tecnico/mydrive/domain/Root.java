package pt.tecnico.mydrive.domain;

public class Root extends Root_Base {
	
    public Root() {
    	super();
        setUsername("root");
        setPassword("***");
        setName("Super User");
        setUmask("rwxdr-x-");
        setMyDrive(MyDrive.getInstance());
        setUp();
    }
    
    @Override
    public void setUp(){
    	MyDrive mydrive = MyDrive.getInstance();
    	
    	Directory baseDir = new Directory(mydrive, this, "/", "rwxdr-x-");
        baseDir.setDirectory(baseDir);
       
        Directory homeDir = new Directory(mydrive, this, "home", "rwxdr-x-", baseDir);

        Directory rootDir = new Directory(mydrive, this, "root", "rwxdr-x-", homeDir);
        this.setHomeDirectory(rootDir);
    }

    public void setHomeDirectory(Directory directory) {
        setTrueHomedir(directory);
        addUserfiles(directory);
    }
   
    
    @Override
    public void removeUser(User user){
    	MyDrive mydrive = MyDrive.getInstance();
    	
    	if(!user.isRoot() && !user.isGuest()){
    		mydrive.getFileByPath(user.getHomedir()).deleteFile(); 
    		mydrive.getMydriveusersSet().remove(user); 		
    		user.setMyDrive(null);
    		user.deleteObject();  
    	}
    }
}

