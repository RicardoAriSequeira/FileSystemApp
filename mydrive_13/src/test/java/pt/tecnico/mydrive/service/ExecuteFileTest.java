package pt.tecnico.mydrive.service;

import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Mock;
import mockit.MockUp;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;

import org.joda.time.DateTime;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import pt.tecnico.mydrive.domain.*;
import pt.tecnico.mydrive.exceptions.*;

@RunWith(JMockit.class)
public class ExecuteFileTest extends AbstractServiceTest {
     private long token, token2;
     private Login l, l2;
     private Directory dir, dir2, userDirectory, dirBarra;
     
    
    private Link mockedLink;
    private Link realLink;
    private PlainFile mockedFile;
    private PlainFile mockedFileWithInvalidAssociation;
    private App app;

    protected void populate() {
        
         MyDrive md = MyDrive.getInstance();
   

         User user = new User(md, "magda", "12345678", "magda", "rwxdr---");
         userDirectory = (Directory) md.getFileByPath("/home/magda");
         dirBarra = (Directory) md.getFileByPath("/");
         dir = new Directory(md, user, "dir_teste_execute_file", "rwxdr---", userDirectory);

         new PlainFile(md, "file_teste_execute_1", user, "rwxdr---", dir, "");
         new PlainFile(md, "file_teste_execute_2", user, "rwxdr---", dir, ".tecnico");
         new PlainFile(md, "file_teste_execute_4", user, "rwxdr---", dir, "pt.tecnico.mydrive.app.Hello.result bomdia\npt.tecnico.mydrive.app.Hello.result boanoite");
         app = new App(md, "app_teste_execute", user, "rwxdr---", dir, "pt.tecnico.mydrive.app.Hello.result");
         new App(md, "app_teste_execute_2", user, "rwxdr---", dirBarra, "pt.tecnico.mydrive.app.Hello.result");
         new Link(md, "link_teste_execute_1",user, "rwxdr---", dir, "/home/magda/dir_teste_execute_file");
         new Link(md, "link_teste_execute_2", user, "rwxdr---", dir, "/home/magda/dir_teste_execute_file/file_teste_execute_2");
         new Link(md, "link_teste_execute_3", user, "rwxdr---", dir, "/home/magda/dir_teste_execute_file/file_teste_execute_0");
         new Link(md, "link_teste_execute_101",user, "rwxdr---", dir, "/home/magda/dir_teste_execute_file/link_teste_execute_102");
         new Link(md, "link_teste_execute_102",user, "rwxdr---", dir, "/home/magda/dir_teste_execute_file/link_teste_execute_101");
         realLink = new Link(md, "link_teste_execute_4", user, "rwxdr---", dir, "/home/magda/dir_teste_execute_file/app_teste_execute");
         new Link(md, "link_teste_execute_5", user, "rwxdr---", dir, "/home/magda/dir_teste_execute_file/file_teste_execute_4");
         mockedLink = new Link(md, "link_mock_teste", user, "rwxdr---", dir, "/home/$USER/dir_teste_execute_file/app_teste_execute");
         mockedFile = new PlainFile(md, "file_mock_teste.pf", user, "rwxdr---", dir, "lala");
         mockedFileWithInvalidAssociation = new PlainFile(md, "file_mock_teste.error", user, "rwxdr---", dir, "lala2");
         
         User user2 = new User(md, "user", "12345678", "user", "rwdr---");
         Directory user2Directory = (Directory) md.getFileByPath("/home/user");

         dir2 = new Directory(md, user2, "dir_teste_execute_file2", "rwxdr---", user2Directory);

         new PlainFile(md, "file_teste_execute_3", user2, "rwxd----", dir2, "texto_3"); 
        
         l = new Login(md, "magda", "12345678");
            token = l.getToken();
            l.setCurrentDir(dir);

                 l2 = new Login(md, "user", "12345678");
              token2 = l2.getToken();
              l2.setLastAccess(new DateTime().minusHours(3));
    
        
    }

     @Test(expected=IsDirectoryException.class)
     public void TestWithDirectory(){

         ExecuteFileService service = new ExecuteFileService(token, "/home/magda/dir_teste_execute_file", "diretorio");
         service.execute();
     }

     @Test(expected=IsDirectoryException.class )
     public void TestWithLinkToDir(){
    

        ExecuteFileService service = new ExecuteFileService(token, "/home/magda/dir_teste_execute_file/link_teste_execute_1", "diretorio"); 
         service.execute();

     }

    @Test(expected=FileDoesNotExistException.class)
    public void TestWithLinkToFalseFile() {
        
       ExecuteFileService service = new ExecuteFileService(token, "/home/magda/dir_teste_execute_file/link_teste_execute_3", "ficheiro_nao_existe"); 
        service.execute();
    }


    @Test(expected = FileDoesNotExistException.class)
    public void InvalidFileName() {
        
       ExecuteFileService service = new ExecuteFileService(token, "/home/magda/dir_teste_execute_file/file_teste_execute_0", "ficheiro_nao_existe"); 
        service.execute();
    }

    @Test
    public void SucessWithAppWithoutArguments(){
     
		 ByteArrayOutputStream outContent = new ByteArrayOutputStream();
		System.setOut(new PrintStream(outContent));
	
         ExecuteFileService service = new ExecuteFileService(token, "/home/magda/dir_teste_execute_file/app_teste_execute", null); 
         service.execute();
         //assertTrue(service.execute());
         assertTrue(outContent.toString().contains("Wee"));
         //System.setOut(null);
     }
    
    @Test
    public void SucessWithAppWithArguments(){
     
		 ByteArrayOutputStream outContent = new ByteArrayOutputStream();
		System.setOut(new PrintStream(outContent));
	
         ExecuteFileService service = new ExecuteFileService(token, "/home/magda/dir_teste_execute_file/app_teste_execute", "ola"); 
         service.execute();
         //assertTrue(service.execute());
         assertTrue(outContent.toString().contains("ola"));
         //System.setOut(null);
     }
    
    
    
      @Test
    	public void SucessWithLinkToApp(){
     
		  ByteArrayOutputStream outContent = new ByteArrayOutputStream();
		 System.setOut(new PrintStream(outContent));
	
         ExecuteFileService service = new ExecuteFileService(token, "/home/magda/dir_teste_execute_file/link_teste_execute_4", "adeus"); 
         service.execute();
         assertTrue(outContent.toString().contains("adeus"));
          //System.setOut(null);
     }
     
      
        @Test
     	public void SucessWithLinkToPlainFile(){
     
		   ByteArrayOutputStream outContent = new ByteArrayOutputStream();
		  System.setOut(new PrintStream(outContent));
	
         ExecuteFileService service = new ExecuteFileService(token, "/home/magda/dir_teste_execute_file/link_teste_execute_5", "deve_ignorar"); 
         service.execute();
         assertTrue(outContent.toString().contains("bomdia"));
         assertTrue(outContent.toString().contains("boanoite"));
         //  System.setOut(null);
     }
       
           @Test
     	public void SucessWithPlainFile(){
     
		   ByteArrayOutputStream outContent = new ByteArrayOutputStream();
		  System.setOut(new PrintStream(outContent));
	
         ExecuteFileService service = new ExecuteFileService(token, "/home/magda/dir_teste_execute_file/file_teste_execute_4", "deve_ignorar"); 
         service.execute();
         assertTrue(outContent.toString().contains("bomdia"));
         assertTrue(outContent.toString().contains("boanoite"));
          // System.setOut(null);
     }

     @Test
        public void SucessWithPlainFileWithRelativePath(){
     
           ByteArrayOutputStream outContent = new ByteArrayOutputStream();
          System.setOut(new PrintStream(outContent));
    
         ExecuteFileService service = new ExecuteFileService(token, "file_teste_execute_4", "deve_ignorar"); 
         service.execute();
         assertTrue(outContent.toString().contains("bomdia"));
         assertTrue(outContent.toString().contains("boanoite"));
          // System.setOut(null);
     }  

     @Test
        public void SucessWithAppInBarDirectoryWithRelativePath(){
     l.setCurrentDir(dirBarra);
           ByteArrayOutputStream outContent = new ByteArrayOutputStream();
          System.setOut(new PrintStream(outContent));
    
         ExecuteFileService service = new ExecuteFileService(token, "app_teste_execute_2", "ola"); 
         service.execute();
        assertTrue(outContent.toString().contains("ola"));
          // System.setOut(null);
     } 
	
     @Test(expected = InvalidAppContentException.class)
     public void TestWithInvalidPlainFileContent() {
        l.setCurrentDir(dir);
        ExecuteFileService service = new ExecuteFileService(token, "/home/magda/dir_teste_execute_file/file_teste_execute_2", "conteudo nao e app"); 
         service.execute();
     }


     @Test(expected = InvalidAppContentException.class)
     public void TestWithLinkToInvalidPlainFileContent() {
        
       ExecuteFileService service = new ExecuteFileService(token, "/home/magda/dir_teste_execute_file/link_teste_execute_2", "conteudo nao e app"); 
         service.execute();
     }

     @Test(expected=LinkLoopException.class)
    public void TestWithLinkLoop(){
        ExecuteFileService service = new ExecuteFileService(token, "/home/magda/dir_teste_execute_file/link_teste_execute_101", "link loop"); 
        service.execute();
    }


    @Test(expected=UserDoesNotHavePermissionException.class)
    public void NotHavePermission(){
        l.setCurrentDir(dir2);
        ExecuteFileService service = new ExecuteFileService(token, "/home/user/dir_teste_execute_file2/file_teste_execute_3", "nao_tem_permissao"); 
        service.execute();
    }

    @Test(expected=InvalidSessionException.class)
    public void ExpiredSession(){
        l2.setCurrentDir(dir2);
        ExecuteFileService service = new ExecuteFileService(token2, "/home/magda/dir_teste_execute_file/file_teste_execute_3", "sessao_expirada"); 
        service.execute();
    }
    
   @Test
   public void execEnvironmentlLink() throws Exception{
	     new MockUp<ExecuteFileService>() {
		 	  @Mock
			  void dispatch() throws Exception{
		 		  realLink.exec(null);
		 	  }
		 };
		 
		 ByteArrayOutputStream outContent = new ByteArrayOutputStream();
		 System.setOut(new PrintStream(outContent));
		
		 ExecuteFileService service = new ExecuteFileService (token, mockedLink.getFullPath(), null); 
		 service.execute();
		 
		 assertTrue(outContent.toString().contains("Wee"));
	}
   
   @Test(expected=EnvironmentVariableDoesNotExist.class)
   public void execEnvironmentlLinkException() throws Exception{
	     String variable = "$USER";
	     new MockUp<ExecuteFileService>() {
		 	  @Mock
			  void dispatch() throws Exception{
		 		  throw new EnvironmentVariableDoesNotExist(variable);
		 	  }
		 };
		
		 ExecuteFileService service = new ExecuteFileService (token, mockedLink.getFullPath(), null); 
		 service.execute();
	}
   
   
   @Test
   public void executeAssociation() throws Exception{
	   String[] args = mockedFile.getFullPath().split(" ");
	   new MockUp<ExecuteFileService>(){
		   @Mock
		   void dispatch() throws Exception{
			   app.exec(args);
		   }
	   };
	   ExecuteFileService service = new ExecuteFileService(token, mockedFile.getFullPath(), args[0]); 
	   service.execute();
	   
	   new Verifications() {
           {
               app.exec(args);
           }
       };
   }
   
   @Test(expected=InvalidAssociationException.class)
   public void executeAssociationWithoutAssociation() throws Exception{
	   String[] args = mockedFileWithInvalidAssociation.getFullPath().split(" ");
	     
	   new MockUp<ExecuteFileService>(){
		   @Mock
		   void dispatch() throws Exception{
			   String[] ext = mockedFileWithInvalidAssociation.getName().split("\\.");
			   throw new InvalidAssociationException(ext[1]);
		   }
	   };
	   ExecuteFileService service = new ExecuteFileService(token, mockedFile.getFullPath(), args[0]); 
	   service.execute();
   }
}