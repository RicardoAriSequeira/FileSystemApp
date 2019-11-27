package pt.tecnico.mydrive.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import pt.tecnico.mydrive.domain.User;
import pt.tecnico.mydrive.domain.Directory;
import pt.tecnico.mydrive.domain.MyDrive;
import pt.tecnico.mydrive.domain.PlainFile;
import pt.tecnico.mydrive.domain.Login;
import pt.tecnico.mydrive.exceptions.*;
import org.joda.time.DateTime;

public class ChangeDirectoryTest extends AbstractServiceTest {

    
    
    protected void populate() {
    	
    	MyDrive mydrive = MyDrive.getInstance();
        
        Directory dirHome, dirMiguel, dir2, dir3, dirdotdot;
        User cdUser, permissionsUser;
        
        dirHome = mydrive.getHomeDirectory();
        cdUser = new User(mydrive, "miguel", "pw1234567", "Miguel Marques", "rwxdr---");
        permissionsUser = new User(mydrive,  "permissoes", "pw12348910", "Mike Marques", "rwxdr---");
        dirMiguel = (Directory) mydrive.getFileByPath("/home/miguel");
        dir2 = new Directory(mydrive, cdUser, "testcd1", "rwxdr---", dirMiguel);
        dir3 = new Directory(mydrive, cdUser, "testcd2", "rwxdr---", dir2);
        new Directory(mydrive, cdUser, "testcd1", "rwxdr---", dir3);
        new Directory(mydrive, permissionsUser, "permcd1", "rwxd----", dir3);
        dirdotdot = new Directory(mydrive, permissionsUser, "permcddotdot", "rwxd----", dirHome);
        new Directory(mydrive, cdUser, "dir9000", "rwxdr---", dirdotdot);
        new PlainFile(mydrive, "ficheiro", cdUser, "rwxdr---", dir3, "ficheiro");
        
    }

    @Test
    public void successNull() {
        MyDrive mydrive = MyDrive.getInstance();
        Directory home = (Directory) mydrive.getFileByPath("/home");
        Login log = new Login(mydrive,"miguel","pw1234567");
        long token = log.getToken();
        log.setCurrentDir(home);
        ChangeDirectoryService service = new ChangeDirectoryService(token, null);
        service.execute();

        assertEquals("User was moved to his home dir", log.getUser().getHomedir(), "/home/miguel"); 

    }


    
    
    @Test
    public void successDot() {
    	MyDrive mydrive = MyDrive.getInstance();
    	Login log = new Login(mydrive,"miguel","pw1234567");
        long token = log.getToken();
    	ChangeDirectoryService service = new ChangeDirectoryService(token, "."); 
        service.execute();
        String file = service.result();
        Directory cdFile = (Directory) mydrive.getFileByPath(file);
        assertEquals("Displays current directory( /home/miguel )", log.getCurrentDir().getFullPath(), cdFile.getFullPath());

    }
    
    
    
    @Test
    public void successDoubleDot() {
    	MyDrive mydrive = MyDrive.getInstance();
    	Login log = new Login(mydrive,"miguel","pw1234567");
        long token = log.getToken();
    	ChangeDirectoryService service = new ChangeDirectoryService(token, "..");
        service.execute();
        String file = service.result();
        Directory cdFile = (Directory) mydrive.getFileByPath(file);
        assertEquals("Changes session status to parent directory ( /home )", log.getCurrentDir().getFullPath(), cdFile.getFullPath());
    }
    
    

    @Test
    public void successRelative() {
    	MyDrive mydrive = MyDrive.getInstance();
    	Login log = new Login(mydrive,"miguel","pw1234567");
        long token = log.getToken();
        
        Directory testCD = (Directory) mydrive.getFileByPath("/home/miguel/testcd1/testcd2");
        log.setCurrentDir(testCD);
    	ChangeDirectoryService service = new ChangeDirectoryService(token, "testcd1");
        service.execute();
        String file = service.result();
        Directory cdFile = (Directory) mydrive.getFileByPath(file);
           
        assertEquals("Changes session status to relative child directory ", log.getCurrentDir().getFullPath(), cdFile.getFullPath());
    }
    
    @Test
    public void successDoubleRelative() {
    	MyDrive mydrive = MyDrive.getInstance();
    	Login log = new Login(mydrive,"miguel","pw1234567");
        long token = log.getToken();
        
        Directory testCD = (Directory) mydrive.getFileByPath("/home/miguel/testcd1");
        log.setCurrentDir(testCD);
    	ChangeDirectoryService service = new ChangeDirectoryService(token, "testcd2/testcd1");
        service.execute();
        String file = service.result();
        Directory cdFile = (Directory) mydrive.getFileByPath(file);
           
        assertEquals("Changes session status to relative child directory ", log.getCurrentDir().getFullPath(), cdFile.getFullPath());
    }
    
    @Test
    public void successAbsolute() {
    	MyDrive mydrive = MyDrive.getInstance();
    	Login log = new Login(mydrive,"miguel","pw1234567");
        long token = log.getToken();
        
        Directory testCD = (Directory) mydrive.getFileByPath("/home/miguel/testcd1/testcd2");
        log.setCurrentDir(testCD);
    	ChangeDirectoryService service = new ChangeDirectoryService(token, "/home/miguel/testcd1");
        service.execute();
        String file = service.result();
        Directory cdFile = (Directory) mydrive.getFileByPath(file);
           
        assertEquals("Changes session status to absolute child directory ",log.getCurrentDir().getFullPath(), cdFile.getFullPath());
    }

    @Test(expected = FileDoesNotExistException.class)
    public void pathDoesNotExist() {
    	MyDrive mydrive = MyDrive.getInstance();
    	Login log = new Login(mydrive,"miguel","pw1234567");
        long token = log.getToken();
        
        Directory testCD = (Directory) mydrive.getFileByPath("/home/miguel/testcd1/testcd2");
        log.setCurrentDir(testCD);
    	ChangeDirectoryService service = new ChangeDirectoryService(token, "/home/miguel/testcd1/asdasd");
        service.execute();
        }

    @Test(expected = IsNotDirectoryException.class)
    public void fileIsNotDirectory() {
    	MyDrive mydrive = MyDrive.getInstance();
    	Login log = new Login(mydrive,"miguel","pw1234567");
        long token = log.getToken();
        
        Directory testCD = (Directory) mydrive.getFileByPath("/home/miguel/testcd1/testcd2");
        log.setCurrentDir(testCD);
    	ChangeDirectoryService service = new ChangeDirectoryService(token, "/home/miguel/testcd1/testcd2/ficheiro");
        service.execute();
        }

    @Test(expected = InvalidSessionException.class)
    public void invalidSession() {
    	MyDrive mydrive = MyDrive.getInstance();
    	Login log = new Login(mydrive,"miguel","pw1234567");
    	log.setLastAccess(new DateTime().minusHours(3));
        long token = log.getToken();
        ChangeDirectoryService service = new ChangeDirectoryService(token,"/home/miguel");
        service.execute();
    }
    
    @Test(expected = UserDoesNotHavePermissionException.class)
    public void noPermissions() {
    	MyDrive mydrive = MyDrive.getInstance();
    	Login log = new Login(mydrive,"miguel","pw1234567");
    	long token = log.getToken();
    	ChangeDirectoryService service = new ChangeDirectoryService(token,"/home/miguel/testcd1/testcd2/permcd1");
    	service.execute();
    }
    // @Test(expected = UserDoesNotHavePermissionException.class)
    // public void noPermissionsDot() {
    // 	MyDrive mydrive = MyDrive.getInstance();
    // 	Login log = new Login(mydrive,"miguel","pw1234567");
    // 	long token = log.getToken();
    // 	log.setCurrentDir((Directory) mydrive.getFileByPath("/home/permcddotdot"));
    // 	ChangeDirectoryService service = new ChangeDirectoryService(token,".");
    // 	service.execute();
    // }
    
    @Test(expected = UserDoesNotHavePermissionException.class)
    public void noPermissionsDotDot() {
    	MyDrive mydrive = MyDrive.getInstance();
    	Login log = new Login(mydrive,"miguel","pw1234567");
    	long token = log.getToken();
    	log.setCurrentDir((Directory) mydrive.getFileByPath("/home/permcddotdot/dir9000"));
    	ChangeDirectoryService service = new ChangeDirectoryService(token,"..");
    	service.execute();
    }

}
