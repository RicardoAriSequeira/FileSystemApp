package pt.tecnico.mydrive.service;

import mockit.Mock;
import mockit.MockUp;
import mockit.integration.junit4.JMockit;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.joda.time.DateTime;

import pt.tecnico.mydrive.domain.*;
import pt.tecnico.mydrive.exceptions.EnvironmentVariableDoesNotExist;
import pt.tecnico.mydrive.exceptions.FileDoesNotExistException;
import pt.tecnico.mydrive.exceptions.InvalidSessionException;
import pt.tecnico.mydrive.exceptions.IsDirectoryException;
import pt.tecnico.mydrive.exceptions.LinkLoopException;
import pt.tecnico.mydrive.exceptions.UserDoesNotHavePermissionException;


@RunWith(JMockit.class)
public class ReadFileTest extends AbstractServiceTest {

	private long token, token2;
	private Login l, l2;
	private Directory dir, dir2, userDirectory;
	
	private static Link mockedLink;
	private static Link realLink;
	
	protected void populate() {

	    MyDrive md = MyDrive.getInstance();
	
	    User user = new User(md, "magda", "12345678", "magda", "rwxdr---");
	    userDirectory = (Directory) md.getFileByPath("/home/magda");
	
	    dir = new Directory(md, user, "dir_teste_read_file", "rwxdr---", userDirectory);
	
	    new PlainFile(md, "file_teste_read_1", user, "rwxdr---", dir, "");
	    new PlainFile(md, "file_teste_read_2", user, "rwxdr---", dir, "texto_2");
	    new App(md, "app_teste_read", user, "rwxdr---", dir, "texto_app");
	    new Link(md, "link_teste_read_1",user, "rwxdr---", dir, "/home/magda/dir_teste_read_file");
	    realLink = new Link(md, "link_teste_read_2", user, "rwxdr---", dir, "/home/magda/dir_teste_read_file/file_teste_read_2");
	    new Link(md, "link_teste_read_3", user, "rwxdr---", dir, "/home/magda/dir_teste_read_file/file_teste_read_5");
	    mockedLink = new Link(md, "link_mock_teste", user, "rwxdr---", dir, "/home/$USER/dir_teste_read_file/file_teste_read_2");
	    new Link(md, "link_teste_read_101",user, "rwxdr---", dir, "/home/magda/dir_teste_read_file/link_teste_read_102");
	    new Link(md, "link_teste_read_102",user, "rwxdr---", dir, "/home/magda/dir_teste_read_file/link_teste_read_101");
	
	    User user2 = new User(md, "user", "12345678", "user", "rwdr---");
	    Directory user2Directory = (Directory) md.getFileByPath("/home/user");
	
	    dir2 = new Directory(md, user2, "dir_teste_read_file", "rwxdr---", user2Directory);
	
	    new PlainFile(md, "file_teste_read_3", user2, "rwxd----", dir2, "texto_3"); 
		
		l = new Login(md, "magda", "12345678");
	   	token = l.getToken();
	    l.setCurrentDir(dir);
	
	    l2 = new Login(md, "user", "12345678");
	   	token2 = l2.getToken();
	   	l2.setLastAccess(new DateTime().minusHours(3));
	
	    
	}
	
	@Test
	public void sucessWithNonEmptyFile(){
		ReadFileService service = new ReadFileService(token, "file_teste_read_2");
		service.execute();
		String content = service.result();
		
		assertEquals("Testing read non-empty file", 
						"texto_2", 
						content);
	}
	
	
	@Test
	public void EmptyFile(){
	    ReadFileService service = new ReadFileService(token, "file_teste_read_1"); 
	    service.execute();
	    String content = service.result();
	    
	    assertEquals("Testing read empty file", 
	                    "", 
	                    content);
	}
	
	@Test
	public void TestWithApp(){
	    ReadFileService service = new ReadFileService(token, "app_teste_read"); 
	    service.execute();
	    String content = service.result();
	
	    assertEquals("Testing app", 
	                    "texto_app", 
	                    content);
	}
	
	@Test
	public void TestWithLinkToFile(){
	    ReadFileService service = new ReadFileService(token, "link_teste_read_2"); 
	    service.execute();
	    String content = service.result();
	    
	    assertEquals("Testing link", 
	                    "texto_2", 
	                    content);
	}
	
	@Test(expected=IsDirectoryException.class )
	public void TestWithLinkToDir(){
	
	    ReadFileService service = new ReadFileService(token, "link_teste_read_1"); 
	    service.execute();
	
	}
	
	@Test(expected = FileDoesNotExistException.class)
	public void TestWithLinkToFalseFile() {
		
	   ReadFileService service = new ReadFileService(token, "link_teste_read_3"); 
	   service.execute();
	}
	
	
	@Test(expected = FileDoesNotExistException.class)
	public void InvalidFileName() {
		
	   ReadFileService service = new ReadFileService(token, "file_teste_read_0"); 
	   service.execute();
	}
	
	
	@Test(expected=UserDoesNotHavePermissionException.class)
	public void NotHavePermission(){
		l.setCurrentDir(dir2);
	    ReadFileService service = new ReadFileService(token, "file_teste_read_3"); 
	    service.execute();
	}
	
	
	@Test(expected=IsDirectoryException.class)
	public void isDirectory(){
		l.setCurrentDir(userDirectory);
	    ReadFileService service = new ReadFileService(token, "dir_teste_read_file"); 
	    service.execute();
	}
	
	@Test(expected=InvalidSessionException.class)
	public void ExpiredSession(){
		l2.setCurrentDir(dir2);
	    ReadFileService service = new ReadFileService(token2, "file_teste_read_3"); 
	    service.execute();
	}
	
	@Test(expected=LinkLoopException.class)
	public void readLinkToLink(){
		ReadFileService service = new ReadFileService(token, "link_teste_read_101"); 
	    service.execute();
	} 
	
	
	@Test
    public void readEnvironmentlLink(){
		String actual = "texto_2";
	     new MockUp<ReadFileService>() {
		 	  @Mock
			  void dispatch() throws Exception{
		 		  realLink.showContent();
		 	  }
		 	  
		 	  @Mock 
		 	  String result(){
		 		  return actual;
		 	  }
		 };
		 
		 ReadFileService service = new ReadFileService (token, mockedLink.getName()); 
		 service.execute();
		 
		 assertEquals(service.result(), actual);
	}
	
	@Test(expected=EnvironmentVariableDoesNotExist.class)
    public void readEnvironmentlLinkExeption(){
		String variable = "$USER";
		new MockUp<ReadFileService>() {
		 	  @Mock
			  void dispatch() throws Exception{
		 		  throw new EnvironmentVariableDoesNotExist(variable);
		 	  }
		 };
		 
		 ReadFileService service = new ReadFileService (token, mockedLink.getName()); 
		 service.execute();
	}
}