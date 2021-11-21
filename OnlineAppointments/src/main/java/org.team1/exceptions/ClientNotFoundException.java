package org.team1.exceptions;

public class ClientNotFoundException extends RuntimeException {

    public ClientNotFoundException(String id) {
        super("Could not find client with id " + id);
    }


}
