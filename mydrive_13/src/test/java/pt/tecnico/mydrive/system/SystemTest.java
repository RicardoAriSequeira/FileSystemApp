package pt.tecnico.mydrive.system;

import org.junit.Test;

import pt.tecnico.mydrive.service.AbstractServiceTest;
import pt.tecnico.mydrive.presentation.*;
import pt.tecnico.mydrive.domain.*;

public class SystemTest extends AbstractServiceTest {

    private MdShell sh;
    protected void populate() {
        
        new Login(MyDrive.getInstance());
        
        sh = new MdShell();
      
        MyDrive md = MyDrive.getInstance();
        User usr = new User(md, "miguel","thisismypassword", "Miguel", "rwxdr---");
        User usr2 = new User(md, "husky","thisismypassword", "Husky", "rwxdr---");
        Directory dir = (Directory) md.getFileByPath("/home/miguel");
        new App(md, "application", usr, "rwxdr---", dir, "pt.tecnico.mydrive.app.Hello.result");
        new PlainFile(md, "simplefile", usr, "rwxdr---", dir, "this file has content");
        
        //Login l = new Login(MyDrive.getInstance(), "root", "***");
        //sh.updateSession(l);
    }

    @Test
    public void success() {
        new Log(sh).execute(new String[] {"miguel","thisismypassword"});

        new ChangeDirectory(sh).execute(new String[] {".."});
        new ChangeDirectory(sh).execute(new String[] {"."});
        new ChangeDirectory(sh).execute(new String[] {"/home/miguel"});
        new ChangeDirectory(sh).execute(new String[] {".."});
        new ChangeDirectory(sh).execute(new String[] {"miguel"});
        
        new List(sh).execute(new String[] {"/home"});
        new List(sh).execute(new String[] {});

        new Execute(sh).execute(new String[] {"/home/miguel/application", "print this"} );
        
        new Write(sh).execute(new String[] {"/home/miguel/simplefile","this absolute file has been updated"});
        new Write(sh).execute(new String[] {"simplefile","this relative file has been updated"});

        new Environment(sh).execute(new String[] {"CVSROOT","chumbado"}); 
        new Environment(sh).execute(new String[] {"CVSROOT","passado"});
        new Environment(sh).execute(new String[] {});

        new Log(sh).execute(new String[] {"husky", "thisismypassword"});
        
        new Key(sh).execute(new String[] {});   
        new Key(sh).execute(new String[] {"miguel"});
        new Key(sh).execute(new String[] {});
     }
}
