package org.team1.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;

@Entity
@Table(name = "doctor")
public class Doctor implements Serializable {

    private String amka;

    @NotNull
    @Pattern(regexp = "[a-zA-Z]{3,30}")
    private String firstName;

    @NotNull
    @Pattern(regexp = "[a-zA-Z]{3,30}")
    private String lastName;

    @NotNull
    @Pattern(regexp = "[a-zA-Z0-9]{3,30}")
    private String username;

    @NotNull
    private String password;

    @NotNull
    @Min(10)
    private long phone;

    @NotNull
    @Email
    private String email;

    @NotNull
    private Specialty specialty;

    public Doctor(){}

    public Doctor(String amka, String firstName, String lastName, String username, String password, long phone,
                  String email, Specialty specialty){

        this.amka = amka;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.phone = phone;
        this.email = email;
        this.specialty = specialty;
    }

    @Id
    @Column(unique = true, nullable = false)
    public String getAmka() {
        return amka;
    }
    public void setAmka(String amka) {
        this.amka = amka;
    }

    @Column(name = "first_name", nullable = false)
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Column(name = "lastName", nullable = false)
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Column(name = "username", unique = true, nullable = false)
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    @Column(name = "password", nullable = false)
    @JsonIgnore
    public String getPassword() {
        return password;
    }
    @JsonProperty
    public void setPassword(String password) {
        this.password = password;
    }

    @Column(name= "phone", nullable = false)
    public long getPhone() {
        return phone;
    }
    public void setPhone(long phone) {
        this.phone = phone;
    }

    @Column(name = "email", nullable = false)
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    @ManyToOne
    @JoinColumn(name = "specialty_id")
    public Specialty getSpecialty(){ return specialty;}
    public void setSpecialty(Specialty specialty) { this.specialty = specialty; }


    @Override
    public String toString(){
        return "Doctor{" + "amka=" + amka + ",First Name=" + firstName + ",Last Name=" + lastName + ",username=" + username +
                ",phone=" + phone + ",email=" + email + ",specialty=" + specialty + '\'' + '}';
    }
}

