package com.backend.service;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "chat")
public class ChatMessage {

    @Id
    @GeneratedValue
    private long id;

    private String name;
    private String message;

    @Temporal(TemporalType.TIMESTAMP)
    private java.util.Date timestamp;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "{" +
                "\"id\" : " + id +
                ", \"name\": \"" + name + '"' +
                ", \"message\": \"" + message + '"' +
                ", \"date\": \"" + timestamp + '"' +
                "}";
    }
}