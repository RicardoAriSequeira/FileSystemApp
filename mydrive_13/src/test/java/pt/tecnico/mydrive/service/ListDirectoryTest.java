package pt.tecnico.mydrive.service;

import static org.junit.Assert.assertEquals;

import org.joda.time.DateTime;
import org.junit.Test;

import pt.tecnico.mydrive.domain.*;
import pt.tecnico.mydrive.exceptions.InvalidSessionException;
import pt.tecnico.mydrive.service.dto.FileDto;

import java.util.List;

public class ListDirectoryTest extends AbstractServiceTest {
    
    protected void populate() {
        
        MyDrive md = MyDrive.getInstance();

        Directory userDirectory, dir;
        User usr;
        
        usr = new User(md, "ricardo", "123456789", "Ricardo", "rwdr---");
        userDirectory = (Directory) md.getFileByPath("/home/ricardo");
        dir = new Directory(md, usr, "teste", "rwxdr---", userDirectory);
        new Directory(md, usr, "abcd", "rwxd----", dir);
        new Directory(md, usr, "efgh", "rwxd----", dir);
        new PlainFile(md, "ijlm", usr, "rwxd----", dir, "ola");
        new PlainFile(md, "nopq", usr, "rwxd----", dir, "adeus");
        
    }
    
    @Test
    public void success() {
        MyDrive md = MyDrive.getInstance();
        Login log = new Login(md,"ricardo","123456789");
        log.setCurrentDir((Directory) md.getFileByPath("/home/ricardo/teste"));
        long token = log.getToken();
        
        ListDirectoryService service = new ListDirectoryService(token);
        service.execute();
        List<FileDto> files = service.result();
        
         
         assertEquals("List with 6 files.", 6, files.size());
         assertEquals("Type of first file is: Directory.", "Directory", files.get(0).getType());
         assertEquals("Owner of second file is: ricardo.", "Ricardo", files.get(1).getOwner());
         assertEquals("Name of second file is: \'..\'.", "..", files.get(1).getName());
         assertEquals("Name of third file is: abcd.", "abcd", files.get(2).getName());
         assertEquals("Permissions of fourth file is: rwxd----.", "rwxd----", files.get(3).getPermissions());
         assertEquals("Type of fifth file is: PlainFile.", "PlainFile", files.get(4).getType());
         assertEquals("Size of sixth file is: 5.", "5", files.get(5).getSize());
           
    }
    
    @Test(expected = InvalidSessionException.class)
    public void invalidSession() {
    	MyDrive mydrive = MyDrive.getInstance();
    	Login log = new Login(mydrive,"ricardo","123456789");
    	log.setCurrentDir((Directory) mydrive.getFileByPath("/home/ricardo/teste"));
    	log.setLastAccess(new DateTime().minusHours(3));
        long token = log.getToken();
        ListDirectoryService service = new ListDirectoryService(token);
        service.execute();
    }
}