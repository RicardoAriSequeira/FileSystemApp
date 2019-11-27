package pt.tecnico.mydrive.presentation;

import pt.tecnico.mydrive.service.*;
import pt.tecnico.mydrive.service.dto.VariableDto;

public class Environment extends MdCommand {

    public Environment(Shell sh) {
    	super(sh, "env","\n\tUSAGE: env [name [value]]");
    	}
    public void execute(String[] args) {
    	
    	if(args.length > 2){
    		throw new RuntimeException("USAGE: " + name() + " [name [value]]");
    	}

    	long token = shell().getCurrentToken();

        if(args.length == 0) {
            AddVariableService avs = new AddVariableService(token, null, null);
            avs.execute();   
            for(VariableDto e: avs.result()){
            	System.out.println(e.getName() + "=" + e.getValue());
            }

        }

    	if (args.length == 1) {
	 	 	AddVariableService avs = new AddVariableService(token, args[0], null);
	 	 	avs.execute();
            for(VariableDto e: avs.result()){
            	if(e.getName().equals(args[0])){
            		System.out.println(e.getValue());
            	}
            }
		}

		else if (args.length == 2) {
	    	AddVariableService avs = new AddVariableService(token, args[0], args[1]);
	    	avs.execute();
		}



	}
}

/*
env [name [value]]
-> Cria ou altera variavel com nome name e associa lhe um valor.
-> Valor omitido: imprime valor que ja tinha sido associado
-> Nome e valor omitidos: imprime todas as variaveis separadas por =

*/