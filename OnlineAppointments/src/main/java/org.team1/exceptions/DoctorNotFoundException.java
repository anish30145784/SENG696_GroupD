package org.team1.exceptions;

public class DoctorNotFoundException extends RuntimeException {

    public DoctorNotFoundException(String id) {
        super("Could not find doctor with id " + id);
    }
}
