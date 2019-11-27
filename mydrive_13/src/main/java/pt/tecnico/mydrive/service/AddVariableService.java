package pt.tecnico.mydrive.service;

import java.util.ArrayList;
import java.util.List;

import pt.tecnico.mydrive.domain.EnvironmentVariable;
import pt.tecnico.mydrive.domain.Login;
import pt.tecnico.mydrive.exceptions.MyDriveException;
import pt.tecnico.mydrive.service.dto.VariableDto;

public class AddVariableService extends MyDriveService {
	
	private List<VariableDto> list;
	private long _token;
	private String varName, varValue;
	
	public AddVariableService(long token, String name, String value){
		_token = token;
		varName = name;
		varValue = value;
	}
	
	@Override
	protected void dispatch() throws MyDriveException {
		Login log = getMyDrive().getSessionByToken(_token);
		getMyDrive().refreshSession(log);
		if(varName!=null && varValue!=null){
			log.addVariable(varName, varValue);
		}
		
		list =  new ArrayList<VariableDto>();
		for(EnvironmentVariable env: log.getLoginVariablesSet()){
			list.add(new VariableDto(env.getName(), env.getValue()));
		}
	}
	
	public final List<VariableDto> result() {
        return list;
    }

}
