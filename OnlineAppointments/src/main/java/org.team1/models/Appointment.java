package org.team1.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

@Entity(name = "appointmentEntity")
@Table(name = "appointment")
public class Appointment implements Serializable {


    private Long id;

    private Client client;

    @NotNull
    private Doctor doctor;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM-dd-yyyy HH:mm")
    private Date dateTime;

    @NotNull
    private String description;

    @Nullable
    @Size(max = 250)
    private String notes;

    @Nullable
    private Integer criticality;

    @Nullable
    @Size(max = 250)
    private String status;

    @Nullable
    private String email;

    @Nullable
    private String sms;

    @Nullable
    private String breed;

    @Nullable
    private String age;

    @Nullable
    private String feedback;

    @Column(name = "id_deleted")
    private Boolean isDeleted = false;

    public Appointment() {
    }

    public Appointment(Client client, Doctor doctor, Date dateTime, String description, String notes) {
        this.client = client;
        this.doctor = doctor;
        this.dateTime = dateTime;
        this.description = description;
        this.notes = notes;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne
    @JoinColumn(name = "client_id")
    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    @ManyToOne
    @JoinColumn(name = "doctor_id")
    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    @Column(name = "datetime")
    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    @Column(name = "description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name = "notes")
    @Nullable
    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Nullable
    public Criticality getCriticality() {
        return Criticality.parse(this.criticality);
    }

    public void setCriticality(@Nullable Criticality criticality) {
        this.criticality = criticality.getValue();
    }

    @Nullable
    public String getStatus() {
        return status;
    }

    public void setStatus(@Nullable String status) {
        this.status = status;
    }

    @Nullable
    public String getEmail() {
        return email;
    }

    public void setEmail(@Nullable String email) {
        this.email = email;
    }

    @Nullable
    public String getSms() {
        return sms;
    }

    public void setSms(@Nullable String sms) {
        this.sms = sms;
    }

    @Nullable
    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(@Nullable String feedback) {
        this.feedback = feedback;
    }

    @Override
    public String toString() {
        return "Appointment{" +
                "id=" + id +
                ", client=" + client +
                ", doctor=" + doctor +
                ", dateTime=" + dateTime +
                ", description='" + description + '\'' +
                ", notes='" + notes + '\'' +
                ", criticality=" + criticality +
                ", status='" + status + '\'' +
                ", email='" + email + '\'' +
                ", sms='" + sms + '\'' +
                ", feedback='" + feedback + '\'' +
                '}';
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

    @Nullable
    public String getBreed() {
        return breed;
    }

    public void setBreed(@Nullable String breed) {
        this.breed = breed;
    }

    @Nullable
    public String getAge() {
        return age;
    }

    public void setAge(@Nullable String age) {
        this.age = age;
    }
}
