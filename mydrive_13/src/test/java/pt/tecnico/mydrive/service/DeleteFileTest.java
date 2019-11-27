package pt.tecnico.mydrive.service;

import org.junit.Test;

import pt.tecnico.mydrive.domain.*;
import pt.tecnico.mydrive.exceptions.*;
import static org.junit.Assert.*;



public class DeleteFileTest extends AbstractServiceTest {

	private long token;
	private Directory userDirectory;
	private User usr1;
	private User usr2;
	private Root root;
	private long rootToken;
	
	protected void populate(){
		MyDrive md = MyDrive.getInstance();
        root = (Root) md.getUserByName("root");
        
        Directory dir1_test1, dir2_test1;
       
        usr1 = new User(md, "ricardo", "123456789", "Ricardo", "rwxdr---");
        usr2 = new User(md, "zemen", "567891011", "Jose", "rwxdr---");
        userDirectory = (Directory) md.getFileByPath("/home/ricardo");
        new Directory(md, usr1, "teste", "rwxdr---", userDirectory);
        new PlainFile(md, "file1_usr1", usr1, "rwxdr---", userDirectory, "lelelelelel");
        new PlainFile(md, "file1_usr2", usr2, "rwxdr---", userDirectory, "lalalalalal");
        new PlainFile(md, "file2_usr2", usr2, "rwxdr---", userDirectory, "blqlalasfwea");
        dir1_test1 = new Directory(md, usr1, "dir1_usr1", "xwxdr---", userDirectory);
        new PlainFile(md, "file2_usr1", usr1, "rwxdr---", dir1_test1, "asdfasdfasdf");
        dir2_test1 = new Directory(md, usr1, "dir2_usr1", "xwxdr---", dir1_test1);
        new PlainFile(md, "file3_usr1", usr1, "rwxdr---", dir2_test1, "ededededededed");
        new PlainFile(md, "file4_usr1", usr1, "rwxdr---", dir2_test1, "ededededededed");
        
        Login log = new Login(md, "ricardo", "123456789");
	    token = log.getToken();
	    Login rootLog = new Login(md, root.getUsername(), root.getPassword());
	    rootToken = rootLog.getToken();
	    
	}
	
	private File getFileByName(String name){
		File f = MyDriveService.getMyDrive().getSessionByToken(token).getCurrentDir().hasFile(name);
		return f;		
	}

	@Test
    public void successDeleteFile() {
    	final String name = "file1_usr1";
    	DeleteFileService service = new DeleteFileService(token, name);
        service.execute();
        File file = getFileByName(name);
        assertNull("file deleted",file);
    }
	
	@Test
    public void successDeleteDirectoryWithFilesAndDirectories() {
    	final String name = "dir1_usr1";
        DeleteFileService service = new DeleteFileService(token, name);
        service.execute();
		
		// check all the files in the directory were deleted
        File fileInDir1 = getFileByName("file2_usr1");
        File file1InDir2 = getFileByName("file3_usr1"); 
        File file2InDir2 = getFileByName("file4_usr1");
        File Dir1 = getFileByName(name);
        File Dir2 = getFileByName("dir2_usr1");
        assertNull("file1 in dir2 deleted", file1InDir2);
        assertNull("file2 in dir2 deleted", file2InDir2);
        assertNull("dir2 deleted", Dir2);
        assertNull("file in dir1 deleted", fileInDir1);
        assertNull("dir1 deleted", Dir1);
    }
	
	@Test
	public void rootDeleteFile(){
		final String name = "file1_usr2";
		Login log = MyDrive.getInstance().getSessionByToken(rootToken);
		log.setCurrentDir(userDirectory);
		DeleteFileService service = new DeleteFileService(rootToken, name);
		service.execute();
		
		File file = getFileByName(name);
		assertNull("root deleted file1_usr2", file);
	}
	
	@Test(expected = FileDoesNotExistException.class)
    public void deleteNonExistingFile() {
		final String name = "bolacha";
		DeleteFileService service = new DeleteFileService(token, name);
		service.execute();
    }
	
	@Test(expected = UserDoesNotHavePermissionException.class)
    public void deleteFileFromOtherOwner() {
		final String name = "file2_usr2";
		DeleteFileService service = new DeleteFileService(token, name);
		service.execute();
    }
	
	@Test(expected = UserDoesNotHavePermissionException.class)
	public void userCantDeleteHome(){
		final String name = "home";
		Login log = MyDrive.getInstance().getSessionByToken(token);
		log.setCurrentDir((Directory) MyDrive.getInstance().getFileByPath("/"));
		DeleteFileService service = new DeleteFileService(token, name);
		service.execute();
	}
	
	@Test(expected = FileCantBeRemovedException.class)
	public void rootCantDeleteHome(){
		final String name = "home";
		Login log = MyDrive.getInstance().getSessionByToken(rootToken);
		log.setCurrentDir((Directory) MyDrive.getInstance().getFileByPath("/"));
		DeleteFileService service = new DeleteFileService(rootToken, name);
		service.execute();
	}
}