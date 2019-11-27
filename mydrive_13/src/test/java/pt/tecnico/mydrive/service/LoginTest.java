package pt.tecnico.mydrive.service;

import static org.junit.Assert.assertEquals;

	import org.junit.Test;

	import org.joda.time.DateTime;

	import pt.tecnico.mydrive.domain.*;
	import pt.tecnico.mydrive.exceptions.*;

public class LoginTest extends AbstractServiceTest {
	
	long token, token2;
		
	protected void populate() {
		
	    MyDrive md = MyDrive.getInstance();

	    new User(md, "magda", "123456789", "magda", "rwdr---");
	    new User(md, "user", "password", "user", "rwdr---");
	    new User(md, "user2", "123456789", "user2", "rwdr---");

	    Login log = new Login(md, "user", "password");
	    token2 = log.getToken();
	    log.setLastAccess(new DateTime().minusHours(3));

	
	}
	
	@Test
	public void sucess(){
		MyDrive md = MyDrive.getInstance();

		LoginService service = new LoginService("magda", "123456789");
		service.execute();
		long token = service.result();
				
		assertEquals("Testing with right token", 
						token, 
						md.getSessionByToken(token).getToken());
	}

	@Test
	public void sucessWithTwoUsers(){
		MyDrive md = MyDrive.getInstance();

		LoginService service = new LoginService("magda", "123456789");
		service.execute();
		long token = service.result();

		LoginService service2 = new LoginService("user2", "123456789");
		service2.execute();
		long token2 = service2.result();
				
		assertEquals("Testing with right token", 
						token2, 
						md.getSessionByToken(token2).getToken());
		
		assertEquals("Testing with right token", 
						token, 
						md.getSessionByToken(token).getToken());
	}


	@Test(expected = WrongPasswordException.class)
	public void InvalidPassword() {
	   LoginService service = new LoginService("magda", "password_errada"); 
	    service.execute();
	}

	@Test(expected = UserDoesNotExistException.class)
	public void InvalidUser() {
	   LoginService service = new LoginService("user_inexistente", "password"); 
	    service.execute();
	}

	@Test(expected = SessionNotFoundException.class)
	public void sessionExpired() {
		
		MyDrive md = MyDrive.getInstance();
	    LoginService service = new LoginService("magda", "123456789"); 
	    service.execute();
	
	    token = service.result();

	    Login fail = md.getSessionByToken(token2);
	    service.execute();

	}
	
}

