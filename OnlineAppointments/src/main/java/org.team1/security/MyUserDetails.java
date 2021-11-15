package org.team1.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.team1.exceptions.UserNotFoundException;
import org.team1.models.Client;
import org.team1.models.Doctor;

import java.util.Collection;
import java.util.Collections;


public class MyUserDetails implements UserDetails {
    private Client client;
    private Doctor doctor;

    public MyUserDetails(Doctor doctor, Client client){
       this.doctor = doctor;
       this.client = client;
   }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
       if (doctor == null){
           return Collections.singletonList(new SimpleGrantedAuthority("Client"));
       }else {
           return Collections.singletonList(new SimpleGrantedAuthority("Doctor"));
       }
    }

    @Override
    public String getPassword() {
       if (doctor == null && client != null){
           return client.getPassword();
       }else if (client == null && doctor != null){
           return doctor.getPassword();
       }else {
           throw new UserNotFoundException();
       }
    }

    @Override
    public String getUsername() {
        if (doctor == null && client != null){
            return client.getUsername();
        }else if (doctor != null && client == null){
            return doctor.getUsername();
        }else {
            throw new UserNotFoundException();
        }
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}