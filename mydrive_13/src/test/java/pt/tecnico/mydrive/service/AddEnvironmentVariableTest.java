package pt.tecnico.mydrive.service;
import org.junit.Test;

import pt.tecnico.mydrive.domain.*;
import pt.tecnico.mydrive.service.dto.VariableDto;

import static org.junit.Assert.*;

import java.util.List;


public class AddEnvironmentVariableTest extends AbstractServiceTest {
	private Login log_usr1;
	private Login log_usr2;
	private long usr1_token;
	
	public void populate(){
		MyDrive md = MyDrive.getInstance(); 

		new User(md, "istTeste", "123456789", "Ricardo", "rwxdr---");
		log_usr1 = new Login(md, "istTeste", "123456789");
		usr1_token = log_usr1.getToken();
		new User(md, "zemen", "567891011", "Jose", "rwxdr---");
		log_usr2 = new Login(md, "zemen", "567891011");
		
		log_usr1.addVariable("var1_usr1", "sample_text_ricardo_1");
		log_usr1.addVariable("var2_usr1", "sample_text_ricardo_2");
		log_usr2.addVariable("var1_usr2", "sample_text_zemen_1");
		log_usr2.addVariable("var2_usr2", "sample_text_zemen_2");
		
	}
	
	private String varContent(List<VariableDto> vars, String name){
		for( VariableDto v : vars)
			if( v.getName().equals(name)) return v.getValue();
		return null;
	}
	
	private String varContentGivenLog(Login user, String var_name){
		for( EnvironmentVariable v : user.getLoginVariablesSet())
			if( v.getName().equals(var_name)) return v.getValue();
		return null;
	}
	
	@Test
	public void successAddVariable(){
		final String var_value = "sample_text_ricardo_3";
		
		
		AddVariableService service = new AddVariableService(usr1_token, "var3_usr1", var_value);
		service.execute();
		List<VariableDto> service_vars = service.result();
		assertEquals("The new var has the correct value", varContent(service_vars, "var3_usr1"), var_value);
		assertEquals("User has the correct number of variables", 3, service_vars.size());
	}

	@Test
	public void successChangeVariable(){
		String var_value = null;
		for(EnvironmentVariable v : log_usr1.getLoginVariablesSet())
			if(v.getName().equals("var1_usr1")) var_value = v.getValue();
			
	
		final String new_var_value = "sample_text_ricardo_3";
		AddVariableService service = new AddVariableService(usr1_token, "var1_usr1", new_var_value);
		service.execute();
		List<VariableDto> service_vars = service.result();
		assertEquals("The new var has the correct value", varContent(service_vars, "var1_usr1"), new_var_value);
		assertFalse(var_value.equals(new_var_value));
		assertEquals("User has the correct number of variables", 2 , service_vars.size());
	}
	
	/*@Test
	public void successChangeVariableNullValue(){
		String var_value = null;
		for(EnvironmentVariable v : log_usr1.getLoginVariablesSet())
			if(v.getName().equals("var1_usr1")) var_value = v.getValue();
			
	
		final String new_var_value = null;
		AddVariableService service = new AddVariableService(usr1_token, "var1_usr1", new_var_value);
		service.execute();
		List<VariableDto> service_vars = service.result();
		assertEquals("The new var has the correct value", varContent(service_vars, "var1_usr1"), new_var_value);
		assertFalse(var_value.equals(new_var_value));
		assertEquals("User has the correct number of variables", 2 , service_vars.size());
	}*/
	
	@Test
	public void successDiferentLoginsHaveDiferentVars(){
		
		Login log2_usr1 = new Login(MyDrive.getInstance(), "istTeste", "123456789");
		Long usr1_token2 = log2_usr1.getToken();
		//log2_usr1.addVariable("var1_usr1", "sample_text_ricardo_1");
		//log2_usr1.addVariable("var2_usr1", "sample_text_ricardo_2");
		String new_var_value1 = "test1";
		String new_var_value2 = "test2";
		AddVariableService service1 = new AddVariableService(usr1_token2, "var1_usr1", new_var_value1);
		service1.execute();
		AddVariableService service2 = new AddVariableService(usr1_token2, "var2_usr1", new_var_value2);
		service2.execute();
		String var_value1 = varContentGivenLog(log_usr1, "var1_usr1");
		String var_value2 = varContentGivenLog(log_usr1, "var2_usr1");
		
		assertFalse(var_value1.equals(new_var_value1));
		assertFalse(var_value2.equals(new_var_value2));
		
	}
}
	
