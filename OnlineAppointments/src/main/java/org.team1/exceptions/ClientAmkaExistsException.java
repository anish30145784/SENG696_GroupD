package org.team1.exceptions;

public class ClientAmkaExistsException extends Exception{

    public ClientAmkaExistsException(String id) { super("Could not register with this id " + id); }

}
