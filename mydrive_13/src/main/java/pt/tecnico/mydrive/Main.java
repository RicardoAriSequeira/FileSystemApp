package pt.tecnico.mydrive;

import java.io.IOException;
import java.io.PrintStream;
import java.io.File;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.FenixFramework;

import pt.tecnico.mydrive.domain.*;

public class Main {

	static final Logger log = LogManager.getRootLogger();

	public static void main(String[] args) throws IOException {

		System.out.println("*** Welcome to the MyDrive application! ***");

		try {

			setup();
			for (String s : args)
				xmlScan(new File(s));
			example();

		} finally {
			FenixFramework.shutdown();
		}
	}

	@Atomic
	public static void setup() {

		log.trace("Setup: " + FenixFramework.getDomainRoot());

		MyDrive _mydrive = MyDrive.getInstance();
		
		new Login(_mydrive);
		
		new User(_mydrive, "sequeira", "123456789", "Ricardo Sequeira", "rwxdr---");
		new User(_mydrive, "magda", "123456789", "Magda", "rwxdr---");
		new User(_mydrive, "miguel", "123456789", "Miguel", "rwxdr---");
		new User(_mydrive, "pereira", "abcdefghi", "Ricardo Pereira", "rwdxr---");
		new User(_mydrive, "jose", "abcdefghi", "Jose", "rwxdr---");
		new User(_mydrive, "diogo", "abcdefghi", "Diogo", "rwxdr---");

	}

	@Atomic
	public static void init() {
		MyDrive.getInstance().cleanup();
	}
	

	public static void xmlPrint() {
		log.trace("xmlPrint: " + FenixFramework.getDomainRoot());
		Document mydrive = MyDrive.getInstance().xmlExport();
		XMLOutputter xmlOutput = new XMLOutputter(Format.getPrettyFormat());
		try {
			xmlOutput.output(mydrive, new PrintStream(System.out));
		} catch (IOException e) {
			System.out.println(e);
		}
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

	@Atomic
	public static void example() {

		MyDrive _mydrive = MyDrive.getInstance();

		Directory base = (Directory) _mydrive.getFileByPath("/");
		Directory home = (Directory) _mydrive.getFileByPath("/home");
		Root root = (Root) _mydrive.getUserByName("root");
		Directory dir, dir1, dir2;
		PlainFile file;

		file = new PlainFile(_mydrive, "README", root, "rwxdr-x-", home, _mydrive.getListUsers());

		dir = new Directory(_mydrive, root, "usr", "rwxdr-x-", base);
		dir1 = new Directory(_mydrive, root, "local", "rwxdr-x-", dir);
		dir2 = new Directory(_mydrive, root, "bin", "rwxdr-x-", dir1);

		file.showContent();

		// _mydrive.deleteFile("/usr/local/bin");
		dir2.deleteFile();

		xmlPrint();

		// _mydrive.deleteFile("/home/README");
		file.deleteFile();

		// _mydrive.listDirectory("/home");
		home.list();

	}
}
