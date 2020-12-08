package ru.timebook.orderhandler.okDeskServer.domain;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
public class Token {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private User user;

    private String tokenString;

    private Timestamp expiredAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTokenString() {
        return tokenString;
    }

    public void setTokenString(String tokenString) {
        this.tokenString = tokenString;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Timestamp getExpiredAt() {
        return expiredAt;
    }

    public void setExpiredAt(Timestamp expiredAt) {
        this.expiredAt = expiredAt;
    }

    public boolean isValid(){
        return expiredAt.after(new Timestamp(System.currentTimeMillis()));
    }
}
