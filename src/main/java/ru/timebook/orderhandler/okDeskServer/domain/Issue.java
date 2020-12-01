package ru.timebook.orderhandler.okDeskServer.domain;

import javax.persistence.*;

@Entity
public class Issue {
    @Id
    @GeneratedValue
    private Long id;

    @Column(name="content", columnDefinition="TEXT")
    private String content;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
