package pt.tecnico.mydrive.service;

import static org.junit.Assert.assertEquals;

import org.joda.time.DateTime;
import org.junit.Test;

import mockit.Mock;
import mockit.MockUp;
import pt.tecnico.mydrive.domain.*;
import pt.tecnico.mydrive.exceptions.FileDoesNotExistException;
import pt.tecnico.mydrive.exceptions.InvalidSessionException;
import pt.tecnico.mydrive.exceptions.IsLinkException;
import pt.tecnico.mydrive.exceptions.IsDirectoryException;
import pt.tecnico.mydrive.exceptions.UserDoesNotHavePermissionException;


public class WriteFileTest extends AbstractServiceTest {
	
	private long token1, token2;
	private Link mockedLink, realLink;
	
	protected void populate(){		
		MyDrive mydrive = MyDrive.getInstance();
		
		Directory userDirectory, dir, dir2;
		User user1;
		
		user1 = new User(mydrive, "diogoj", "a1b2c3d4", "Diogo Jerónimo", "rwdr----");
		userDirectory = (Directory) mydrive.getFileByPath("/home/diogoj");
		
		dir = new Directory(mydrive, user1, "notes", "rwxd----", userDirectory);
		
		dir2 = new Directory(mydrive, user1, "notes2", "rwxd----", dir);
		new PlainFile(mydrive, "note_3", user1, "rwxd----", dir2, "");
		new PlainFile(mydrive, "note_1", user1, "rwxd----", dir, "");
		new PlainFile(mydrive, "note_2", user1, "rwxd----", dir, "Insert content here");
		realLink = new Link(mydrive, "shortcut", user1, "rwxd----", dir, "/home");
		mockedLink = new Link(mydrive, "shortcut", user1, "rwxd----", dir, "/$HOME_DIR");
		new App(mydrive, "app", user1, "rwxd----", dir, "pt.tecnico.MyDrive.app.hello");
		new Directory(mydrive, user1, "new_dir", "rwxd----", dir);
		
		new User(mydrive, "rand0m", "lol123456", "random", "rwdr---");


	    Login log1 = new Login(mydrive, "diogoj", "a1b2c3d4");  
	    log1.setCurrentDir(dir);
	    Login log2 = new Login(mydrive, "rand0m", "lol123456");
	    log2.setCurrentDir(dir);
	    token1 = log1.getToken();
	    token2 = log2.getToken();

	}

	@Test
	public void writeEmptyFile(){
		MyDrive mydrive = MyDrive.getInstance();

		WriteFileService service = new WriteFileService(token1, "note_1", "Hello World !");
		service.execute();
		
		PlainFile file = (PlainFile) mydrive.getFileByPath("/home/diogoj/notes/note_1");
		
		assertEquals("Testing writing in empty file", 
					 "Hello World !", 
					 file.getContent());
	}
	
	@Test
	public void writeNonEmptyFile(){
		MyDrive mydrive = MyDrive.getInstance();
		WriteFileService service = new WriteFileService(token1, "note_2", "Text to insert in note 2\n"); 
		service.execute();
	
		PlainFile file = (PlainFile) mydrive.getFileByPath("/home/diogoj/notes/note_2");
		
		assertEquals("Testing writing in non-empty file",
					 "Text to insert in note 2\n", 
					 file.getContent());
	}
	
	@Test
	public void WriteFileOnRelativePath(){
		MyDrive mydrive = MyDrive.getInstance();
		WriteFileService service = new WriteFileService(token1, "notes2/note_3", "Text to insert in note 2\n"); 
		service.execute();
	
		PlainFile file = (PlainFile) mydrive.getFileByPath("/home/diogoj/notes/notes2/note_3");
		
		assertEquals("Testing writing in non-empty file",
					 "Text to insert in note 2\n", 
					 file.getContent());
	
	}
	
	@Test
	public void writeFileOnRelativePathWithParentDir(){
		MyDrive mydrive = MyDrive.getInstance();
		WriteFileService service = new WriteFileService(token1, "../notes/notes2/note_3", "Text to insert in note 2\n"); 
		service.execute();
	
		PlainFile file = (PlainFile) mydrive.getFileByPath("/home/diogoj/notes/notes2/note_3");
		
		assertEquals("Testing writing in non-empty file",
					 "Text to insert in note 2\n", 
					 file.getContent());
	
	}
	
	@Test
	public void writeFileOnRelativeFileWithMoreThanOneCurrentDir(){
		MyDrive mydrive = MyDrive.getInstance();
		WriteFileService service = new WriteFileService(token1, "./././notes2/note_3", "Text to insert in note 2\n"); 
		service.execute();
	
		PlainFile file = (PlainFile) mydrive.getFileByPath("/home/diogoj/notes/notes2/note_3");
		
		assertEquals("Testing writing in non-empty file",
					 "Text to insert in note 2\n", 
					 file.getContent());
	
	}
	
	@Test (expected=FileDoesNotExistException.class)
	public void FileDoesNotExistInParentDir() {
		WriteFileService service = new WriteFileService(token1, "../notes2/note_3", "Text to insert in note 2\n"); 
		service.execute();
	}
	
	@Test(expected=IsDirectoryException.class)
	public void writeDirectory(){	
		WriteFileService service = new WriteFileService(token1, "new_dir", "This is a text\n");
		service.execute();
	}
	
	@Test(expected=UserDoesNotHavePermissionException.class)
	public void writeWithoutPermission(){
		WriteFileService service = new WriteFileService(token2, "note_2", "SWASFOISAOFNASASXVV"); 
		service.execute();		
	}
	
	@Test(expected=FileDoesNotExistException.class)
	public void writeNonExistingFile(){
		WriteFileService service = new WriteFileService(token1, "nota001", "Escrever este texto\n"); 
		service.execute();
	}
	
	@Test(expected = IsLinkException.class)
	public void writeLink(){
		WriteFileService service = new WriteFileService(token1, "shortcut", "Tryinh to write in a link\n");
		service.execute();
	}
	@Test
	public void writeApp(){
		MyDrive mydrive = MyDrive.getInstance();
		WriteFileService service = new WriteFileService(token1, "app", "pt.tecnico.MyDrive.app.goodbye");
		service.execute();
		
		App file = (App) mydrive.getFileByPath("/home/diogoj/notes/app");
		
		assertEquals("Testing writing in an app",
					 "pt.tecnico.MyDrive.app.goodbye",
					 file.getContent());
	}
	
	@Test(expected=InvalidSessionException.class)
	public void writeWithSessionExpired(){
		Login log = MyDrive.getInstance().getSessionByToken(token1);
		log.setLastAccess(new DateTime().minusHours(2));
		
		WriteFileService service = new WriteFileService(token1,"note_1", "Some text goes here\n");
		service.execute();
	}
	
	@Test(expected = IsLinkException.class)
	public void writeEnvironmentlLink(){
		
		 new MockUp<MyDrive>() {
			  @Mock
			  File resolveEnvironmentLink(File file) { return realLink; }
			};
		
		 WriteFileService service = new WriteFileService(token1, mockedLink.getName(), "WRITE DA LINK !"); 
		 service.execute();
	}
	
	
}