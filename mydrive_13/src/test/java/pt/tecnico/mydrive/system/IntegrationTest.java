package pt.tecnico.mydrive.system;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import mockit.Mock;
import mockit.MockUp;
import mockit.integration.junit4.JMockit;
import pt.tecnico.mydrive.domain.Directory;
import pt.tecnico.mydrive.domain.Link;
import pt.tecnico.mydrive.domain.MyDrive;
import pt.tecnico.mydrive.domain.PlainFile;
import pt.tecnico.mydrive.domain.Root;
import pt.tecnico.mydrive.domain.User;
import pt.tecnico.mydrive.service.AbstractServiceTest;
import pt.tecnico.mydrive.service.AddVariableService;
import pt.tecnico.mydrive.domain.App;
import pt.tecnico.mydrive.service.ChangeDirectoryService;
import pt.tecnico.mydrive.service.CreateFileService;
import pt.tecnico.mydrive.service.DeleteFileService;
import pt.tecnico.mydrive.service.ExecuteFileService;
import pt.tecnico.mydrive.service.ListDirectoryService;
import pt.tecnico.mydrive.service.LoginService;
import pt.tecnico.mydrive.service.MyDriveService;
import pt.tecnico.mydrive.service.ReadFileService;
import pt.tecnico.mydrive.service.WriteFileService;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.List;
import java.util.ArrayList;

import org.jdom2.Document;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;
import org.jdom2.output.Format;


@RunWith(JMockit.class)
public class IntegrationTest extends AbstractServiceTest {
	
	private static App app;
	
    protected void populate() { // populate mockup
    	
    	MyDrive mydrive = MyDrive.getInstance();    
        new User(mydrive, "john", "12345678", "John", "rwxdrwxd");
        User user = new User(mydrive, "bob", "abcdefgh", "bob", "rwxd----"); 
        
        Directory userDirectory = (Directory) mydrive.getFileByPath("/home/bob");
        app = new App(mydrive, "app_teste_execute", user, "rwxdr---", userDirectory, "pt.tecnico.mydrive.app.Hello.result");
        new PlainFile(mydrive, "file_mock_teste.pf", user, "rwxdr---", userDirectory, "lala");
    }

    @Test
    public void success() throws Exception {
    	
    	// LOGIN root
    	LoginService login = new LoginService("root", "***");
    	login.execute();
    
    	// LIST /home/root
    	ListDirectoryService list = new ListDirectoryService(login.result(), ".");
    	list.execute();
    	assertEquals(2, list.result().size());
    	
    	// CHANGE TO .
    	ChangeDirectoryService change = new ChangeDirectoryService(login.result(), ".");
    	change.execute();
    	
    	// CHANGE TO .. (/home)
    	change = new ChangeDirectoryService(login.result(), "..");
    	change.execute();
    	
    	// LIST /home
    	list = new ListDirectoryService(login.result(), ".");
    	list.execute();
    	assertEquals(6, list.result().size());
    	
    	// LIST /
    	list = new ListDirectoryService(login.result(), "..");
    	list.execute();
    	assertEquals(3, list.result().size());
    	
    	// CHANGE TO .
    	change = new ChangeDirectoryService(login.result(), ".");
    	change.execute();
    	
    	// CHANGE TO .. (/)
    	change = new ChangeDirectoryService(login.result(), "..");
    	change.execute();
    	
    	// LIST /home/john
    	list = new ListDirectoryService(login.result(), "/home/john");
    	list.execute();
    	assertEquals(2, list.result().size());
    	
    	// LIST /home/bob
    	list = new ListDirectoryService(login.result(), "/home/bob");
    	list.execute();
    	assertEquals(4, list.result().size());
    	
    	// LOGIN john
    	login = new LoginService("john", "12345678");
    	login.execute();
    	
    	// LIST /home/john
    	list = new ListDirectoryService(login.result(), ".");
    	list.execute();
    	assertEquals(2, list.result().size());
    	
    	// CREATE DIRECTORY ON /home/john CALLED Games
    	new CreateFileService(login.result(), "Games", "Directory").execute();
    	
    	// CREATE DIRECTORY ON /home/john CALLED Documents
    	new CreateFileService(login.result(), "Documents", "Directory").execute();
    	
    	// CREATE DIRECTORY ON /home/john CALLED Shortcuts
    	new CreateFileService(login.result(), "Shortcuts", "Directory").execute();
    	
    	// CREATE THRASH FILES ON /home/john
    	new CreateFileService(login.result(), "ThrashDirectory", "Directory").execute();
    	new CreateFileService(login.result(), "ThrashPlainFile.txt", "PlainFile", "This is a thrash plain file.").execute();
    	new CreateFileService(login.result(), "ThrashApp", "App", "String").execute();
    	new CreateFileService(login.result(), "ThrashLink", "Link", "/home/john/ThrashPlainFile.txt").execute();
    	
    	// LIST /home/john
    	list = new ListDirectoryService(login.result(), ".");
    	list.execute();
    	assertEquals(9, list.result().size());
    	
    	// DELETE THRASH FILES
    	new DeleteFileService(login.result(), "ThrashPlainFile.txt").execute();
    	new DeleteFileService(login.result(), "ThrashApp").execute();
    	new DeleteFileService(login.result(), "ThrashLink").execute();
    	new DeleteFileService(login.result(), "ThrashDirectory").execute();
    	
    	// LIST /home/john
    	list = new ListDirectoryService(login.result(), ".");
    	list.execute();
    	assertEquals(5, list.result().size());
    	
    	// CHANGE TO .. (/home/john/Documents)
    	change = new ChangeDirectoryService(login.result(), "Documents");
    	change.execute();
    	
    	// CREATE FILE ShopList.txt
    	new CreateFileService(login.result(), "ShopList.txt", "PlainFile", "").execute();
    	
    	// READ FILE ShopList.txt
    	ReadFileService file = new ReadFileService(login.result(), "ShopList.txt");
    	file.execute();
    	assertEquals("", file.result());
    	
    	// WRITE FILE milk, coffee and water ON ShopList.txt
    	new WriteFileService(login.result(), "ShopList.txt", "milk, coffee and water").execute();
    	
    	// READ FILE ShopList.txt
    	file = new ReadFileService(login.result(), "ShopList.txt");
    	file.execute();
    	assertEquals("milk, coffee and water", file.result());
    	
    	// WRITE FILE meat, fish and vegetables ON ShopList.txt
    	new WriteFileService(login.result(), "ShopList.txt", "meat, fish and vegetables").execute();
    	
    	// READ FILE ShopList.txt
    	file = new ReadFileService(login.result(), "ShopList.txt");
    	file.execute();
    	assertEquals("meat, fish and vegetables", file.result());
    	
    	// LIST /home/john/Documents
    	list = new ListDirectoryService(login.result(), ".");
    	list.execute();
    	assertEquals(3, list.result().size());
    	
    	// LOGIN john
    	login = new LoginService("bob", "abcdefgh");
    	login.execute();
    	
    	// ADD VARIABLE 
    	AddVariableService variables = new AddVariableService(login.result(), "country", "USA");
    	variables.execute();
    	assertEquals(1, variables.result().size());
    	
    	// ADD VARIABLE 
    	variables = new AddVariableService(login.result(), "drink", "Coca-Cola");
    	variables.execute();
    	assertEquals(2, variables.result().size());
    	
    	// ADD VARIABLE 
    	variables = new AddVariableService(login.result(), "car", "Ferrari");
    	variables.execute();
    	assertEquals(3, variables.result().size());
    	
    	// LOGIN bob
    	login = new LoginService("bob", "abcdefgh");
    	login.execute();
    	
    	// CREATE FILE ShopList.txt
    	new CreateFileService(login.result(), "PrivateBob.txt", "PlainFile", "This is private! But if you are root...").execute();
    	
    	// EXECUTE FILE "file_mock_teste.pf"
    	new MockUp<MyDrive>() {
		 	  @Mock
			  PlainFile execAssociation(PlainFile file) { return app; }
		 		};
		 	String arg = "/home/bob/file_mock_teste.pf";
		 	ByteArrayOutputStream outContent = new ByteArrayOutputStream();
		 	System.setOut(new PrintStream(outContent));
		 	ExecuteFileService service = new ExecuteFileService(login.result(), arg, arg); 
		 	service.execute();
		 	String newLine = System.getProperty("line.separator");
		 	assertTrue(outContent.toString().contains("Wee" + newLine + arg));
		 	
		// LOGIN nobody
    	login = new LoginService("nobody", null);
    	login.execute();
    	
    	// LOGIN root
    	login = new LoginService("root", "***");
    	login.execute();
    	
    	// CHANGE TO .. (/home/bob)
    	change = new ChangeDirectoryService(login.result(), "/home/bob");
    	change.execute();
    	
    	// READ FILE PrivateBob.txt
    	file = new ReadFileService(login.result(), "PrivateBob.txt");
    	file.execute();
    	assertEquals("This is private! But if you are root...", file.result());
    }
}