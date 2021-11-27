package org.team1.models;

import lombok.Data;

import java.io.Serializable;

@Data
public class FeedbackDto implements Serializable {

    private String firstName;
    private String email;
    private String patientName;
    private String patientEmail;
    private String feedback;

}
