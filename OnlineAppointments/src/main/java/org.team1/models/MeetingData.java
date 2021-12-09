package org.team1.models;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Table(name = "meeting_date")
@Entity
public class MeetingData implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "url", nullable = false)
    private String url;

    @Column(name = "meeting_id", nullable = false)
    private String meetingId;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "occupied")
    private Boolean occupied;
}
