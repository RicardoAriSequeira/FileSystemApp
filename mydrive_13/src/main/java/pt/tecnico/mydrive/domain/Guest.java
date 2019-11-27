package pt.tecnico.mydrive.domain;

import pt.tecnico.mydrive.exceptions.IllegalPasswordChangeException;

public class Guest extends Guest_Base {
    
    public Guest(MyDrive mydrive) {
        super();
        
        setUsername("nobody");
        setName("Guest");
        setUmask("rxwdr-x-");
        setMyDrive(mydrive);
        setUp();
    }
    
    @Override
    public void setPassword(String password){
    	throw new IllegalPasswordChangeException(this.getUsername());
    }
    
}
