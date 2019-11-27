package pt.tecnico.mydrive.presentation;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.FenixFramework;
import pt.tecnico.mydrive.domain.Login;
import pt.tecnico.mydrive.domain.MyDrive;
import java.io.File;
import java.io.IOException;

import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

public class MdShell extends Shell {

  public static void main(String[] args) throws Exception{
	try{
		setup(args);

	}finally{
		FenixFramework.shutdown();
	}
  }
  
  @Atomic
  public static void setup(String[] args) throws Exception{
	  MyDrive md = MyDrive.getInstance();
	  new Login(md);
	  MdShell sh = new MdShell();
    for (String s : args)
      	xmlScan(new File(s));	  
	  sh.execute();
  }
  
	@Atomic
	public static void xmlScan(File file) {
		log.trace("xmlScan" + FenixFramework.getDomainRoot());
		MyDrive mydrive = MyDrive.getInstance();
		SAXBuilder builder = new SAXBuilder();
		try {
			Document document = (Document) builder.build(file);
			mydrive.xmlImport(document.getRootElement());
		} catch (JDOMException | IOException e) {
			e.printStackTrace();
		}
	}

  public MdShell() { // add commands here
    super("MyDrive");
    new Key(this);
    new ChangeDirectory(this);
    new Execute(this);
    new Log(this);
    new Environment(this);
    new List(this);
  }
}