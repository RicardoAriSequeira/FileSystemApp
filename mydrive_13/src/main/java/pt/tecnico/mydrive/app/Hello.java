package pt.tecnico.mydrive.app;

public class Hello {
	
	public Hello(){}
	
	public static void result(String[] args){
		System.out.println("Wee");
		if(args!= null)
			System.out.println(args[0]);
	}
}