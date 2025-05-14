package com.parcheggio.parcheggio_backend.model;
import jakarta.persistence.*;
import java.util.Collection;
import java.util.Collections;

@SuppressWarnings("unused")
@Entity
@Table(name="Users")
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, unique=true)
    private String email;
    
    @Column(nullable=false)
    private String password;
    
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

}
