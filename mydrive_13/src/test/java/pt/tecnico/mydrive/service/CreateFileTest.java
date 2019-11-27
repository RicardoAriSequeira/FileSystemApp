package pt.tecnico.mydrive.service;

import pt.tecnico.mydrive.domain.*;
import pt.tecnico.mydrive.exceptions.InvalidFileNameException;
import pt.tecnico.mydrive.exceptions.InvalidLinkContentException;
import pt.tecnico.mydrive.exceptions.InvalidSessionException;
import pt.tecnico.mydrive.exceptions.IsDirectoryException;
import pt.tecnico.mydrive.exceptions.IsLinkException;
import pt.tecnico.mydrive.exceptions.NameAlreadyExistsException;
import pt.tecnico.mydrive.exceptions.PathTooLongException;
import pt.tecnico.mydrive.exceptions.UserDoesNotHavePermissionException;

import static org.junit.Assert.*;

import org.joda.time.DateTime;
import org.junit.Test;

public class CreateFileTest extends AbstractServiceTest {
	
	private long token;
	private Directory userDirectory;
	private User user;
	
	@Override
	protected void populate() { //not sure what's needed
		MyDrive mydrive = MyDriveService.getMyDrive();
		
		user = new User(mydrive, "ricardo", "banana123", "Ricardo Pereira", "rwdr----");
		userDirectory = (Directory) mydrive.getFileByPath("/home/ricardo");
		
		new User(mydrive, "ricardo2", "banana1234", "Ricardo Pereira2", "rwdr----");
		
		new Directory(mydrive, user, "notes", "rwxdr---", userDirectory);
	    new PlainFile(mydrive, "note_1", user, "rwxdr---", userDirectory, "");
	    Login log = new Login(mydrive, "ricardo", "banana123");
	    new Login(mydrive, "ricardo2", "banana1234");
	    token = log.getToken();
	    
	}
	
	private File getFileByName(String name){
		File f = MyDriveService.getMyDrive().getFileByName(name);
		return f;		
	}
	
	@Test
    public void successCreatingDirectory() {
    	final String name = "fileName";
        CreateFileService service = new CreateFileService(token, name, "Directory");
        service.execute();
		
		// check file was created
		File file = getFileByName(name);
        assertNotNull("Directory was created", file);
        assertEquals("Valid file name", name, file.getName());
        assertEquals("Valid father directory name", userDirectory, file.getDirectory());
        assertEquals("Valid user name", user, file.getUser());
        assertEquals("Valid permissions", user.getUmask(), file.getPermissions());
        assertEquals("Valid type", "Directory", file.getClass().getSimpleName());
    }
	
	@Test
    public void successCreatingPlainFile() {
    	final String name = "fileName";
        CreateFileService service = new CreateFileService(token, name, "PlainFile");
        service.execute();
		
		// check file was created
		File file = getFileByName(name);
        assertNotNull("PlainFile was created", file);
        assertEquals("Valid file name", name, file.getName());
        assertEquals("Valid father directory name", userDirectory, file.getDirectory());
        assertEquals("Valid user name", user, file.getUser());
        assertEquals("Valid permissions", user.getUmask(), file.getPermissions());
        assertEquals("Valid type", "PlainFile", file.getClass().getSimpleName());
    }

	@Test
    public void successCreatingLink() {
    	final String name = "fileName";
        CreateFileService service = new CreateFileService(token, name, "Link", "/home/ricardo/note_1");
        service.execute();
		
		// check file was created
		File file = getFileByName(name);
        assertNotNull("Link was created", file);
        assertEquals("Valid file name", name, file.getName());
        assertEquals("Valid father directory name", userDirectory, file.getDirectory());
        assertEquals("Valid user name", user, file.getUser());
        assertEquals("Valid permissions", user.getUmask(), file.getPermissions());
        assertEquals("Valid type", "Link", file.getClass().getSimpleName());
    }
	
	@Test
    public void successCreatingApp() {
    	final String name = "fileName";
        CreateFileService service = new CreateFileService(token, name, "App", "app.name");
        service.execute();
		
		// check file was created
		File file = getFileByName(name);
        assertNotNull("App was created", file);
        assertEquals("Valid file name", name, file.getName());
        assertEquals("Valid father directory name", userDirectory, file.getDirectory());
        assertEquals("Valid user name", user, file.getUser());
        assertEquals("Valid permissions", user.getUmask(), file.getPermissions());
        assertEquals("Valid type", "App", file.getClass().getSimpleName());
    }
	
	@Test
    public void successPlainFileWithContent() {
    	final String name = "fileName";
        CreateFileService service = new CreateFileService(token, name, "PlainFile", "hi");
        service.execute();
		
		// check file was created
		File file = getFileByName(name);
        assertNotNull("PlainFile was created", file);
        assertNotNull("PlainFile has content", file.showContent());
        assertEquals("Valid file name", name, file.getName());
        assertEquals("Valid father directory name", userDirectory, file.getDirectory());
        assertEquals("Valid user name", user, file.getUser());
        assertEquals("Valid permissions", user.getUmask(), file.getPermissions());
        assertEquals("Valid type", "PlainFile", file.getClass().getSimpleName());
    }
	
	@Test
    public void successAppWithContent() {
    	final String name = "fileName";
        CreateFileService service = new CreateFileService(token, name, "App", "hi");
        service.execute();
		
		// check file was created
		File file = getFileByName(name);
        assertNotNull("App was created", file);
        assertNotNull("App has content", file.showContent());
        assertEquals("Valid file name", name, file.getName());
        assertEquals("Valid father directory name", userDirectory, file.getDirectory());
        assertEquals("Valid user name", user, file.getUser());
        assertEquals("Valid permissions", user.getUmask(), file.getPermissions());
        assertEquals("Valid type", "App", file.getClass().getSimpleName());
    }
	
	@Test(expected = NameAlreadyExistsException.class)
	public void invalidPlainFileCreationWithDuplicateName() {
    	final String name = "note_1";
        CreateFileService service = new CreateFileService(token, name, "PlainFile");
        service.execute();
    }
	
	@Test(expected = NameAlreadyExistsException.class)
	public void invalidDirectoryFileCreationWithDuplicateName() {
    	final String name = "notes";
        CreateFileService service = new CreateFileService(token, name, "Directory");
        service.execute();
    }
	
	@Test(expected = IsDirectoryException.class)
	public void invalidDirectoryFileCreationWithContent() {
    	final String name = "dir1";
        CreateFileService service = new CreateFileService(token, name, "Directory", "error");
        service.execute();
    }
	
	@Test(expected = PathTooLongException.class)
	public void invalidPlainFileCreationWithPathTooLong() {
    	//1025 character long string - change it so the path itself is 1025
    	final String name = "aSyF0D3B9rpOeWtUKFWmzqVVWMJJ63eSKXftKvpoYhc4JxazZAEZBmYDzeag02MVLS39PPv2j4kcEW3yMDjLZ3EgSAr6r8pgH9tK61Z9GZIb3KPQphixLbN22s2VBRjS0sDYkeQaTCkjtc2Lx87Dp5FcfW9ZWU9XZlSqYGtEF6fvMOV5rhkDYAskgG3By6j7D7yJj36hsv4xXUrne5BEkkKy7wmC4VLK47F2D6z48bHeBKqgo5C15BNaJWIAF5ef8OxAoxPbtFRZSbiPZhjvhmg4ZwK88w9mn8CxQqzmkOW53CNvHRKnqEjLVohxZcKqURRoB8kVchwveZJwW6c4lME9c1yDaDgBF13MrTRJVXjfYU2cVysYPxg8wt6Qen7x6MgqX6hjc3bja0eoQTukVfzKFZSjBW4kwVEEt94vCvlUEWxUGrLQbUWPASMqfNJmQy577jWeUzZNMshmkAZqKBkexNcie1uas2B4JojXGhEOD9FC8Pln369lPDtEoOkbwq0kDKY6vXCfwCT0O8PtTQNCQDgebGaLbzGPM1XhuPHBtZ3FNgZAX4ntv4uVyYBMrKGxJCSgvH8DFYY42EkOPQOMuln32znSbvLertbgqzUqjpF4cj2UGUMSphjoWAb3EBJTSH1mg15NGTgULCqgExc8o4IA1rKX4cnG8nVw1xcEOnske2jO3zYihh1wJAINs4r2FJHxLGenYkxrRowxRfkjzb2XkxpLLKwGAUHJUNWzWDlnHsXY0T1kwXsWYVT1a4fwTV4jwxpwDNnoTbXap9CQTvSqjOOo80Xpnawu5rcXHMO63SsbbNVGes1UPTCUvieGS40rp3yCI71YQs3NmQoLkTovuJwvT4NJOKUG8QfVwA9ztX3Bc0nUcUqZ1XVpKxVsmhmqE3T0rXi7kJ3s9hAaXDWwiFPuxPyc93iCCyCRpsfMgsfoGFKkw3yQhkCwJgGrnaHYyhUDJaVwQoqe72wLx6itnhR07yVqkkVeM2yg5tPEBaONCbm9RvqVmXL8k";
        CreateFileService service = new CreateFileService(token, name, "PlainFile", "hi");
        service.execute();
    }
	
	@Test(expected = InvalidLinkContentException.class)
    public void invalidLinkCreationWithoutPathAsContent() {
    	final String name = "fileName";
        CreateFileService service = new CreateFileService(token, name, "Link", "error");
        service.execute();
    }
	
	@Test(expected=InvalidSessionException.class)
	public void ExpiredSession(){
		final String name = "fileName";
		
		MyDrive mydrive = MyDriveService.getMyDrive();
		Login log = mydrive.getSessionByToken(token);
		log.setLastAccess(new DateTime().minusHours(3));
		
        CreateFileService service = new CreateFileService(token, name, "Directory");
        service.execute();
	}
	
	@Test(expected=InvalidFileNameException.class)
	public void failurePlainFileWithBadName(){
		final String name = "file/Name";
        CreateFileService service = new CreateFileService(token, name, "PlainFile");
        service.execute();
	}
	
	@Test(expected=InvalidFileNameException.class)
	public void failureDirectoryWithBadName(){
		final String name = "file/Name";
        CreateFileService service = new CreateFileService(token, name, "Directory");
        service.execute();
	}
	
	@Test(expected=UserDoesNotHavePermissionException.class)
	public void failureCreatingPlainFileWithoutPermissions(){
		Login log = MyDrive.getInstance().getSessionByToken(token);
		log.setCurrentDir((Directory) MyDrive.getInstance().getFileByPath("/home/ricardo2"));
		final String name = "fileName";
        CreateFileService service = new CreateFileService(token, name, "PlainFile");
        service.execute();
	}
	
	@Test(expected=UserDoesNotHavePermissionException.class)
	public void failureCreatingDirectoryWithoutPermissions(){
		Login log = MyDrive.getInstance().getSessionByToken(token);
		log.setCurrentDir((Directory) MyDrive.getInstance().getFileByPath("/home/ricardo2"));
		final String name = "fileName";
        CreateFileService service = new CreateFileService(token, name, "Directory");
        service.execute();
	}
}
